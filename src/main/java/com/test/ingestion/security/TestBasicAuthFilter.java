/**
 * 
 */
package com.test.ingestion.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.Test.Test.core.context.Context;
import com.Test.Test.core.context.ExecutionContext;
import com.Test.Test.core.context.LoggingContext;
import com.Test.Test.core.context.PlatformProperties;
import com.Test.Test.core.context.SearchContext;
import com.Test.Test.core.context.UserContext;
import com.Test.Test.entitymanagement.service.UserDetailsImpl;
import com.Test.Test.security.SecurityHelper;
import com.Test.Test.security.web.http.TestRememberMeManager;
import com.Test.Test.security.web.http.filter.BasicAuthFilter;
import com.Test.Test.util.TestLogger;
import com.Test.Test.util.StringUtil;

/**
 * @author Kaniska_Mandal
 *
 */
public class TestBasicAuthFilter extends BasicAuthenticationFilter {

	 private static final TestLogger logger = new TestLogger(testBasicAuthFilter.class);

	 @Override
		protected void onSuccessfulAuthentication(HttpServletRequest request,
				HttpServletResponse response, Authentication authResult)
				throws IOException {
			UserDetailsImpl user = (UserDetailsImpl) authResult.getPrincipal();
			HttpSession session = request.getSession(true);
			if (session != null) {
				session.setAttribute("tenantId", user.getTenantId());
				session.setAttribute("userId",user.getUserId());
				if(logger.isDebugEnabled()) {
					logger.debug("BasicAuthenticationFilter: TenantId, UserId set in userSession : " 
							+ session.getAttribute("tenantId") + ", "  + session.getAttribute("userId"));
				}
				    UserDetailsImpl userDetails = SecurityHelper.getAuthenticationObjFromSession();

					UserContext userContext = createUserContext(userDetails);
					if (userContext != null) {
						ExecutionContext.associateContext(Context.USER_CONTEXT,userContext);

						// Associate UserContext into http session for later fetching via jsp
						//HttpSession session = ((HttpServletRequest) request).getSession(true);
						
						// check if user context session variable is set, if so, overwrite 
						if (null != session.getAttribute("UserCxt")) {
							session.setAttribute("UserCxt", userContext);
						} else {
							session.setAttribute("UserCxt", userContext);
						}
					}
			}
		}
	 
	 @Override
	  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
	            throws IOException, ServletException {
		 try{
			 super.doFilter(req, res, chain);
		 }finally {
				ExecutionContext.clearContext();
				logger.debug("ExecutionContextHolder is"
						+ " now cleared, as request processing completed");
			}
	 }

		private UserContext createUserContext(UserDetailsImpl userDetails) {
			 UserContext userContext = null;
			 if(null != userDetails) {
			     int tenantId = userDetails.getTenantId();
			     int userId = userDetails.getUserId();
			     if (tenantId > 0 && userId > 0) {
				     userContext = new UserContext(tenantId, userId);
				     userContext.setLoginId(userDetails.getUsername());
				     userContext.setRoles(userDetails.getAuthorities());
				     userContext.setAppRoleNameMap(userDetails.getAppIdToRoleIdMapForUser());
				     userContext.setEmail(userDetails.getEmail());
				     userContext.setFirstName(userDetails.getFirstName());
				     userContext.setLastName(userDetails.getLastName());
				 } else {
				     logger.error("Invalid tenant id/user id : ", tenantId, userId);
				 }
			 }
			 return userContext;
		}
		
}
