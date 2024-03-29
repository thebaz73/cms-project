package sparkle.cms.authoring.common.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import sparkle.cms.data.CmsContentRepository;
import sparkle.cms.data.CmsSiteRepository;
import sparkle.cms.domain.CmsContent;
import sparkle.cms.domain.CmsSite;
import sparkle.cms.domain.CmsUser;

import java.util.ArrayList;
import java.util.List;

/**
 * ContentManager
 * Created by thebaz on 14/04/15.
 */
@Component
public class ContentManager {
    @Autowired
    private CmsSiteRepository cmsSiteRepository;
    @Autowired
    private CmsContentRepository cmsContentRepository;

    public Page<CmsContent> findAllContents(CmsUser cmsUser, Pageable pageable) {
        List<CmsSite> cmsSites = cmsSiteRepository.findByWebMaster(cmsUser);
        List<CmsContent> cmsContents = new ArrayList<>();
        for (CmsSite cmsSite : cmsSites) {
            cmsContents.addAll(cmsContentRepository.findBySiteId(cmsSite.getId()));
        }
        return new PageImpl<>(cmsContents, pageable, cmsContents.size());
    }

    public Page<CmsContent> findAuthoredContents(CmsUser cmsUser, Pageable pageable) {
        CmsSite cmsSite = cmsSiteRepository.findOne(cmsUser.getAuthoredSiteId());
        List<CmsContent> cmsContents = cmsContentRepository.findBySiteId(cmsSite.getId());
        return new PageImpl<>(cmsContents, pageable, cmsContents.size());
    }

    public List<CmsContent> findAllContents(CmsUser cmsUser) {
        List<CmsSite> cmsSites = cmsSiteRepository.findByWebMaster(cmsUser);
        List<CmsContent> cmsContents = new ArrayList<>();
        for (CmsSite cmsSite : cmsSites) {
            cmsContents.addAll(cmsContentRepository.findBySiteId(cmsSite.getId()));
        }
        return cmsContents;
    }

    public List<CmsContent> findAuthoredContents(CmsUser cmsUser) {
        CmsSite cmsSite = cmsSiteRepository.findOne(cmsUser.getAuthoredSiteId());
        return cmsContentRepository.findBySiteId(cmsSite.getId());
    }

    public List<CmsContent> findSitesContents(List<CmsSite> cmsSites) {
        List<CmsContent> cmsContents = new ArrayList<>();
        for (CmsSite cmsSite : cmsSites) {
            cmsContents.addAll(cmsContentRepository.findBySiteId(cmsSite.getId()));
        }
        return cmsContents;
    }

    public void deleteSiteContents(CmsSite cmsSite) {
        cmsContentRepository.deleteBySiteId(cmsSite.getId());
    }

    public void publish(String contentId, boolean b) {
        CmsContent cmsContent = cmsContentRepository.findOne(contentId);
        cmsContent.setPublished(b);
        cmsContentRepository.save(cmsContent);
    }
}
