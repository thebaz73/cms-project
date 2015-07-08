package sparkle.cms.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import sparkle.cms.domain.CmsHotspot;

/**
 * CmsPageRepository
 * Created by bazzoni on 08/07/2015.
 */
public interface CmsHotspotRepository extends MongoRepository<CmsHotspot, String> {
}
