package artoria.user;

import java.util.Collection;

/**
 * Permission manager.
 * @author Kahle
 */
public interface PermissionManager {

    /**
     */
    void save(String resource, Collection<String> roleCodes);

    /**
     */
    void remove(String resource, Collection<String> roleCodes);

    /**
     */
    void clear();

    /**
     */
    Collection<String> findByResource(String resource);

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
     * @return Whether to allow access
     */
    boolean authenticate(String resource, Collection<String> roleCodes);

}
