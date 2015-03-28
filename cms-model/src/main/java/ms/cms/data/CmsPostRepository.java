package ms.cms.data;

import ms.cms.domain.CmsPost;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * CmsPostRepository
 * Created by thebaz on 24/03/15.
 */
public interface CmsPostRepository extends MongoRepository<CmsPost, String> {
    /**
     * Finds @CmsPost given its address
     *
     * @param siteId site id
     * @param uri post uri
     * @return list of @CmsPost
     */
    List<CmsPost> findBySiteIdAndUri(String siteId, String uri);
}
