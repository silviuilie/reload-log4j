package eu.pm.tools.log4j;

import eu.pm.tools.log4j.fragment.Log4jUtilityController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Iterator;

import static junit.framework.Assert.*;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.mockito.Mockito.mock;

/**
 * Created by iliesilviu
 * Date: 01/03/14 / 22:25389
 * <p/>
 * <p>
 * TODO :comment me!
 * </p>
 * <p/>
 * <br/>
 * log4j-level-reloader|eu.pm.tools.log4j
 *
 * @author Silviu Ilie
 */
public class Log4jUtilityControllerTest {

    private Log4jUtilityController log4jUtilityController = new Log4jUtilityController();
    private HttpSession mockHttpSession = mock(HttpSession.class);
    private HttpServletRequest mockHttpRequest = mock(HttpServletRequest.class);

    @Before
    public void init() {
        log4jUtilityController = new Log4jUtilityController();
        log4jUtilityController.setApplicationContext(
                new Log4jApplicationContext("eu", Log4jApplicationContext.DEFAULT_AUTHORIZATION)
        );
    }

    @Test
    public void setPriority() {
        try {

            final String result = log4jUtilityController.setPriority("Log4jUtilityControllerTest", "DEBUG", mockHttpSession);
            assertTrue(isNotEmpty(result));

            final ObjectMapper mapper = new ObjectMapper();
            final JsonFactory factory = mapper.getJsonFactory(); // since 2.1 use mapper.getFactory() instead
            final JsonParser jp = factory.createJsonParser(result);
            final JsonNode resultJSON = mapper.readTree(jp);

            Iterator<String> fields = resultJSON.getFieldNames();
            while (fields.hasNext()) {
                final String key = fields.next();
                if ("type".equalsIgnoreCase(key)) {
                    assertEquals("SUCCESS", resultJSON.get(key).toString().replaceAll("\"", ""));
                }
            }

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void log4jChange() {

        final String viewName = log4jUtilityController.log4jChange(mockHttpSession, mockHttpRequest);
        assertNotNull(viewName);

    }

    @Test
    public void log4jFindClass() {

        try {
            final String result = log4jUtilityController.log4jFindClass("Log4jUtilityControllerTest", mockHttpSession);

            log.debug("classes found:" + result);

            assertEquals(
                    "eu.pm.tools.log4j.Log4jUtilityControllerTest",
                    result.replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", "")
            );
        } catch (IOException e) {
            fail();
        }

    }


    private Log log = LogFactory.getLog(this.getClass());


}
