package eu.pm.tools.log4j.servlet;

import eu.pm.tools.log4j.Log4jUtility;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static org.mockito.Mockito.*;

/**
 * Created by silviu
 * Date: 01/03/14 / 22:21562
 * <p/>
 * <p>
 * {@link Log4jUtilityServlet} test.
 * </p>
 * <p/>
 * <br/>
 * log4j-level-reloader|eu.pm.tools.log4j
 *
 * @author Silviu Ilie
 */
public class Log4jUtilityServletTest {

    Log4jUtilityServlet servlet = new Log4jUtilityServlet();

    Log4jUtility mockLog4jUtility = mock(Log4jUtility.class);
    ServletConfig mockServletConfig = mock(ServletConfig.class);
    ServletContext ctx = mock(ServletContext.class);

    HttpSession mockSession = mock(HttpSession.class);
    HttpServletRequest reqMock = mock(HttpServletRequest.class);
    HttpServletResponse respMock = mock(HttpServletResponse.class);


    @Before
    public void init() throws ServletException {
        reset(mockLog4jUtility);
        reset(mockServletConfig);
        reset(ctx);
        reset(mockSession);
        reset(reqMock);
        reset(respMock);

        when(mockServletConfig.getServletContext()).thenReturn(ctx);

        when(ctx.getInitParameter(Log4jUtility.LOG4J_UTILITY_PACKAGE_ATTR_NAME))
                .thenReturn(Log4jUtility.LOG4J_UTILITY_PACKAGE_ATTR_NAME + ".test");

        when(ctx.getInitParameter(Log4jUtility.LOG4J_UTILITY_HOME_URL))
                .thenReturn(Log4jUtility.LOG4J_UTILITY_HOME_URL + ".test");
        when(ctx.getInitParameter(Log4jUtility.LOG4J_UTILITY_JSP_LOCATION_ATTR_NAME))
                .thenReturn(Log4jUtility.LOG4J_UTILITY_JSP_LOCATION_ATTR_NAME + ".test");
        when(ctx.getInitParameter(Log4jUtility.LOG4J_UTILITY_AUTH_ATTR_NAME))
                .thenReturn(Log4jUtility.LOG4J_UTILITY_AUTH_ATTR_NAME + ".test");

        when(reqMock.getSession()).thenReturn(mockSession);
        when(reqMock.getRequestURI()).thenReturn(Log4jUtility.LOG4J_UTILITY_HOME_URL);
        when(mockSession.getServletContext()).thenReturn(ctx);

        servlet.setLog4jUtility(mockLog4jUtility);
        servlet.init(mockServletConfig);
    }

    @Test
    public void doGetNotAuthorized() throws ServletException, IOException {

        servlet.doGet(reqMock, respMock);

        verify(mockLog4jUtility, times(1)).isAuthorized(mockSession);
        verify(respMock, times(1)).sendRedirect(null);
    }

    @Test
    public void doGetAuthorized() throws ServletException, IOException {
        RequestDispatcher requestDispatcherMock = mock(RequestDispatcher.class);

        when(ctx.getRequestDispatcher(Log4jUtility.LOG4J_UTILITY_JSP_LOCATION_ATTR_NAME + ".test"
                + "log4jlevelReload.jsp")).thenReturn(requestDispatcherMock);

        when(reqMock.getSession()).thenReturn(mockSession);
        when(reqMock.getRequestURI()).thenReturn(Log4jUtility.LOG4J_UTILITY_HOME_URL);
        when(mockLog4jUtility.isAuthorized(mockSession)).thenReturn(true);

        servlet.doGet(reqMock, respMock);

        verify(requestDispatcherMock, times(1)).forward(reqMock, respMock);
    }
}
