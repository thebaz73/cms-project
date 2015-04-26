package ms.cms.data;

import ms.cms.domain.CmsContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * CmsContentRepository
 * Created by thebaz on 24/03/15.
 */
public interface CmsContentRepository extends MongoRepository<CmsContent, String> {
    /**
     * Finds @CmsContent given its site id and uri
     *
     * @param siteId site id
     * @param uri    post uri
     * @return list of @CmsContent
     */
    List<CmsContent> findBySiteIdAndUri(String siteId, String uri);

    /**
     * Finds @CmsContent given its site id and uri
     *
     * @param siteId site id
     * @param uri    post uri
     * @param pageable page info
     *
     * @return page of @CmsContent
     */
    Page<CmsContent> findBySiteIdAndUri(String siteId, String uri, Pageable pageable);

    /**
     * Finds @CmsContents given its site id
     *
     * @param siteId site id
     * @return list of @CmsContent
     */
    List<CmsContent> findBySiteId(String siteId);

    /**
     * Finds @CmsContents given its site id
     *
     * @param siteId   site id
     * @param published published
     * @param pageable page info
     * @return page of @CmsContent
     */
    Page<CmsContent> findBySiteIdAndPublished(String siteId, boolean published, Pageable pageable);

    /**
     * Counts @CmsContents given its site id
     *
     * @param siteId site id
     * @return number of @CmsContent
     */
    int countBySiteId(String siteId);

    /**
     * Deletes @CmsContents given its site id
     *
     * @param siteId site id
     */
    List<CmsContent> deleteBySiteId(String siteId);
}
