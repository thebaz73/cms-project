package sparkle.cms.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import sparkle.cms.domain.CmsSite;
import sparkle.cms.domain.CmsUser;

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

    /**
     * Finds @EmsSite given its web master
     *
     * @param cmsUser  site web master
     * @param pageable pageable
     * @return list of @EmsSite
     */
    Page<CmsSite> findByWebMaster(CmsUser cmsUser, Pageable pageable);
}
