package sparkle.cms.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import sparkle.cms.domain.CmsTag;

import java.util.List;

/**
 * CmsTagRepository
 * Created by thebaz on 24/03/15.
 */
public interface CmsTagRepository extends MongoRepository<CmsTag, String> {
    /**
     * Finds @CmsTag given its address
     *
     * @param siteId site id
     * @param uri    tag uri
     * @return list of @CmsTag
     */
    List<CmsTag> findBySiteIdAndUri(String siteId, String uri);

    /**
     * Finds @CmsTag given its address
     *
     * @param siteId site id
     * @return list of @CmsTag
     */
    List<CmsTag> findBySiteId(String siteId);
}
