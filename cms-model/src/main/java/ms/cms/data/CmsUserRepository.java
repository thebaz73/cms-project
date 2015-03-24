package ms.cms.data;

import ms.cms.domain.CmsUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * CmsUserRepository
 * Created by thebaz on 21/03/15.
 */
public interface CmsUserRepository extends MongoRepository<CmsUser, String> {
    /**
     * Finds @EmsUser given its username
     *
     * @param username user name
     * @return list of @EmsUser
     */
    List<CmsUser> findByUsername(String username);

    /**
     * Finds @EmsUser given its email
     *
     * @param email user email
     * @return list of @EmsUser
     */
    List<CmsUser> findByEmail(String email);
}
