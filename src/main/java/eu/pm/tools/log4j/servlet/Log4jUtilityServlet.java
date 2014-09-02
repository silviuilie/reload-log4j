package eu.pm.tools.log4j.servlet;


import eu.pm.tools.log4j.Log4jApplicationContext;
import eu.pm.tools.log4j.Log4jUtility;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Constructor;

import static eu.pm.tools.log4j.Log4jUtility.LOG4J_UTILITY_HOME;
import static eu.pm.tools.log4j.Log4jUtility.LOG4J_UTILITY_FIND_CLASS;
import static eu.pm.tools.log4j.Log4jUtility.LOG4J_UTILITY_RELOAD;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * Created by iliesilviu
 * Date: 13/02/14 / 22:14279
 * <p>
 * services {@link eu.pm.tools.log4j.Log4jUtility}.
 * </p>
 * <br/>
 *
 * @author Silviu Ilie
 */
public class Log4jUtilityServlet extends HttpServlet {

    private static final String DEFAULT_JSP_LOCATION = "/WEB-INF/jsp/";

    /**
     * jsp location.
     */
    private String jspLocation = DEFAULT_JSP_LOCATION;


    /**
     * log4j tools.
     */
    private Log4jUtility log4jUtility = new Log4jUtility();

    /**
     * handles all GET requests, if  URI is not expected redirect to home.
     *
     * @param req  request
     * @param resp response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        copyParameters(req);

        String requestName = req.getRequestURI();

        // when expected URI requested and authorized, go home.
        if (requestName.contains(LOG4J_UTILITY_HOME)
                && log4jUtility.isAuthorized(req.getSession())) {

            ServletContext context = getServletContext();
            RequestDispatcher dispatcher = context.getRequestDispatcher(jspLocation + "log4jlevelReload.jsp");
            dispatcher.forward(req, resp);
            return;

        }
        resp.sendRedirect(req.getContextPath());
    }


    /**
     * handles all POST requests, if  URI is not expected redirect to home.
     *
     * @param req  request
     * @param resp response
     * @throws ServletException
     * @throws IOException
     */

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String requestName = req.getRequestURI();

        // when expected URI requested and authorized, find class.
        if (requestName.contains(LOG4J_UTILITY_FIND_CLASS)
                && log4jUtility.isAuthorized(req.getSession())) {
            resp.getWriter().write(
                    log4jUtility.log4jFindClass(req.getParameter("name"), req.getSession())
            );
        }

        // when expected URI requested and authorized, set priority
        if (requestName.contains(LOG4J_UTILITY_RELOAD)
                && log4jUtility.isAuthorized(req.getSession())) {
            resp.getWriter().write(
                    log4jUtility.setPriority(
                            req.getParameter("target"),
                            req.getParameter("priority"),
                            req.getSession())
            );
        }
    }


    /**
     * handle configuration.
     *
     * @param config container provided.
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        ServletContext ctx = config.getServletContext();

        final String jspLocation = ctx.getInitParameter(Log4jUtility.LOG4J_UTILITY_JSP_LOCATION_ATTR_NAME);
        if (jspLocation == null) {
            log.warn("log4j-jsp-location parameter not found, using default " + DEFAULT_JSP_LOCATION);
        } else {
            this.jspLocation = jspLocation;
        }

        final String packageName = ctx.getInitParameter(Log4jUtility.LOG4J_UTILITY_PACKAGE_ATTR_NAME);
        if (packageName == null) {
            throw new RuntimeException("log4j-base-packageName parameter is required.");
        }

        final String authorizationClassName = ctx.getInitParameter(Log4jUtility.LOG4J_UTILITY_AUTH_ATTR_NAME);
        if (authorizationClassName == null) {
            log.warn("log4j-authorization-class parameter not found, using default");
        }

        Log4jApplicationContext.ReloadAuthorization log4jUtilityAuthorization;

        if (isNotEmpty(authorizationClassName)) {
            try {

                @SuppressWarnings("unchecked")
                Class<Log4jApplicationContext.ReloadAuthorization> authorizationClass =
                        (Class<Log4jApplicationContext.ReloadAuthorization>)
                                Class.forName(authorizationClassName);

                Constructor<Log4jApplicationContext.ReloadAuthorization> authClassConstructor =
                        authorizationClass.getDeclaredConstructor();
                log4jUtilityAuthorization = authClassConstructor.newInstance();

            } catch (Throwable e) {
                log.warn(authorizationClassName + " not found.using default : "
                        + Log4jApplicationContext.DEFAULT_AUTHORIZATION);
                log.error(authorizationClassName + " not found", e);
                log4jUtilityAuthorization = Log4jApplicationContext.DEFAULT_AUTHORIZATION;
            }

        } else {
            log.warn("using default authorization " + Log4jApplicationContext.DEFAULT_AUTHORIZATION);
            log4jUtilityAuthorization = Log4jApplicationContext.DEFAULT_AUTHORIZATION;
        }

        log4jUtility.setApplicationContext(new Log4jApplicationContext(packageName, log4jUtilityAuthorization));

        if (log.isDebugEnabled()) {
            log.debug("=====================================");
            log.debug("log4j reloader available as /" + Log4jUtility.LOG4J_UTILITY_HOME);
            log.debug("=====================================");
        }

    }

    private void copyParameters(HttpServletRequest req) {
        ServletContext ctx = req.getSession().getServletContext();
        req.setAttribute(Log4jUtility.LOG4J_UTILITY_HOME, ctx.getInitParameter(Log4jUtility.LOG4J_UTILITY_HOME));
        req.setAttribute(Log4jUtility.LOG4J_UTILITY_FIND_CLASS, ctx.getInitParameter(Log4jUtility.LOG4J_UTILITY_FIND_CLASS));
        req.setAttribute(Log4jUtility.LOG4J_UTILITY_RELOAD, ctx.getInitParameter(Log4jUtility.LOG4J_UTILITY_RELOAD));
    }


    /**
     * log
     */
    private Log log = LogFactory.getLog(this.getClass());
}
