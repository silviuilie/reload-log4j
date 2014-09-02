package eu.pm.tools.log4j.fragment;

import eu.pm.tools.log4j.Log4jUtility;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by silviu
 * Date: 13/02/14 / 22:14279
 * <p>
 * spring {@code Controller} for {@link eu.pm.tools.log4j.Log4jUtility}.
 * </p>
 * <br/>
 *
 * @author Silviu Ilie
 */
@Controller
public class Log4jUtilityController extends Log4jUtility {


    /**
     * log4j priority reload 'home'.
     *
     * @return
     */
    @RequestMapping(value = LOG4J_UTILITY_HOME)
    public final String log4jChange(HttpSession session, HttpServletRequest request) {
        if (isAuthorized(session)) {

            request.setAttribute(Log4jUtility.LOG4J_UTILITY_PACKAGE_ATTR_NAME, applicationContext.getPackageName());
            request.setAttribute(Log4jUtility.LOG4J_UTILITY_AUTH_ATTR_NAME, applicationContext.getUtilityAuthorization());

            return "log4jlevelReload";
        }
        throw new IllegalStateException();
    }

    /**
     * finds class that contains the {@code name}
     *
     * @param name class name fragment
     * @return
     */
    @RequestMapping(value = LOG4J_UTILITY_FIND_CLASS, method = RequestMethod.POST)
    @ResponseBody
    public final String log4jFindClass(@RequestParam(value = "name") final String name,
                                       HttpSession session) throws IOException {
        return super.log4jFindClass(name, session);
    }


    /**
     * priority reload request handler.
     *
     * @param priority logging priority
     * @return
     */
    @RequestMapping(value = LOG4J_UTILITY_RELOAD, method = RequestMethod.POST)
    @ResponseBody
    public final String setPriority(@RequestParam(value = "target") final String target,
                                    @RequestParam(value = "priority") final String priority,
                                    HttpSession session) throws IOException {

        return super.setPriority(target, priority, session);
    }
}
