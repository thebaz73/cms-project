package sparkle.cms.solr.data;

import org.springframework.data.domain.Sort;
import org.springframework.data.solr.repository.SolrCrudRepository;
import sparkle.cms.solr.domain.SparkleDocument;

import java.util.List;

/**
 * SolrContentRepository
 * Created by bazzoni on 26/05/2015.
 */
public interface SolrContentRepository extends PartialUpdateRepository, SolrCrudRepository<SparkleDocument, String> {
    /**
     * Find content matching title, summary or content
     *
     * @param title   content title
     * @param summary content summary
     * @param content content summmry
     * @param sort    pageable
     * @return list of @CmsContent
     */
    List<SparkleDocument> findByTitleContainingOrSummaryContainingOrContentContaining(String title, String summary, String content, Sort sort);
}
