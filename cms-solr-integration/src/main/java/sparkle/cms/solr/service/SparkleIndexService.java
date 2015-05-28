package sparkle.cms.solr.service;

import sparkle.cms.domain.CmsContent;
import sparkle.cms.solr.domain.SparkleDocument;

import java.util.List;

/**
 * SparkleIndexService
 * Created by bazzoni on 27/05/2015.
 */
public interface SparkleIndexService {
    /**
     * Add a content to Solr index
     *
     * @param cmsContent cmsContent
     */
    public void addToIndex(CmsContent cmsContent);

    /**
     * Delete an indexed document from Solr index
     *
     * @param id document id
     */
    public void deleteFromIndex(String id);

    /**
     * Search index for specified term
     *
     * @param searchTerm search term
     * @return found documents
     */
    public List<SparkleDocument> search(String searchTerm);

    /**
     * Updates content to Solr index
     *
     * @param cmsContent cmsContent
     */
    public void update(CmsContent cmsContent);
}
