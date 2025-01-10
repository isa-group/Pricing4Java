package io.github.isagroup;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.isagroup.services.jwt.PricingJwtUtils;

import static org.junit.jupiter.api.Assertions.*;

public class PricingEvaluatorUtilTests {

    private static final String JWT_SECRET_TEST = "secret";
    private static final Integer JWT_EXPIRATION_TEST = 86400;
    private static final String JWT_SUBJECT_TEST = "admin1";
    private static final String JWT_EXPRESSION_TEST = "userContext['pets']*4 < planContext['maxPets']";

    private static final String USER_PLAN = "ADVANCED";
    private static final String YAML_CONFIG_PATH = "pricing/petclinic.yml";

    private PricingContext pricingContext;

    private PricingEvaluatorUtil pricingEvaluatorUtil;

    private PricingJwtUtils jwtUtils;

    @BeforeEach
    public void setUp() {

        Map<String, Object> userContext = new HashMap<>();
        userContext.put("username", JWT_SUBJECT_TEST);
        userContext.put("pets", 2);

        PricingContextTestImpl pricingContext = new PricingContextTestImpl();

        pricingContext.setJwtExpiration(JWT_EXPIRATION_TEST);
        pricingContext.setJwtSecret(JWT_SECRET_TEST);
        pricingContext.setUserContext(userContext);
        pricingContext.setUserPlan(USER_PLAN);
        pricingContext.setConfigFilePath(YAML_CONFIG_PATH);

        this.pricingContext = pricingContext;
        this.pricingEvaluatorUtil = new PricingEvaluatorUtil(pricingContext);
        this.jwtUtils = new PricingJwtUtils(pricingContext);
    }

    @Test
    void simpleTokenGenerationTest() {

        String token = pricingEvaluatorUtil.generateUserToken();

        Map<String, Map<String, Object>> features = jwtUtils.getFeaturesFromJwtToken(token);

        assertTrue(jwtUtils.validateJwtToken(token), "Token is not valid");
        assertTrue((Boolean) features.get("maxPets").get("eval"), "Features is not a string");
        assertFalse((Boolean) features.get("maxVisitsPerMonthAndPet").get("eval"), "Features is not a string");
        assertTrue((Boolean) features.get("haveCalendar").get("eval"),
                "haveCalendar evaluation should be true");
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
        assertEquals((Integer) pricingContext.getUserContext().get("pets"),
                (Integer) tokenicedUserContext.get("pets"),
                "UserContext pets value is not the same after token codification");
        assertEquals((Boolean) pricingContext.getUserContext().get("havePetsDashboard"),
                (Boolean) tokenicedUserContext.get("havePetsDashboard"),
                "UserContext havePetsDashboard value is not the same after token codification");

    }
}
