package io.github.isagroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PricingContextTestImpl extends PricingContext {

    private String path;
    private String secret;
    private Integer jwtExpiration;
    private String userPlan;
    private List<String> userAddOns;
    private Map<String, Object> userContext;

    public PricingContextTestImpl() {
        this.path = null;
        this.secret = "defualtSecret";
        this.jwtExpiration = 86400;
        this.userPlan = null;
        this.userAddOns = new ArrayList<>();
        this.userContext = null;
    }

    @Override
    public String getConfigFilePath() {
        return path;
    }

    public void setConfigFilePath(String path) {
        this.path = path;
    }

    @Override
    public String getJwtSecret() {
        return secret;
    }

    public void setJwtSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public int getJwtExpiration() {
        return jwtExpiration;
    }

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
    public List<String> getUserAddOns() {
        return userAddOns;
    }

    public void setUserAddOns(List<String> userAddOns) {
        this.userAddOns = userAddOns;
    }

    public void addUserAddOns(String userAddOn) {
        this.userAddOns.add(userAddOn);
    }

}
