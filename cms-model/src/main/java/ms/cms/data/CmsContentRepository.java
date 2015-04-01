package ms.cms.data;

import ms.cms.domain.CmsContent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * CmsContentRepository
 * Created by thebaz on 24/03/15.
 */
public interface CmsContentRepository extends MongoRepository<CmsContent, String> {
    /**
     * Finds @CmsContent given its address
     *
     * @param siteId site id
     * @param uri    post uri
     * @return list of @CmsContent
     */
    List<CmsContent> findBySiteIdAndUri(String siteId, String uri);
}
