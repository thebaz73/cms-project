package ms.cms.data;

import ms.cms.domain.CmsTag;
import org.springframework.data.mongodb.repository.MongoRepository;

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
     * @param tag    tag tag
     * @return list of @CmsTag
     */
    List<CmsTag> findBySiteIdAndTag(String siteId, String tag);
}
