package eu.pm.tools.log4j;

import org.junit.Test;

import javax.servlet.http.HttpSession;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * <p>
 * DefaultLog4jUtilityAuthorization test
 * </p>
 * created : 18/10/14 20:30
 *
 * @author Silviu Ilie
 */
public class DefaultLog4jUtilityAuthorizationTest {

    private DefaultLog4jUtilityAuthorization utilityAuthorization = new DefaultLog4jUtilityAuthorization();

    HttpSession mockSession = mock(HttpSession.class);


    //@Test
    public void authorize() {
        assertTrue(utilityAuthorization.authorize(mockSession));
    }
}
