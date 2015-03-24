package ms.cms.data;

import ms.cms.domain.CmsPost;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * CmsPageRepository
 * Created by thebaz on 24/03/15.
 */
public interface CmsPostRepository extends MongoRepository<CmsPost, String> {
}
