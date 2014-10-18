package eu.pm.tools.log4j;

import javax.servlet.http.HttpSession;

/**
 * <p>
 * use this to describe who can access the utility.
 * </p>
 * created : 18/10/14 17:35
 *
 * @author Silviu Ilie
 */
public interface ReloadAuthorization {

    boolean authorize(HttpSession session);

}
