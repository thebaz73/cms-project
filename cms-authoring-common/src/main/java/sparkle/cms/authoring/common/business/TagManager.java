package sparkle.cms.authoring.common.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sparkle.cms.data.CmsTagRepository;
import sparkle.cms.domain.CmsTag;

import java.util.List;

/**
 * TagManager
 * Created by thebaz on 01/05/15.
 */
@Component
public class TagManager {
    @Autowired
    private CmsTagRepository cmsTagRepository;

    public List<CmsTag> findSiteTags(String siteId) {
        return cmsTagRepository.findBySiteId(siteId);
    }
}
