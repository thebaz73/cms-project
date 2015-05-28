package sparkle.cms.solr.data;

import sparkle.cms.domain.CmsContent;

/**
 * PartialUpdateRepository
 * Created by bazzoni on 28/05/2015.
 */
public interface PartialUpdateRepository {
    /**
     * Update solr index
     *
     * @param cmsContent content
     */
    public void update(CmsContent cmsContent);
}
