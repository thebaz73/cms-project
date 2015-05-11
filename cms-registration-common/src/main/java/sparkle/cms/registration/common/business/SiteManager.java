package sparkle.cms.registration.common.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import sparkle.cms.data.CmsSiteRepository;
import sparkle.cms.domain.CmsSite;
import sparkle.cms.domain.CmsUser;
import sparkle.cms.domain.WorkflowType;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * SiteManager
 * Created by thebaz on 14/04/15.
 */
@Component
public class SiteManager {
    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    public List<CmsSite> findAllSites(CmsUser cmsUser) {
        return cmsSiteRepository.findByWebMaster(cmsUser);
    }

    public List<CmsSite> findAuthoredSites(CmsUser cmsUser) {
        CmsSite cmsSite = cmsSiteRepository.findOne(cmsUser.getAuthoredSiteId());
        return Arrays.asList(cmsSite);
    }

    public Page<CmsSite> findAllSites(CmsUser cmsUser, Pageable pageable) {
        return cmsSiteRepository.findByWebMaster(cmsUser, pageable);
    }

    public Page<CmsSite> findAuthoredSites(CmsUser cmsUser, Pageable pageable) {
        CmsSite cmsSite = cmsSiteRepository.findOne(cmsUser.getAuthoredSiteId());
        return new PageImpl<>(Arrays.asList(cmsSite), pageable, 1);
    }

    public String createSite(CmsUser cmsUser, String name, String address, WorkflowType workflowType) throws RegistrationException {
        if (!cmsSiteRepository.findByAddress(address).isEmpty()) {
            throw new RegistrationException("Site already registered");
        }

        CmsSite cmsSite = new CmsSite(name, new Date(), address, workflowType, cmsUser);

        cmsSiteRepository.save(cmsSite);

        return cmsSite.getId();
    }

    public List<CmsSite> findSites(CmsUser cmsUser) {
        return cmsSiteRepository.findByWebMaster(cmsUser);
    }

    public CmsSite findAuthoredSite(CmsUser cmsUser) {
        return cmsSiteRepository.findOne(cmsUser.getId());
    }

    public CmsSite findSite(String siteId) {
        return cmsSiteRepository.findOne(siteId);
    }
}