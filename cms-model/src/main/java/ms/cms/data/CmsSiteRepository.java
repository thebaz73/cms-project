package ms.cms.data;

import ms.cms.domain.CmsSite;
import ms.cms.domain.CmsUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * CmsSiteRepository
 * Created by thebaz on 21/03/15.
 */
public interface CmsSiteRepository extends MongoRepository<CmsSite, String> {
    /**
     * Finds @EmsSite given its address
     *
     * @param address site address
     * @return list of @EmsSite
     */
    List<CmsSite> findByAddress(String address);

    /**
     * Finds @EmsSite given its web master
     *
     * @param user site web master
     * @return list of @EmsSite
     */
    List<CmsSite> findByWebMaster(CmsUser user);
}
