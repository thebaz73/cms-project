package ms.cms.authoring.ui.web;

import ms.cms.authoring.common.business.AuthoringManager;
import ms.cms.domain.CmsSite;
import ms.cms.domain.CmsUser;
import ms.cms.registration.common.business.RegistrationException;
import ms.cms.registration.common.business.RegistrationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static ms.cms.utils.UserUtils.isAdmin;
import static ms.cms.utils.UserUtils.isWebmaster;

/**
 * NavigationController
 * Created by thebaz on 02/04/15.
 */
@Controller(value = "navigationController")
public class NavigationController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RegistrationManager registrationManager;
    @Autowired
    private AuthoringManager authoringManager;

    @RequestMapping({"/"})
    public String home() {
        return "redirect:home";
    }

    @RequestMapping({"home"})
    public String dashboard(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) throws IOException {
        model.put("date", new Date());
        try {
            CmsUser cmsUser = registrationManager.findUser(request.getRemoteUser());
            if (isAdmin(cmsUser)) {
                model.put("siteCount", 0);
                model.put("contentsCount", 0);
                model.put("authorCount", 0);
                model.put("commentsCount", 0);
            } else if (isWebmaster(cmsUser)) {
                List<CmsSite> cmsSites = registrationManager.findSites(cmsUser.getId());
                model.put("siteCount", cmsSites.size());
                int contentsCount = 0;
                int authorCount = 0;
                for (CmsSite cmsSite : cmsSites) {
                    contentsCount += authoringManager.countContents(cmsSite);
                    authorCount += registrationManager.findSiteAuthors(cmsSite.getId()).size();
                }
                model.put("contentsCount", contentsCount);
                model.put("authorCount", authorCount);
                model.put("commentsCount", 0);
            } else {
                model.put("siteCount", 1);
                CmsSite cmsSite = registrationManager.findAuthoredSite(cmsUser.getId());
                int contentsCount = authoringManager.countContents(cmsSite);
                int authorCount = registrationManager.findSiteAuthors(cmsSite.getId()).size();
                model.put("contentsCount", contentsCount);
                model.put("authorCount", authorCount);
                model.put("commentsCount", 0);
            }
        } catch (RegistrationException e) {
            String msg = String.format("Cannot create dashboard. Reason: %s", e.getMessage());
            logger.info(msg);
            response.sendError(400, msg);
        }
        return "index";
    }

    @RequestMapping({"/user"})
    public String userProfile(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) throws IOException {
        model.put("date", new Date());
        try {
            CmsUser cmsUser = registrationManager.findUser(request.getRemoteUser());
            model.put("cmsUser", cmsUser);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot find user. Reason: %s", e.getMessage());
            logger.info(msg);
            response.sendError(400, msg);
        }
        return "user";
    }

    @RequestMapping(value = {"/user"}, method = RequestMethod.PUT)
    public String editUserProfile(HttpServletRequest request, HttpServletResponse response, CmsUser editUser, Map<String, Object> model) throws IOException {
        model.put("date", new Date());
        try {
            registrationManager.editUser(editUser.getId(), editUser.getUsername(), editUser.getPassword(), editUser.getName());
            CmsUser cmsUser = registrationManager.findUser(request.getRemoteUser());
            model.put("cmsUser", cmsUser);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot edit user. Reason: %s", e.getMessage());
            logger.info(msg);
            response.sendError(400, msg);
        }
        return "user";
    }

    @RequestMapping({"/settings"})
    public String appSettings(Map<String, Object> model) {
        model.put("date", new Date());
        return "settings";
    }

    @RequestMapping({"/contents"})
    public String contentsAuthoring(Map<String, Object> model) {
        model.put("date", new Date());
        return "contents";
    }

    @RequestMapping({"/comments"})
    public String commentsManagement(Map<String, Object> model) {
        model.put("date", new Date());
        return "comments";
    }

    @RequestMapping({"/site"})
    public String siteManagement(Map<String, Object> model) {
        model.put("date", new Date());
        return "site";
    }

    @RequestMapping(value = {"/search"}, params = {"query"}, method = RequestMethod.GET)
    public String search(Map<String, Object> model, String query) {
        model.put("date", new Date());
        model.put("docs", new ArrayList<>());
        model.put("query", query);
        return "search";
    }
}
