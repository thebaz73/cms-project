package sparkle.cms.authoring.common.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import sparkle.cms.data.CmsPageRepository;
import sparkle.cms.data.CmsSiteRepository;
import sparkle.cms.domain.CmsPage;
import sparkle.cms.domain.CmsSite;
import sparkle.cms.domain.CmsUser;

import java.util.ArrayList;
import java.util.List;

import static sparkle.cms.authoring.common.utils.AuthoringUtils.toPrettyURL;

/**
 * PageManager
 * Created by bazzoni on 08/07/2015.
 */
@Component
public class PageManager {
    @Autowired
    private CmsSiteRepository cmsSiteRepository;
    @Autowired
    private CmsPageRepository cmsPageRepository;

    public Page<CmsPage> findAllPages(CmsUser cmsUser, Pageable pageable) {
        List<CmsSite> cmsSites = cmsSiteRepository.findByWebMaster(cmsUser);
        List<CmsPage> cmsPages = new ArrayList<>();
        for (CmsSite cmsSite : cmsSites) {
            cmsPages.addAll(cmsPageRepository.findBySiteId(cmsSite.getId()));
        }
        return new PageImpl<>(cmsPages, pageable, cmsPages.size());
    }

    public Page<CmsPage> findAuthoredPages(CmsUser cmsUser, Pageable pageable) {
        CmsSite cmsSite = cmsSiteRepository.findOne(cmsUser.getAuthoredSiteId());
        List<CmsPage> cmsPages = cmsPageRepository.findBySiteId(cmsSite.getId());
        return new PageImpl<>(cmsPages, pageable, cmsPages.size());
    }

    public void createPage(CmsPage cmsPage) {
        cmsPage.setName(toPrettyURL(cmsPage.getTitle()));
        final CmsPage parent = cmsPage.getParent();
        if (parent == null) {
            cmsPage.setUri("/");
        } else {
            cmsPage.setUri(parent.getUri() + "/" + toPrettyURL(cmsPage.getTitle()));
        }
        cmsPageRepository.save(cmsPage);
    }

    public CmsPage findPage(String pageId) {
        return cmsPageRepository.findOne(pageId);
    }

    public void deletePage(String pageId) {
        cmsPageRepository.delete(pageId);
    }
}
