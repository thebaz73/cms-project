package ms.cms.data;

import ms.cms.domain.CmsComment;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * CmsPageRepository
 * Created by thebaz on 24/03/15.
 */
public interface CmsCommentRepository extends MongoRepository<CmsComment, String> {
}
