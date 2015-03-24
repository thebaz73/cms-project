package ms.cms.data;

import ms.cms.domain.CmsAsset;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * CmsPageRepository
 * Created by thebaz on 24/03/15.
 */
public interface CmsAssetRepository extends MongoRepository<CmsAsset, String> {
}
