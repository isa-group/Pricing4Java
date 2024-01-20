package io.github.isagroup.models;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanContextManager {
    private Map<String, Object> userContext;
    private Map<String, Object> planContext;
    private Map<String, Object> usageLimitsContext;
}
