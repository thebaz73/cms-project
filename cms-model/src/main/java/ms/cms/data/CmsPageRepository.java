package ms.cms.data;

import ms.cms.domain.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * CmsPageRepository
 * Created by thebaz on 24/03/15.
 */
@Deprecated
public interface CmsPageRepository extends MongoRepository<CmsPage, String> {
    /**
     * Finds @CmsPage given its address
     *
     * @param siteId site id
     * @param uri page uri
     * @return list of @CmsPage
     */
    List<CmsPage> findBySiteIdAndUri(String siteId, String uri);
}
