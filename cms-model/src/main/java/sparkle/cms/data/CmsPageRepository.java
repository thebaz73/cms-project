package sparkle.cms.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import sparkle.cms.domain.CmsPage;

import java.util.List;

/**
 * CmsPageRepository
 * Created by bazzoni on 08/07/2015.
 */
public interface CmsPageRepository extends MongoRepository<CmsPage, String> {
    /**
     * Finds @CmsPage given its siteId
     *
     * @param siteId site id
     * @return list of @CmsPage
     */
    List<CmsPage> findBySiteId(String siteId);

    /**
     * Finds @CmsPage given its siteId and its parent
     *
     * @param siteId site id
     * @return list of @CmsPage
     */
    List<CmsPage> findBySiteIdAndParent(String siteId, CmsPage parent);
}
