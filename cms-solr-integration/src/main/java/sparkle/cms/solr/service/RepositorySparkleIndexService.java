package sparkle.cms.solr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sparkle.cms.domain.CmsContent;
import sparkle.cms.solr.data.SolrContentRepository;
import sparkle.cms.solr.domain.SparkleDocument;

import java.util.List;

/**
 * RepositorySparkleIndexService
 * Created by bazzoni on 28/05/2015.
 */
@Service
public class RepositorySparkleIndexService implements SparkleIndexService {
    @Autowired
    private SolrContentRepository repository;

    /**
     * Add a content to Solr index
     *
     * @param cmsContent cmsContent
     */
    @Override
    public void addToIndex(CmsContent cmsContent) {
        final SparkleDocument document = SparkleDocument.getBuilder(cmsContent.getId(), cmsContent.getTitle())
                .summary(cmsContent.getSummary())
                .content(cmsContent.getContent())
                .build();
        repository.save(document);
    }

    /**
     * Delete an indexed document from Solr index
     *
     * @param id document id
     */
    @Override
    public void deleteFromIndex(String id) {
        repository.delete(id);
    }

    /**
     * Search index for specified term
     *
     * @param searchTerm search term
     * @return found documents
     */
    @Override
    public List<SparkleDocument> search(String searchTerm) {
        return repository.findByTitleContainingOrSummaryContainingOrContentContaining(searchTerm, searchTerm, searchTerm, new Sort(Sort.Direction.DESC, SparkleDocument.FIELD_ID));
    }

    /**
     * Updates content to Solr index
     *
     * @param cmsContent cmsContent
     */
    @Override
    public void update(CmsContent cmsContent) {
        repository.update(cmsContent);
    }
}
