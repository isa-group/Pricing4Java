package io.github.isagroup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.github.isagroup.services.jwt.JwtUtils;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Import({ io.github.isagroup.PricingEvaluatorUtil.class, io.github.isagroup.services.jwt.JwtUtils.class })
public class PricingEvaluatorUtilTests {

    private static final String JWT_SECRET_TEST = "secret";
    private static final Integer JWT_EXPIRATION_TEST = 86400;
    private static final String JWT_SUBJECT_TEST = "admin1";
    private static final String JWT_EXPRESSION_TEST = "userContext['pets']*4 < planContext['maxPets']";

    @Configuration
    public static class TestConfiguration {

        @Component
        public class PricingContextImpl extends PricingContext {

            @Override
            public String getConfigFilePath() {
                return "yaml-testing/petclinic.yml";
            };

            @Override
            public String getJwtSecret() {
                return JWT_SECRET_TEST;
            };

            @Override
            public int getJwtExpiration() {
                return JWT_EXPIRATION_TEST;
            };

            @Override
            public Map<String, Object> getUserContext() {
                Map<String, Object> userContext = new HashMap<>();

                userContext.put("username", JWT_SUBJECT_TEST);
                userContext.put("pets", 2);
                userContext.put("haveVetSelection", true);
                userContext.put("haveCalendar", true);
                userContext.put("havePetsDashboard", true);
                userContext.put("haveOnlineConsultations", true);

                return userContext;
            }

            @Override
            public String getUserPlan() {
                return "ADVANCED";
            }

            @Override
            public Object getUserAuthorities() {
                Map<String, String> userAuthorities = new HashMap<>();
                userAuthorities.put("role", "admin");
                userAuthorities.put("username", "admin1");
                userAuthorities.put("password", "4dm1n");

                return userAuthorities;
            }

        }

    }

    @Autowired
    private PricingContext pricingContext;

    @Autowired
    private PricingEvaluatorUtil pricingEvaluatorUtil;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void simpleTokenGenerationTest() {

        String token = pricingEvaluatorUtil.generateUserToken();

        Map<String, Map<String, Object>> features = jwtUtils.getFeaturesFromJwtToken(token);

        assertTrue(jwtUtils.validateJwtToken(token), "Token is not valid");
        assertTrue((Boolean) features.get("maxPets").get("eval"), "Features is not a string");
        assertFalse((Boolean) features.get("maxVisitsPerMonthAndPet").get("eval"), "Features is not a string");
        assertTrue((Boolean) features.get("haveCalendar").get("eval"), "haveCalendar evaluation should be true");
        assertFalse((Boolean) features.get("haveOnlineConsultation").get("eval"),
                "haveVetSelection evaluation should be false");

    }

    @Test
    void checkTokenSubjectTest() {

        String token = pricingEvaluatorUtil.generateUserToken();

        String jwtSubject = jwtUtils.getSubjectFromJwtToken(token);

        assertTrue(jwtUtils.validateJwtToken(token), "Token is not valid");
        assertEquals(JWT_SUBJECT_TEST, jwtSubject, "The subject has not being correctly set");

    }

    // @Test
    // void checkTokenTimeoutTest() {

    // String token = pricingEvaluatorUtil.generateUserToken();

    // assertTrue(jwtUtils.validateJwtToken(token), "Token is not valid");

    // try{
    // Thread.sleep(1500);
    // }catch(InterruptedException e){
    // }

    // assertFalse(jwtUtils.validateJwtToken(token), "Token is still being valid
    // after timeout has passed");

    // }

    @Test
    void tokenExpressionsTest() {

        String firstToken = pricingEvaluatorUtil.generateUserToken();

        String newToken = pricingEvaluatorUtil.addExpressionToToken(firstToken, "maxVisitsPerMonthAndPet",
                JWT_EXPRESSION_TEST);

        Map<String, Map<String, Object>> features = jwtUtils.getFeaturesFromJwtToken(newToken);

        assertTrue(jwtUtils.validateJwtToken(newToken), "Token is not valid");
        assertEquals(JWT_EXPRESSION_TEST, (String) features.get("maxVisitsPerMonthAndPet").get("eval"),
                "The expression for the feature maxVisitsPerMonthAndPet has not being correctly set");

    }

    @Test
    void tokenPlanContextTest() {

        String token = pricingEvaluatorUtil.generateUserToken();

        Map<String, Object> tokenicedPlanContext = jwtUtils.getPlanContextFromJwtToken(token);

        assertTrue(jwtUtils.validateJwtToken(token), "Token is not valid");
        assertEquals((Integer) pricingContext.getPlanContext().get("maxPets"),
                (Integer) tokenicedPlanContext.get("maxPets"),
                "PlanContext maxPets value is not the same after token codification");
        assertEquals((Boolean) pricingContext.getPlanContext().get("havePetsDashboard"),
                (Boolean) tokenicedPlanContext.get("havePetsDashboard"),
                "PlanContext havePetsDashboard value is not the same after token codification");
        assertEquals((String) pricingContext.getPlanContext().get("supportPriority"),
                (String) tokenicedPlanContext.get("supportPriority"),
                "PlanContext havePetsDashboard value is not the same after token codification");

    }

    @Test
    void tokenUserContextTest() {

        String token = pricingEvaluatorUtil.generateUserToken();

        Map<String, Object> tokenicedUserContext = jwtUtils.getUserContextFromJwtToken(token);

        assertTrue(jwtUtils.validateJwtToken(token), "Token is not valid");
        assertEquals((Integer) pricingContext.getUserContext().get("pets"), (Integer) tokenicedUserContext.get("pets"),
                "UserContext pets value is not the same after token codification");
        assertEquals((Boolean) pricingContext.getUserContext().get("havePetsDashboard"),
                (Boolean) tokenicedUserContext.get("havePetsDashboard"),
                "UserContext havePetsDashboard value is not the same after token codification");

    }
}
