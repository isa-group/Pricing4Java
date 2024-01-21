package io.github.isagroup.models;

import java.util.Optional;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeatureStatus {

    private Object eval;
    private Object used;
    private Object limit;

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
