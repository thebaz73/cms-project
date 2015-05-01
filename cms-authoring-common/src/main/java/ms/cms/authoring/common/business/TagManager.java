package ms.cms.authoring.common.business;

import ms.cms.data.CmsTagRepository;
import ms.cms.domain.CmsTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
