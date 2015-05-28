package sparkle.cms.solr.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.PartialUpdate;
import org.springframework.stereotype.Repository;
import sparkle.cms.domain.CmsContent;
import sparkle.cms.solr.domain.SparkleDocument;

import javax.annotation.Resource;

/**
 * SolrContentRepositoryImpl
 * Created by bazzoni on 28/05/2015.
 */
@Repository
public class SolrContentRepositoryImpl implements PartialUpdateRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolrContentRepositoryImpl.class);

    @Resource
    private SolrTemplate solrTemplate;

    /**
     * Update solr index
     *
     * @param cmsContent content
     */
    @Override
    public void update(CmsContent cmsContent) {
        LOGGER.debug("Performing partial update for todo entry: {}", cmsContent);

        PartialUpdate update = new PartialUpdate(SparkleDocument.FIELD_ID, cmsContent.getId());

        update.add(SparkleDocument.FIELD_CONTENT, cmsContent.getContent());
        update.add(SparkleDocument.FIELD_SUMMARY, cmsContent.getSummary());
        update.add(SparkleDocument.FIELD_TITLE, cmsContent.getTitle());

        solrTemplate.saveBean(update);
        solrTemplate.commit();
    }
}
