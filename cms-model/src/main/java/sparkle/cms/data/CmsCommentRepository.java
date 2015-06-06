package sparkle.cms.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
     * @param contentId content id
     * @return list of @CmsComment
     */
    List<CmsComment> findByContentId(String contentId);

    /**
     * Finds list of @CmsComment given its contentId
     *
     * @param contentId content id
     * @param pageable  page info
     * @return list of @CmsComment
     */
    Page<CmsComment> findByContentId(String contentId, PageRequest pageable);

    /**
     * Finds list of @CmsComment given its contentId
     *
     * @param siteId site id
     * @return list of @CmsComment
     */
    List<CmsComment> findBySiteId(String siteId);

    /**
     * Deletes @CmsComments given its site id
     *
     * @param contentId content id
     */
    List<CmsComment> deleteByContentId(String contentId);

    /**
     * Deletes @CmsComments given its site id
     *
     * @param siteId site id
     */
    List<CmsComment> deleteBySiteId(String siteId);
}
