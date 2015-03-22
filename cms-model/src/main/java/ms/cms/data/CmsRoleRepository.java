package ms.cms.data;

import ms.cms.domain.CmsRole;
import ms.cms.domain.CmsUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * CmsRoleRepository
 * Created by thebaz on 21/03/15.
 */
public interface CmsRoleRepository extends MongoRepository<CmsRole, String> {
}
