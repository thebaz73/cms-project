package sparkle.cms.authoring.ui.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sparkle.cms.authoring.common.business.AssetManager;
import sparkle.cms.authoring.common.business.CommentManager;
import sparkle.cms.authoring.common.business.ContentManager;
import sparkle.cms.domain.CmsSite;
import sparkle.cms.domain.CmsUser;
import sparkle.cms.domain.WorkflowType;
import sparkle.cms.registration.common.business.RegistrationException;
import sparkle.cms.registration.common.business.RegistrationManager;
import sparkle.cms.registration.common.business.SiteManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static sparkle.cms.utils.UserUtils.isAuthor;
import static sparkle.cms.utils.UserUtils.isWebmaster;

/**
 * SiteController
 * Created by thebaz on 12/04/15.
 */
@Controller(value = "siteController")
public class SiteController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RegistrationManager registrationManager;
    @Autowired
    private SiteManager siteManager;
    @Autowired
    private ContentManager contentManager;
    @Autowired
    private AssetManager assetManager;
    @Autowired
    private CommentManager commentManager;

    @ModelAttribute("allWorkflowTypes")
    public List<WorkflowType> allRoles() {
        return Arrays.asList(WorkflowType.ALL);
    }

    @ModelAttribute("allSites")
    public Page<CmsSite> allSites(HttpServletRequest request, HttpServletResponse response,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) throws IOException {
        try {
            CmsUser cmsUser = registrationManager.findUser(request.getRemoteUser());
            Pageable pageable = new PageRequest(page, pageSize, Sort.Direction.ASC, "name");
            if (isWebmaster(cmsUser)) {
                return siteManager.findAllSites(cmsUser, pageable);
            } else if (isAuthor(cmsUser)) {
                return siteManager.findAuthoredSites(cmsUser, pageable);
            }
        } catch (RegistrationException e) {
            String msg = String.format("Cannot manage sites. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return null;
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    @RequestMapping(value = {"/site"}, method = RequestMethod.GET)
    public String show(ModelMap model) {
        model.put("cmsSite", new CmsSite());
        return "site";
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    @RequestMapping(value = {"/site"}, method = RequestMethod.POST)
    public String addSite(HttpServletRequest request, HttpServletResponse response, @ModelAttribute("cmsSite") CmsSite cmsSite,
                          final BindingResult bindingResult, final ModelMap model) throws IOException {
        if (bindingResult.hasErrors()) {
            return "site";
        }
        try {
            //TODO check whois for domain access
            //TODO WHOIS_URL="http://www.whoisxmlapi.com/whoisserver/WhoisService";

            CmsUser cmsUser = registrationManager.findUser(request.getRemoteUser());
            String siteId = siteManager.createSite(cmsUser, cmsSite.getName(), cmsSite.getAddress(), cmsSite.getWorkflowType());
            assetManager.createSiteRepository(siteId);
            model.clear();
        } catch (RegistrationException e) {
            String msg = String.format("Cannot create site. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return "redirect:/site";
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    @RequestMapping(value = {"/site/{siteId}"}, method = RequestMethod.DELETE)
    public String deleteSite(HttpServletResponse response, @PathVariable("siteId") String siteId) throws IOException {
        try {
            contentManager.deleteSiteContents(siteManager.findSite(siteId));
            commentManager.deleteSiteComments(siteId);
            registrationManager.deleteSite(siteId);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot create site. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return "redirect:/site";
    }
}
