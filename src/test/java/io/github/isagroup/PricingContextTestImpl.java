package io.github.isagroup;

import java.util.Map;

public class PricingContextTestImpl extends PricingContext {

    private String path;

    private String secret;

    private Integer jwtExpiration;

    private String userPlan;

    private Map<String, Object> userContext;

    private Map<String, Object> userAuthorities;

    @Override
    public String getConfigFilePath() {
        return path;
    };

    public void setConfigFilePath(String path) {
        this.path = path;
    }

    @Override
    public String getJwtSecret() {
        return secret;
    };

    public void setJwtSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public int getJwtExpiration() {
        return jwtExpiration;
    };

    public void setJwtExpiration(Integer jwtExpiration) {
        this.jwtExpiration = jwtExpiration;
    }

    @Override
    public Map<String, Object> getUserContext() {
        return userContext;
    }

    public void setUserContext(Map<String, Object> userContext) {
        this.userContext = userContext;
    }

    @Override
    public String getUserPlan() {
        return userPlan;
    }

    public void setUserPlan(String userPlan) {
        this.userPlan = userPlan;
    }

    @Override
    public Object getUserAuthorities() {

        return this.userAuthorities;
    }

    public void setUserAuthorities(Map<String, Object> userAuthorities) {
        this.userAuthorities = userAuthorities;
    }

}