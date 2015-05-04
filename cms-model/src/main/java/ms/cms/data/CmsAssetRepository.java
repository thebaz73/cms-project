package ms.cms.data;

import ms.cms.domain.CmsAsset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * CmsPageRepository
 * Created by thebaz on 24/03/15.
 */
public interface CmsAssetRepository extends MongoRepository<CmsAsset, String> {
    /**
     * Load all site assets
     *
     * @param siteId site id
     * @return list of asset
     */
    List<CmsAsset> findBySiteId(String siteId);

    /**
     * Load all site assets
     *
     * @param siteId site id
     * @return page of asset
     */
    Page<CmsAsset> findBySiteId(String siteId, Pageable pageable);
}
