package sparkle.cms.authoring.ui.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sparkle.cms.authoring.common.business.AuthoringManager;
import sparkle.cms.domain.CmsSite;
import sparkle.cms.domain.CmsUser;
import sparkle.cms.plugin.mgmt.search.SparkleDocument;
import sparkle.cms.registration.common.business.RegistrationException;
import sparkle.cms.registration.common.business.RegistrationManager;
import sparkle.cms.registration.common.business.SiteManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static sparkle.cms.utils.UserUtils.isAuthor;
import static sparkle.cms.utils.UserUtils.isWebmaster;

/**
 * SearchController
 * Created by thebaz on 12/04/15.
 */
@Controller(value = "searchController")
public class SearchController {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private RegistrationManager registrationManager;
    @Autowired
    private AuthoringManager authoringManager;
    @Autowired
    private SiteManager siteManager;

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER", "ROLE_AURTHOR"})
    @RequestMapping(value = {"/search"}, params = {"query"}, method = RequestMethod.GET)
    public String search(HttpServletRequest request, HttpServletResponse response, ModelMap model, String query) throws IOException {
        try {
            List<CmsSite> allSites = new ArrayList<>();
            CmsUser cmsUser = registrationManager.findUser(request.getRemoteUser());
            if (isWebmaster(cmsUser)) {
                allSites = siteManager.findAllSites(cmsUser);
            } else if (isAuthor(cmsUser)) {
                allSites = siteManager.findAuthoredSites(cmsUser);
            }
            model.put("date", new Date());
            model.put("query", query);

            List<? extends SparkleDocument> docs = authoringManager.searchContent(allSites.get(0).getId(), query);
            model.put("docs", docs);
            return "search";
        } catch (RegistrationException e) {
            String msg = String.format("Cannot manage contents. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return "";
    }
}
