package sparkle.cms.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import sparkle.cms.domain.CmsComment;

import java.util.List;

/**
 * CmsPageRepository
 * Created by thebaz on 24/03/15.
 */
public interface CmsCommentRepository extends MongoRepository<CmsComment, String> {

    /**
     * Finds list of @CmsComment given its contentId
     *
     * @param siteId site id
     * @return list of @CmsComment
     */
    List<CmsComment> findBySiteId(String siteId);

    /**
     * Finds list of @CmsComment given its siteId
     *
     * @param siteId   site id
     * @param approved approved flag
     * @return list of @CmsComment
     */
    List<CmsComment> findBySiteIdAndApproved(String siteId, boolean approved);

    /**
     * Finds list of @CmsComment given its contentId
     *
     * @param siteId   site id
     * @param pageable page info
     * @return list of @CmsComment
     */
    Page<CmsComment> findBySiteId(String siteId, Pageable pageable);

    /**
     * Finds list of @CmsComment given its siteId
     *
     * @param siteId   site id
     * @param approved approved flag
     * @param pageable page info
     * @return list of @CmsComment
     */
    Page<CmsComment> findBySiteIdAndApproved(String siteId, boolean approved, Pageable pageable);

    /**
     * Deletes @CmsComments given its site id
     *
     * @param siteId site id
     */
    List<CmsComment> deleteBySiteId(String siteId);

    /**
     * Count  total site comments
     *
     * @param siteId site id
     * @return comment count
     */
    int countBySiteId(String siteId);

    /**
     * Finds list of @CmsComment given its contentId
     *
     * @param contentId content id
     * @return list of @CmsComment
     */
    List<CmsComment> findByContentId(String contentId);

    /**
     * Finds list of @CmsComment given its contentId
     *
     * @param contentId content id
     * @param approved approved flag
     * @return list of @CmsComment
     */
    List<CmsComment> findByContentIdAndApproved(String contentId, boolean approved);

    /**
     * Finds page of @CmsComment given its contentId
     *
     * @param contentId content id
     * @param pageable  page info
     * @return list of @CmsComment
     */
    Page<CmsComment> findByContentId(String contentId, Pageable pageable);

    /**
     * Finds page of @CmsComment given its contentId
     *
     * @param contentId content id
     * @param approved approved flag
     * @param pageable  page info
     * @return list of @CmsComment
     */
    Page<CmsComment> findByContentIdAndApproved(String contentId, boolean approved, Pageable pageable);

    /**
     * Deletes @CmsComments given its site id
     *
     * @param contentId content id
     */
    List<CmsComment> deleteByContentId(String contentId);
}
