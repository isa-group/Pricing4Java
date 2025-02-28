package io.github.isagroup.models;

import java.util.Map;

public class PlanContextManager {
    private Map<String, Object> userContext;
    private Map<String, Object> planContext;
    private Map<String, Object> usageLimitsContext;

    public Map<String, Object> getUserContext() {
        return userContext;
    }

    public void setUserContext(Map<String, Object> userContext) {
        this.userContext = userContext;
    }

    public Map<String, Object> getPlanContext() {
        return planContext;
    }

    public void setPlanContext(Map<String, Object> planContext) {
        this.planContext = planContext;
    }

    public Map<String, Object> getUsageLimitsContext() {
        return usageLimitsContext;
    }

    public void setUsageLimitsContext(Map<String, Object> usageLimitsContext) {
        this.usageLimitsContext = usageLimitsContext;
    }

}
