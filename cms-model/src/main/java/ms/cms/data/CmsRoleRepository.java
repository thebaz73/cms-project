package ms.cms.data;

import ms.cms.domain.CmsRole;
import org.springframework.data.mongodb.repository.MongoRepository;

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
