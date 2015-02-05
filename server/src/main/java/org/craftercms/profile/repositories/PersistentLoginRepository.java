package org.craftercms.profile.repositories;

import org.craftercms.commons.mongo.CrudRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.api.PersistentLogin;

/**
 * DB repository for {@link org.craftercms.profile.api.PersistentLogin}s.
 *
 * @author avasquez
 */
public interface PersistentLoginRepository extends CrudRepository<PersistentLogin> {

    /**
     * Returns the login associated to the given profile ID and token.
     *
     * @param profileId the profile's ID
     * @param token     the token
     *
     * @return the login
     */
    PersistentLogin findByProfileIdAndToken(String profileId, String token) throws MongoDataException;

    /**
     * Removes logins with timestamps older than the specified number of seconds.
     *
     * @param seconds the number of seconds
     */
    void removeOlderThan(long seconds) throws MongoDataException;

}
