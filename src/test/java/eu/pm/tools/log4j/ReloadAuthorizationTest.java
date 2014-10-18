package eu.pm.tools.log4j;

import org.junit.Test;

import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.mock;

/**
 * <p>
 * ReloadAuthorization test
 * </p>
 * created : 18/10/14 21:05
 *
 * @author Silviu Ilie
 */
public class ReloadAuthorizationTest {

    private ReloadAuthorization mockReloadAuthorization = mock(ReloadAuthorization.class);
    private HttpSession mockSession = mock(HttpSession.class);


    @Test
    public void authorize() {
        mockReloadAuthorization.authorize(mockSession);
    }
}
