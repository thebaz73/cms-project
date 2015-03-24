package ms.cms.data;

import ms.cms.domain.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * CmsPageRepository
 * Created by thebaz on 24/03/15.
 */
public interface CmsPageRepository extends MongoRepository<CmsPage, String> {
}
