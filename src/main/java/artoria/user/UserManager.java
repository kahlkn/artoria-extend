package artoria.user;

/**
 * User manager.
 * @author Kahle
 */
public interface UserManager {

    /**
     * Save user information object.
     * @param userInfo User information object
     */
    void save(UserInfo userInfo);

    /**
     * Refresh user information object.
     * @param userId User id
     */
    void refresh(String userId);

    /**
     * Remove user information object.
     * @param userId User id
     */
    void remove(String userId);

    /**
     *
     */
    void clear();

    /**
     * Find user information object.
     * @param userId User id
     * @return User information object
     */
    UserInfo findById(String userId);

}
