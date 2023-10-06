package eu.pm.tools.log4j.controller;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.pm.tools.log4j.Log4jApplicationContext;
import eu.pm.tools.log4j.ReloadAuthorization;
import eu.pm.tools.log4j.fragment.Log4jUtilityController;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Iterator;

import static junit.framework.Assert.*;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.mockito.Mockito.*;

/**
 * Created by silviu
 * Date: 01/03/14 / 22:25389
 * <p/>
 * <p/>
 * <br/>
 * log4j-level-reloader|eu.pm.tools.log4j
 *
 * @author Silviu Ilie
 */
public class Log4jUtilityControllerTest   {

    private Log4jUtilityController log4jUtilityController = new Log4jUtilityController();
    private HttpSession mockHttpSession = mock(HttpSession.class);
    private HttpServletRequest mockHttpRequest = mock(HttpServletRequest.class);
    private ReloadAuthorization mockReloadAuthorization = mock(ReloadAuthorization.class);

    @Before
    public void init() {
        log4jUtilityController = new Log4jUtilityController();
        log4jUtilityController.setApplicationContext(
                new Log4jApplicationContext("eu", mockReloadAuthorization)
        );

        reset(mockReloadAuthorization, mockHttpRequest, mockHttpSession);
    }

    /**
     * fails with Apache Maven 3.8.5 / 3.6.3 (jdk1.8.0_241).
     * succeeds in
     * ¶
     */
    @Test public void setPriority() {
        try {

            when(mockReloadAuthorization.authorize(mockHttpSession)).thenReturn(true);

            final String result = log4jUtilityController.setPriority("Log4jUtilityControllerTest", "DEBUG", mockHttpSession);
            assertTrue(isNotEmpty(result));
            System.out.println("result = " + result);

            final ObjectMapper mapper = new ObjectMapper();
            final JsonFactory factory = mapper.getJsonFactory(); // since 2.1 use mapper.getFactory() instead
            final JsonParser jp = factory.createJsonParser(result);
            final JsonNode resultJSON = mapper.readTree(jp);

            Iterator<String> fields = resultJSON.fieldNames();
            while (fields.hasNext()) {
                final String key = fields.next();
                if ("type".equalsIgnoreCase(key)) {
                    assertEquals("SUCCESS", resultJSON.get(key).toString().replaceAll("\"", ""));
                }
            }

            verify(mockReloadAuthorization).authorize(mockHttpSession);

        } catch (IOException e) {
            fail();
        }
    }

    /**
     * fails with spring-jcl (5.3)
     *  see https://github.com/spring-projects/spring-framework/issues/20611
     */
    @Test
    public void resetPriority() {
        try {

            when(mockReloadAuthorization.authorize(mockHttpSession)).thenReturn(true);

            // set initial priority
            log4jUtilityController.setPriority("Log4jUtilityControllerTest", "DEBUG", mockHttpSession);
            final String result = log4jUtilityController.setPriority("Log4jUtilityControllerTest", "restore", mockHttpSession);
            assertTrue(isNotEmpty(result));

            final ObjectMapper mapper = new ObjectMapper();
            final JsonFactory factory = mapper.getJsonFactory(); // since 2.1 use mapper.getFactory() instead
            final JsonParser jp = factory.createJsonParser(result);
            final JsonNode resultJSON = mapper.readTree(jp);

            Iterator<String> fields = resultJSON.fieldNames();
            while (fields.hasNext()) {
                final String key = fields.next();
                if ("type".equalsIgnoreCase(key)) {
                    assertEquals("SUCCESS", resultJSON.get(key).toString().replaceAll("\"", ""));
                }
            }

            verify(mockReloadAuthorization, times(2)).authorize(mockHttpSession);

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void resetPriorityForNeverChangedTarget() {
        try {

            when(mockReloadAuthorization.authorize(mockHttpSession)).thenReturn(true);

            final String result = log4jUtilityController.setPriority("Log4jUtilityControllerTest"
                    + RandomUtils.nextInt(), "restore", mockHttpSession
            );

            assertTrue(isNotEmpty(result));

            final ObjectMapper mapper = new ObjectMapper();
            final JsonFactory factory = mapper.getJsonFactory(); // since 2.1 use mapper.getFactory() instead
            final JsonParser jp = factory.createJsonParser(result);
            final JsonNode resultJSON = mapper.readTree(jp);

            Iterator<String> fields = resultJSON.fieldNames();
            while (fields.hasNext()) {
                final String key = fields.next();
                if ("type".equalsIgnoreCase(key)) {
                    assertEquals("SUCCESS", resultJSON.get(key).toString().replaceAll("\"", ""));
                }
            }

            verify(mockReloadAuthorization).authorize(mockHttpSession);

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void resetPriorityNotAuthorized() {
        try {

            when(mockReloadAuthorization.authorize(mockHttpSession)).thenReturn(false);

            log4jUtilityController.setPriority("Log4jUtilityControllerTest", "restore", mockHttpSession);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalStateException);
        }
    }

    @Test
    public void log4jChange() {

        when(mockReloadAuthorization.authorize(mockHttpSession)).thenReturn(true);

        final String viewName = log4jUtilityController.log4jChange(mockHttpSession, mockHttpRequest);

        verify(mockReloadAuthorization).authorize(mockHttpSession);

        assertNotNull(viewName);

    }


    @Test
    public void log4jChangeNotAuthorized() {
        when(mockReloadAuthorization.authorize(mockHttpSession)).thenReturn(false);

        String viewName = null;
        try {
            viewName = log4jUtilityController.log4jChange(mockHttpSession, mockHttpRequest);
        } catch (Exception e) {
            assert (e instanceof IllegalStateException);
        }

        verify(mockReloadAuthorization).authorize(mockHttpSession);

        assertNull(viewName);

    }

    @Test @Ignore
    public void log4jFindClass() {

        when(mockReloadAuthorization.authorize(mockHttpSession)).thenReturn(true);
        try {
            final String result = log4jUtilityController.log4jFindClass("Log4jUtility", mockHttpSession);

            log.debug("classes found:" + result);

            verify(mockReloadAuthorization).authorize(mockHttpSession);

            assertEquals(
                    "eu.pm.tools.log4j.controller.Log4jUtilityControllerTest",
                    result.replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", "")
            );
        } catch (IOException e) {
            fail();
        }

    }

    @Test
    public void log4jFindClassNotAuthorized() {

        when(mockReloadAuthorization.authorize(mockHttpSession)).thenReturn(false);
        try {
            log4jUtilityController.log4jFindClass("Log4jUtilityControllerTest", mockHttpSession);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalStateException);
        }

    }


    private Log log = LogFactory.getLog(this.getClass());


}
