package eu.pm.tools.log4j.servlet;

import eu.pm.tools.log4j.Log4jApplicationContext;
import eu.pm.tools.log4j.Log4jUtility;
import eu.pm.tools.log4j.ReloadAuthorization;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

import static eu.pm.tools.log4j.Log4jUtility.LOG4J_UTILITY_FIND_CLASS_URL;
import static eu.pm.tools.log4j.Log4jUtility.LOG4J_UTILITY_RELOAD_URL;
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


    public class ClassToFindByName {

    }

    ReloadAuthorization mockAuth =  mock(ReloadAuthorization.class);

    @Before
    public void init() throws ServletException {
        reset(mockAuth, mockLog4jUtility, mockServletConfig, ctx, mockSession, reqMock, respMock);

        when(mockServletConfig.getServletContext()).thenReturn(ctx);

        when(ctx.getInitParameter(Log4jUtility.LOG4J_UTILITY_PACKAGE_ATTR_NAME))
                .thenReturn("eu");

        when(ctx.getInitParameter(Log4jUtility.LOG4J_UTILITY_HOME_URL))
                .thenReturn(Log4jUtility.LOG4J_UTILITY_HOME_URL + ".test");

        when(ctx.getInitParameter(Log4jUtility.LOG4J_UTILITY_JSP_LOCATION_ATTR_NAME))
                .thenReturn(Log4jUtility.LOG4J_UTILITY_JSP_LOCATION_ATTR_NAME + ".test");


        when(ctx.getInitParameter(Log4jUtility.LOG4J_UTILITY_AUTH_ATTR_NAME))
                .thenReturn(mockAuth.getClass().getSimpleName());


        when(reqMock.getSession()).thenReturn(mockSession);
        when(reqMock.getRequestURI()).thenReturn(Log4jUtility.LOG4J_UTILITY_HOME_URL);
        when(mockSession.getServletContext()).thenReturn(ctx);

        servlet.setLog4jUtility(mockLog4jUtility);
        servlet.init(mockServletConfig);
    }

    @After
    public void validate() {
//        validateMockitoUsage();
    }

    @Test
    public void doPostEmpty() throws ServletException, IOException {
        servlet.doPost(reqMock, respMock);
    }


    @Test
    public void doPostFindClass() throws ServletException, IOException {
        String classNameFragment = "ToFindBy";
        PrintWriter mockWriter = mock(PrintWriter.class);

        when(reqMock.getSession()).thenReturn(mockSession);
        when(reqMock.getParameter("name")).thenReturn(classNameFragment);
        when(reqMock.getRequestURI()).thenReturn(LOG4J_UTILITY_FIND_CLASS_URL);
        when(mockLog4jUtility.isAuthorized(mockSession)).thenReturn(true);
        when(mockLog4jUtility.log4jFindClass(classNameFragment, mockSession)).thenReturn(classNameFragment);
        when(respMock.getWriter()).thenReturn(mockWriter);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return null;
            }
        }).when(mockWriter).write(classNameFragment);

        servlet.doPost(reqMock, respMock);

        verify(mockWriter, times(1)).write(classNameFragment);
        verify(reqMock, times(2)).getSession();
        verify(reqMock, times(1)).getParameter("name");
        verify(reqMock, times(1)).getRequestURI();
        verify(mockLog4jUtility, times(1)).isAuthorized(mockSession);
        verify(respMock, times(1)).getWriter();
    }

    @Test
    public void doPostReloadClassPriority() throws ServletException, IOException {
        String className = "ToFindBy";
        String priority = "DEBUG";
        PrintWriter mockWriter = mock(PrintWriter.class);

        when(reqMock.getSession()).thenReturn(mockSession);
        when(reqMock.getParameter("target")).thenReturn(className);
        when(reqMock.getParameter("priority")).thenReturn(priority);
        when(reqMock.getRequestURI()).thenReturn(LOG4J_UTILITY_RELOAD_URL);
        when(mockLog4jUtility.isAuthorized(mockSession)).thenReturn(true);
        when(mockLog4jUtility.setPriority(className, priority, mockSession)).thenReturn("");
        when(respMock.getWriter()).thenReturn(mockWriter);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return null;
            }
        }).when(mockWriter).write("");

        servlet.doPost(reqMock, respMock);

        verify(mockWriter, times(1)).write("");
        verify(reqMock, times(2)).getSession();
        verify(reqMock, times(1)).getParameter("target");
        verify(reqMock, times(1)).getParameter("priority");
        verify(reqMock, times(1)).getRequestURI();
        verify(mockLog4jUtility, times(1)).isAuthorized(mockSession);
        verify(respMock, times(1)).getWriter();
    }

    @Test
    public void doPostReloadResetPriority() throws ServletException, IOException {
        String className = "ToFindBy";
        String priority = "restore";
        PrintWriter mockWriter = mock(PrintWriter.class);

        when(reqMock.getSession()).thenReturn(mockSession);
        when(reqMock.getParameter("target")).thenReturn(className);
        when(reqMock.getParameter("priority")).thenReturn(priority);
        when(reqMock.getRequestURI()).thenReturn(LOG4J_UTILITY_RELOAD_URL);
        when(mockLog4jUtility.isAuthorized(mockSession)).thenReturn(true);
        when(mockLog4jUtility.setPriority(className, priority, mockSession)).thenReturn("");
        when(respMock.getWriter()).thenReturn(mockWriter);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return null;
            }
        }).when(mockWriter).write("");

        servlet.doPost(reqMock, respMock);

        verify(mockWriter, times(1)).write("");
        verify(reqMock, times(2)).getSession();
        verify(reqMock, times(1)).getParameter("target");
        verify(reqMock, times(1)).getParameter("priority");
        verify(reqMock, times(1)).getRequestURI();
        verify(mockLog4jUtility, times(1)).isAuthorized(mockSession);
        verify(respMock, times(1)).getWriter();
    }


    @Test
    public void doGetNotAuthorized() throws ServletException, IOException {

        when(mockLog4jUtility.isAuthorized(mockSession)).thenReturn(false);

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
