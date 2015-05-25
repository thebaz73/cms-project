package sparkle.cms.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import sparkle.cms.domain.AssetType;
import sparkle.cms.domain.CmsAsset;

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

    /**
     * Load a site assets by uri
     *
     * @param siteId site id
     * @param uri asset uri
     * @return list of asset
     */
    List<CmsAsset> findBySiteIdAndUri(String siteId, String uri);

    /**
     * Load a site asset by type
     *
     * @param siteId site  id
     * @param type   assert type
     * @return list of asset
     */
    List<CmsAsset> findBySiteIdAndType(String siteId, AssetType type);

    /**
     * Load a site asset by type
     *
     * @param siteId site  id
     * @param type   assert type
     * @return page of asset
     */
    Page<CmsAsset> findBySiteIdAndType(String siteId, AssetType type, Pageable pageable);
}
