package eu.pm.tools.log4j;

/**
 * Created by iliesilviu
 * Date: 16/02/14 / 00:07538
 * <p>
 * <p/>
 * describes an action response.
 * <p/>
 * </p>
 * <br/>
 *
 * @author Silviu Ilie
 */
public class Log4jConfigResetResponse {

    public enum Type {
        ERROR, SUCCESS
    }

    private String message;
    private Type type;


    Log4jConfigResetResponse(final String message, final Type result) {
        this.message = message;
        this.type = result;
    }

    public Type getType() {
        return type;
    }

    String getMessage() {
        return message;
    }

    void setMessage(String message) {
        this.message = message;
    }

}
