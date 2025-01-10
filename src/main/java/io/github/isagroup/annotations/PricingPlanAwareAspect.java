package io.github.isagroup.annotations;

import java.util.Map;
import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.isagroup.PricingContext;
import io.github.isagroup.exceptions.PricingPlanEvaluationException;
import io.github.isagroup.models.Feature;
import io.github.isagroup.models.FeatureStatus;
import io.github.isagroup.models.PlanContextManager;
import io.github.isagroup.models.PricingManager;

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

        String featureId = pricingPlanAware.featureName();

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

    private Boolean evaluateContext(String featureName) {

        PlanContextManager planContextManager = new PlanContextManager();
        
        try{
            planContextManager.setUserContext(pricingContext.getUserContext());
            planContextManager.setPlanContext(pricingContext.getPlanContext());
        }catch(NullPointerException e){
            throw new PricingPlanEvaluationException("The pricing context is null. Please, chech the path to the configuration file.");
        }

        PricingManager pricingManager = pricingContext.getPricingManager();

        Map<String, Feature> features = pricingManager.getFeatures();
        Feature feature = features.get(featureName);

        if (feature == null) {
            throw new PricingPlanEvaluationException("The feature " + featureName + " does not exist in the current pricing configuration");
        }

        FeatureStatus featureStatus = new FeatureStatus();

        String expression;

        if (feature.getServerExpression() != null) {
            expression = feature.getServerExpression();
        } else {
            expression = feature.getExpression();
        }

        Boolean eval = FeatureStatus.computeFeatureEvaluation(expression, planContextManager)
                .orElseThrow(() -> new PricingPlanEvaluationException("Evaluation was null"));
        featureStatus.setEval(eval);

        Optional<String> userContextKey = FeatureStatus.computeUserContextVariable(expression);

        if (!userContextKey.isPresent()) {
            featureStatus.setUsed(null);
            featureStatus.setLimit(null);
        } else {
            featureStatus.setUsed(planContextManager.getUserContext().get(userContextKey.get()));
            featureStatus.setLimit(planContextManager.getPlanContext().get(featureName));
        }

        return featureStatus.getEval();

        // Map<String, Object> userContext = pricingContext.getUserContext();
        // Map<String, Object> planContext = pricingContext.getPlanContext();
        // Map<String, Feature> evaluationContext = pricingContext.getFeatures();

        // PlanContextManager planContextManager = new PlanContextManager();

        // planContextManager.setUserContext(userContext);
        // planContextManager.setPlanContext(planContext);

        // try {
        //     String expression = evaluationContext.get(featureId).getServerExpression();

        //     if (expression == null) {
        //         expression = evaluationContext.get(featureId).getExpression();
        //     }

        //     if (!expression.trim().equals("")) {

        //         ExpressionParser parser = new SpelExpressionParser();
        //         EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();

        //         return parser.parseExpression(expression).getValue(context, planContextManager, Boolean.class);
        //     } else {
        //         return false;
        //     }
        // } catch (NullPointerException e) {
        //     throw new PricingPlanEvaluationException(
        //             "The feature " + featureId + " does not exist in the current pricing configuration");
        // }

    }
}