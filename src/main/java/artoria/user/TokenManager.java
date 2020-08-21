package artoria.user;

import java.util.List;

/**
 * Token manager.
 * @author Kahle
 */
public interface TokenManager {

    /**
     * Save token object.
     * @param token Token object
     */
    void save(Token token);

    /**
     * Refresh token object.
     * @param tokenId Token id
     */
    void refresh(String tokenId);

    /**
     * Remove token object.
     * @param tokenId Token id
     */
    void remove(String tokenId);

    /**
     *
     */
    void clear();

    /**
     *
     */
    void removeByUserId(String userId);

    /**
     * Token id generation logic.
     * @return Token id
     */
    String generateId();

    /**
     * Find token object.
     * @param tokenId Token id
     * @return Token object
     */
    Token findById(String tokenId);

    /**
     * Query token object list.
     * @param userId User id
     * @return Token object list
     */
    List<Token> findByUserId(String userId);

}
