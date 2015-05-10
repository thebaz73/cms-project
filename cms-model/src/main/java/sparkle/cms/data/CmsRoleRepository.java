package sparkle.cms.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import sparkle.cms.domain.CmsRole;

import java.util.List;

/**
 * CmsRoleRepository
 * Created by thebaz on 21/03/15.
 */
public interface CmsRoleRepository extends MongoRepository<CmsRole, String> {
    /**
     * Finds @EmsRole given its role
     *
     * @param role role name
     * @return list of @EmsRole
     */
    List<CmsRole> findByRole(String role);
}
