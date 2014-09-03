package eu.pm.tools.log4j;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Level;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

import static org.apache.commons.lang.StringUtils.*;
import static org.reflections.util.ClasspathHelper.contextClassLoader;
import static org.reflections.util.ClasspathHelper.forClassLoader;
import static org.reflections.util.ClasspathHelper.staticClassLoader;

/**
 * Created by silviu
 * Date: 13/02/14 / 22:14279
 * <p>
 * handles all log4j priority/level change requests
 * </p>
 * <br/>
 *
 * @author Silviu Ilie
 */
public class Log4jUtility {


    @Autowired
    protected Log4jApplicationContext applicationContext;

    /**
     * URI for the 'home' of the utility
     */
    public final static String LOG4J_UTILITY_HOME_URL = "reload.log4j";

    /**
     * URI for searching a class by name
     */
    public final static String LOG4J_UTILITY_FIND_CLASS_URL = "log4jFindClass.log4j";

    /**
     * URI for setting the priority
     */
    public final static String LOG4J_UTILITY_RELOAD_URL = "log4jReload.log4j";

    /**
     * name of the parameters
     */
    public final static String LOG4J_UTILITY_PACKAGE_ATTR_NAME = "log4j-base-packageName";
    public final static String LOG4J_UTILITY_JSP_LOCATION_ATTR_NAME = "log4j-jsp-location";
    public final static String LOG4J_UTILITY_AUTH_ATTR_NAME = "log4j-authorization-class";

    public Log4jUtility() {
        mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    /**
     * context setter.
     *
     * @param context app. context
     */
    public void setApplicationContext(Log4jApplicationContext context) {
        this.applicationContext = context;
    }

    /**
     * finds class that contains the {@code name}
     *
     * @param name fragment.
     * @return classes array
     */
    public String log4jFindClass(final String name, HttpSession session) throws IOException {
        if (isAuthorized(session)) {
            final List<String> classes = findClasspathClasses(name);
            return mapper.writeValueAsString(classes);
        }

        throw new IllegalStateException();
    }


    /**
     * priority reload request handler.
     *
     * @param priority new priority. any string will do to allow custom log4j priorities.
     * @return Log4jConfigResetResponse
     */
    public String setPriority(final String target, final String priority, HttpSession session) throws IOException {

        if (isAuthorized(session)) {
            if ("restore".equalsIgnoreCase(priority)) {
                return mapper.writeValueAsString(resetPriority(target));
            }
            return mapper.writeValueAsString(changeTargetPriority(target, priority));
        }
        throw new IllegalStateException();
    }


    /**
     * authorization check.
     *
     * @param session http session.
     * @return true if authorized
     */
    public boolean isAuthorized(HttpSession session) {
        return applicationContext.getUtilityAuthorization().authorize(session);
    }

    /**
     * resets priority for class to original value.
     *
     * @param target class name.
     * @return {@link eu.pm.tools.log4j.Log4jConfigResetResponse}
     */
    private Log4jConfigResetResponse resetPriority(final String target) {
        final String initialLevel = classInitialPriority.get(target);
        if (isEmpty(initialLevel)) {
            return new Log4jConfigResetResponse(
                    "priority for " + target + " was not changed. nothing to do.",
                    Log4jConfigResetResponse.Type.SUCCESS
            );
        } else {
            return changeTargetPriority(target, initialLevel);
        }
    }

    /**
     * Reflections mapper
     */
    final ObjectMapper mapper = new ObjectMapper();


    /**
     * changes log4j logging priority/level for class name with the name {@code target}.
     *
     * @param target   class name.
     * @param priority new priority/target.
     * @return {@link eu.pm.tools.log4j.Log4jConfigResetResponse}.
     */
    private synchronized Log4jConfigResetResponse changeTargetPriority(String target, String priority) {
        if (CollectionUtils.isEmpty(findClasspathClasses(target))) {
            log.error("failed to find class " + target);
            return new Log4jConfigResetResponse(
                    "failed to find class " + target, Log4jConfigResetResponse.Type.ERROR
            );
        }

        Log targetLogger = null;
        try {
            targetLogger = LogFactory.getLog(target);
            // set initial log4j priority
            if (classInitialPriority.get(target) == null) {
                try {
                    setInitialPriority(target, targetLogger);
                } catch (Exception e) {
                    log.error(e);
                    return new Log4jConfigResetResponse("failed to handle Logger for class " + target
                            + ". Is the logger for this class a Log4JLogger ? : " + e.getMessage(),
                            Log4jConfigResetResponse.Type.ERROR);
                }
            }

        } catch (LogConfigurationException e) {
            log.error(e);
            return new Log4jConfigResetResponse(
                    "failed to get Logger for log4j for class " + target + " : " + e.getMessage(),
                    Log4jConfigResetResponse.Type.ERROR
            );
        }
        if (targetLogger == null) {
            log.error("failed to get Logger for log4j for class " + target);
            return new Log4jConfigResetResponse("failed to get Logger for log4j for class " + target,
                    Log4jConfigResetResponse.Type.ERROR
            );

        }
        try {
            if (isNotEmpty(priority)) {
                // change priority
                Log4JLogger classLog4jLogger = (Log4JLogger) targetLogger;
                classLog4jLogger.getLogger().setLevel(Level.toLevel(priority));
            }
        } catch (Exception e) {
            return new Log4jConfigResetResponse(
                    "failed to change log4j priority/level to " + priority + " : " + e.getMessage(),
                    Log4jConfigResetResponse.Type.ERROR
            );
        }

        return new Log4jConfigResetResponse(
                "for class " + target + ", log4j priority/level changed to " + priority,
                Log4jConfigResetResponse.Type.SUCCESS
        );
    }

    Reflections reflections = null;

    /**
     * finds classes in the classpath that have a name that contains {@code classNameFragment}.
     *
     * @param classNameFragment search string.
     * @return {@code List<String>} classes that match the searched string.
     */
    private List<String> findClasspathClasses(String classNameFragment) {

        if (reflections == null) reflections = new Reflections(
                new ConfigurationBuilder()
                        .setScanners(new SubTypesScanner(false), new ResourcesScanner())
                        .setUrls(
                                forClassLoader(
                                        contextClassLoader(),
                                        staticClassLoader()
                                )
                        )
                        .filterInputsBy(new FilterBuilder().include(
                                FilterBuilder.prefix(applicationContext.getPackageName())
                        ))
        );


        Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);
        List<String> classNames = new ArrayList<String>(20);
        for (Class classz : classes) {
            if (containsIgnoreCase(classz.getName(), classNameFragment)) {
                classNames.add(classz.getName());
            }
        }


        return classNames;
    }


    /**
     * initial class priority/level.
     */
    private static Map<String, String> classInitialPriority = new HashMap<String, String>(10);

    /**
     * saves initial priority/level for the named class.
     *
     * @param className    name of the class
     * @param targetLogger logger for the class.
     */
    private synchronized void setInitialPriority(final String className, final Log targetLogger) {
        try {
            Log4JLogger logger = ((Log4JLogger) targetLogger);
            if (logger == null) {
                throw new RuntimeException("no logger found for " + className);
            }
            if (logger.getLogger() != null
                    && (logger.getLogger().getEffectiveLevel() == null
                    || logger.getLogger().getLevel() == null)) {
                classInitialPriority.put(className,
                        (logger.getLogger().getLevel() != null ?
                                logger.getLogger().getLevel().toString() :
                                logger.getLogger().getEffectiveLevel().toString()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * this class logger.
     */
    private static final Log log = LogFactory.getLog(Log4jUtility.class);

}
