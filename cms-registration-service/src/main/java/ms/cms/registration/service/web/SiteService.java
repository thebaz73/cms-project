package ms.cms.registration.service.web;

import ms.cms.domain.CmsSite;
import ms.cms.domain.CmsUser;
import ms.cms.domain.WorkflowType;
import ms.cms.registration.common.business.RegistrationException;
import ms.cms.registration.common.business.RegistrationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * SiteService
 * Created by thebaz on 24/03/15.
 */
@RestController(value = "siteWebService")
@RequestMapping(value = "/api")
public class SiteService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RegistrationManager registrationManager;

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/site/{userId}", method = RequestMethod.POST)
    public void createSite(HttpServletResponse response,
                           @PathVariable(value = "userId") String userId,
                           @RequestParam(value = "workflow", defaultValue = "SELF_APPROVAL_WF") String workflowType,
                           @RequestBody CmsSite cmsSite) throws IOException {
        try {
            registrationManager.createSite(userId, cmsSite.getName(), cmsSite.getAddress(), WorkflowType.forName(workflowType.toUpperCase()));
        } catch (RegistrationException e) {
            String msg = String.format("Cannot create site. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/sites", method = RequestMethod.GET)
    public List<CmsSite> findSites(HttpServletResponse response, @RequestParam(value = "param") String param) throws IOException {
        try {
            return registrationManager.findSites(param);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot find site. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }

        return null;
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/site", method = RequestMethod.GET)
    public CmsSite findSite(HttpServletResponse response, @RequestParam(value = "param") String param) throws IOException {
        try {
            return registrationManager.findSite(param);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot find site. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }

        return null;
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/site/authored", method = RequestMethod.GET)
    public CmsSite findAuthoredSite(HttpServletResponse response, @RequestParam(value = "param") String param) throws IOException {
        try {
            return registrationManager.findAuthoredSite(param);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot find site. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }

        return null;
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/site/{id}", method = RequestMethod.PUT)
    public void editSite(HttpServletResponse response,
                         @PathVariable(value = "id") String id,
                         @RequestBody CmsSite cmsSite) throws IOException {
        try {
            registrationManager.editSite(id, cmsSite.getName());
        } catch (RegistrationException e) {
            String msg = String.format("Cannot edit site. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/site/{id}", method = RequestMethod.DELETE)
    public void deleteSite(HttpServletResponse response,
                           @PathVariable(value = "id") String id) throws IOException {
        try {
            registrationManager.deleteSite(id);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot edit site. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/sites/authors", method = RequestMethod.GET)
    public List<CmsUser> findSiteAuthors(HttpServletResponse response, @RequestParam(value = "param") String param) throws IOException {
        try {
            return registrationManager.findSiteAuthors(param);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot find site. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }

        return null;
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/site/author/{id}/{userId}", method = RequestMethod.POST)
    public void addSiteAuthor(HttpServletResponse response,
                              @PathVariable("id") String id,
                              @PathVariable(value = "userId") String userId) throws IOException {
        try {
            registrationManager.addSiteAuthor(id, userId);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot add author site. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/site/author/{id}/{userId}", method = RequestMethod.DELETE)
    public void removeSiteAuthor(HttpServletResponse response,
                                 @PathVariable("id") String id,
                                 @PathVariable(value = "userId") String userId) throws IOException {
        try {
            registrationManager.removeSiteAuthor(id, userId);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot remove author site. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }
    }
}
