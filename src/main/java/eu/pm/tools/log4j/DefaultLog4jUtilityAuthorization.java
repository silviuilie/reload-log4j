package eu.pm.tools.log4j;

import javax.servlet.http.HttpSession;

/**
 * <p>
 * * default authorization.
 * <b>
 * WARNING: this allows all requests!.
 * </b>
 * </p>
 * created : 18/10/14 17:18
 *
 * @author Silviu Ilie
 */
public class DefaultLog4jUtilityAuthorization implements ReloadAuthorization {
    @Override
    public boolean authorize(HttpSession session) {
        return true;
    }
}
