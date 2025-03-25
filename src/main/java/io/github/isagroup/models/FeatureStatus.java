package io.github.isagroup.models;

import java.util.Optional;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;

public class FeatureStatus {

    private Boolean eval;
    private Object used;
    private Object limit;

    public Boolean getEval() {
        return eval;
    }

    public void setEval(Boolean eval) {
        this.eval = eval;
    }

    public Object getUsed() {
        return used;
    }

    public void setUsed(Object used) {
        this.used = used;
    }

    public Object getLimit() {
        return limit;
    }

    public void setLimit(Object limit) {
        this.limit = limit;
    }

    public static Optional<Boolean> computeFeatureEvaluation(String expression, PlanContextManager planContextManager) {
        ExpressionParser expressionParser = new SpelExpressionParser();
        EvaluationContext evaluationContext = SimpleEvaluationContext.forReadOnlyDataBinding().build();

        if (expression.trim().isEmpty()) {
            return Optional.of(false);
        }

        return Optional.ofNullable(expressionParser.parseExpression(expression).getValue(evaluationContext,
                planContextManager,
                Boolean.class));

    }

    public static Optional<String> computeUserContextVariable(String expression) {

        if (!expression.contains("<") && !expression.contains(">")) {
            return Optional.ofNullable(null);

        }
        // TODO REFACTOR: Gets userContext Key string in two steps
        // APPLY A REGEX 2 TIMES
        return Optional.ofNullable(expression.split("\\[[\\\"|']")[1].split("[\\\"|']\\]")[0].trim());
    }

}
