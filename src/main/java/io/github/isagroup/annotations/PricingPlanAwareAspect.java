package io.github.isagroup.annotations;

import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.isagroup.PricingContext;
import io.github.isagroup.exceptions.PricingPlanEvaluationException;
import io.github.isagroup.models.Feature;
import io.github.isagroup.models.PlanContextManager;

@Aspect
@Component
public class PricingPlanAwareAspect {

    @Autowired
    private PricingContext pricingContext;

    @Around("@annotation(pricingPlanAware)")
    @Transactional(rollbackFor = PricingPlanEvaluationException.class)
    public Object validatePricingPlan(ProceedingJoinPoint joinPoint, PricingPlanAware pricingPlanAware)
            throws Throwable, PricingPlanEvaluationException {

        Object proceed = joinPoint.proceed();

        String featureId = pricingPlanAware.featureId();

        // Realizar la evaluación del contexto utilizando el valor de "featureId"
        Boolean contextEvaluation = evaluateContext(featureId);

        if (contextEvaluation == null) {
            contextEvaluation = false;
        }

        if (!contextEvaluation) {
            throw new PricingPlanEvaluationException("You have reached the limit of the feature: " + featureId);
        }

        return proceed;
    }

    private Boolean evaluateContext(String featureId) {

        Map<String, Object> userContext = pricingContext.getUserContext();
        Map<String, Object> planContext = pricingContext.getPlanContext();
        Map<String, Feature> evaluationContext = pricingContext.getFeatures();

        PlanContextManager planContextManager = new PlanContextManager();

        planContextManager.setUserContext(userContext);
        planContextManager.setPlanContext(planContext);

        try {
            String expression = evaluationContext.get(featureId).getServerExpression();

            if (expression == null) {
                expression = evaluationContext.get(featureId).getExpression();
            }

            if (!expression.trim().equals("")) {

                ExpressionParser parser = new SpelExpressionParser();
                EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();

                return parser.parseExpression(expression).getValue(context, planContextManager, Boolean.class);
            } else {
                return false;
            }
        } catch (NullPointerException e) {
            throw new PricingPlanEvaluationException(
                    "The feature " + featureId + " does not exist in the current pricing configuration");
        }

    }
}