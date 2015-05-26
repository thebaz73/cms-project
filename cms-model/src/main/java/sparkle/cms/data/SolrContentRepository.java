package sparkle.cms.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.SolrCrudRepository;
import sparkle.cms.domain.CmsContent;

/**
 * SolrContentRepository
 * Created by bazzoni on 26/05/2015.
 */
public interface SolrContentRepository extends SolrCrudRepository<CmsContent, String> {
    /**
     * Find content matching title, summary or content
     *
     * @param title   content title
     * @param summary content summary
     * @param content content summmry
     * @param page    pageable
     * @return page of @CmsContent
     */
    Page<CmsContent> findByTitleOrSummaryContainingOrContentContaining(String title, String summary, String content, Pageable page);
}
