package io.github.isagroup.filters;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.beans.factory.annotation.Value;

import io.github.isagroup.PricingContext;
import io.github.isagroup.PricingEvaluatorUtil;
import io.github.isagroup.services.jwt.PricingJwtUtils;

public class RenewTokenFilter extends OncePerRequestFilter {

	@Autowired
	private PricingJwtUtils jwtUtils;

	@Value("${petclinic.app.jwtSecret}")
	private String jwtSecret;

	@Autowired
	private PricingEvaluatorUtil pricingEvaluatorUtil;

	@Autowired
	private PricingContext pricingContext;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String pricingJwt = parsePricingJwt(request);
		String authJwt = parseAuthJwt(request);

		if (authJwt != null && jwtUtils.validateJwtToken(authJwt) && pricingContext.userAffectedByPricing()) {
			
			String newToken = pricingEvaluatorUtil.generateUserToken();

			String newTokenFeatures = jwtUtils.getFeaturesFromJwtToken(newToken).toString();
			String jwtFeatures = "";
			
			if (pricingJwt != null && !pricingJwt.equals("null")) jwtFeatures = jwtUtils.getFeaturesFromJwtToken(pricingJwt).toString();
			
			if (!newTokenFeatures.equals(jwtFeatures)) {
				response.addHeader("Pricing-Token", newToken);
			}
			
		}

		filterChain.doFilter(request, response);
	}

	private String parsePricingJwt(HttpServletRequest request) {
		String headerPricing = request.getHeader("Pricing-Token");

		if (StringUtils.hasText(headerPricing)) {
			return headerPricing;
		}

		return null;
	}

	private String parseAuthJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");

		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7, headerAuth.length());
		}

		return null;
	}

}

