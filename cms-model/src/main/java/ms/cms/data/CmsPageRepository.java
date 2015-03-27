package ms.cms.data;

import ms.cms.domain.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * CmsPageRepository
 * Created by thebaz on 24/03/15.
 */
public interface CmsPageRepository extends MongoRepository<CmsPage, String> {
    /**
     * Finds @CmsPage given its address
     *
     * @param uri page uri
     * @return list of @CmsPage
     */
    List<CmsPage> findByUri(String uri);
}
