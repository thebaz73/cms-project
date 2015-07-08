package sparkle.cms.authoring.ui.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sparkle.cms.authoring.common.business.PageManager;
import sparkle.cms.domain.CmsPage;
import sparkle.cms.domain.CmsSite;
import sparkle.cms.domain.CmsUser;
import sparkle.cms.registration.common.business.RegistrationException;
import sparkle.cms.registration.common.business.RegistrationManager;
import sparkle.cms.registration.common.business.SiteManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static sparkle.cms.utils.UserUtils.isAuthor;
import static sparkle.cms.utils.UserUtils.isWebmaster;

/**
 * PageController
 * Created by bazzoni on 08/07/2015.
 */
@Controller(value = "pageController")
public class PageController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RegistrationManager registrationManager;
    @Autowired
    private SiteManager siteManager;
    @Autowired
    private PageManager pageManager;

    @ModelAttribute("allSites")
    public List<CmsSite> allSites(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            CmsUser cmsUser = registrationManager.findUser(request.getRemoteUser());
            if (isWebmaster(cmsUser)) {
                return siteManager.findAllSites(cmsUser);
            } else if (isAuthor(cmsUser)) {
                return siteManager.findAuthoredSites(cmsUser);
            }
        } catch (RegistrationException e) {
            String msg = String.format("Cannot manage sites. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return null;
    }

    @ModelAttribute("allPages")
    public Page<CmsPage> allPages(HttpServletRequest request, HttpServletResponse response,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) throws IOException {
        try {
            CmsUser cmsUser = registrationManager.findUser(request.getRemoteUser());
            Pageable pageable = new PageRequest(page, pageSize, Sort.Direction.ASC, "id");
            if (isWebmaster(cmsUser)) {
                return pageManager.findAllPages(cmsUser, pageable);
            } else if (isAuthor(cmsUser)) {
                return pageManager.findAuthoredPages(cmsUser, pageable);
            }
        } catch (RegistrationException e) {
            String msg = String.format("Cannot manage pages. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return null;
    }

    @RequestMapping({"/pages"})
    public String show(ModelMap model) {
        model.put("cmsPage", new CmsPage());
        model.put("mode", "add");
        return "pages";
    }

    @RequestMapping(value = {"/pages"}, method = RequestMethod.POST)
    public String addContent(@ModelAttribute("cmsPage") CmsPage cmsPage,
                             final BindingResult bindingResult, final ModelMap model) throws IOException {
        if (bindingResult.hasErrors()) {
            return "pages";
        }
        pageManager.createPage(cmsPage);
        model.clear();
        return "redirect:/pages";
    }

    @RequestMapping(value = {"/pages/{pageId}"}, method = RequestMethod.GET)
    public String show(ModelMap model, @PathVariable("pageId") String pageId) throws IOException {
        CmsPage cmsPage = pageManager.findPage(pageId);
        model.put("cmsPage", cmsPage);
        model.put("mode", "edit");
        return "pages";
    }

    @RequestMapping(value = {"/pages/{pageId}"}, method = RequestMethod.DELETE)
    public String delete(@PathVariable("pageId") String pageId) throws IOException {
        pageManager.deletePage(pageId);
        return "redirect:/pages";
    }
}
