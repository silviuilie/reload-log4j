package eu.pm.tools.log4j;

/**
 * Created by silviu
 * Date: 15/02/14 / 16:55944
 * <p>
 *     describes the application that uses the utility (base package name and authorization).
 * </p>
 * <br/>
 *
 * @author Silviu Ilie
 */
public class Log4jApplicationContext {

    public static DefaultLog4jUtilityAuthorization DEFAULT_AUTHORIZATION = new DefaultLog4jUtilityAuthorization();

    /**
     * application base package name.
     */
    private String packageName;

    /**
     * describes aut
     */
    private ReloadAuthorization utilityAuthorization;

    /**
     * sets mandatory fields.
     *
     * @param packageName application base package.
     * @param utilityAuthorization authorization.
     */
    public Log4jApplicationContext(String packageName, ReloadAuthorization utilityAuthorization) {
        this.packageName = packageName;
        this.utilityAuthorization = utilityAuthorization;
    }

    /**
     * default.
     */
    private Log4jApplicationContext() {
        //
    }


    public String getPackageName() {
        return packageName;
    }

    public ReloadAuthorization getUtilityAuthorization() {
        return utilityAuthorization;
    }
}
