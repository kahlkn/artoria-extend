package artoria.user;

/**
 * Permission manager.
 * @author Kahle
 */
public interface PermissionManager {

    /**
     * Authenticate.
     * @param resource Resource to be accessed
     * @param token User token object
     * @return Whether to allow access
     */
    boolean authenticate(String resource, Token token);

    /**
     * Authenticate.
     * @param resource Resource to be accessed
     * @param userInfo User information object
     * @return Whether to allow access
     */
    boolean authenticate(String resource, UserInfo userInfo);

    /**
     * Authenticate.
     * @param resource Resource to be accessed
     * @param roleCode The visitor's role code
     * @return Whether to allow access
     */
    boolean authenticate(String resource, String roleCode);

}
