package ms.cms.registration.service.web;

import ms.cms.domain.CmsSite;
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

/**
 * SiteService
 * Created by thebaz on 24/03/15.
 */
@RestController
@RequestMapping(value = "/api")
public class SiteService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RegistrationManager registrationManager;

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    @RequestMapping(value = "/site", method = RequestMethod.POST)
    public void createUser(HttpServletResponse response,
                           @RequestParam(value = "userId") String userId,
                           @RequestParam(value = "name") String name,
                           @RequestParam(value = "address") String address,
                           @RequestParam(value = "address") String workflowType) throws IOException {
        try {
            registrationManager.createSite(userId, name, address, WorkflowType.forName(workflowType));
        } catch (RegistrationException e) {
            String msg = String.format("Cannot create site. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    @RequestMapping(value = "/site/{param}", method = RequestMethod.GET)
    public CmsSite findSite(HttpServletResponse response, @PathVariable(value = "param") String param) throws IOException {
        try {
            return registrationManager.findSite(param);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot find site. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }

        return null;
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    @RequestMapping(value = "/site/{id}", method = RequestMethod.PUT)
    public void editSite(HttpServletResponse response,
                         @PathVariable(value = "id") String id,
                         @RequestParam(value = "name", defaultValue = "") String name) throws IOException {
        try {
            registrationManager.editSite(id, name);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot edit site. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
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

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    @RequestMapping(value = "/site/author/{id}", method = RequestMethod.POST)
    public void addSiteAuthor(HttpServletResponse response,
                              @PathVariable("id") String id,
                              @RequestParam(value = "userId") String userId) throws IOException {
        try {
            registrationManager.addSiteAuthor(id, userId);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot add author site. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    @RequestMapping(value = "/site/author/{id}", method = RequestMethod.DELETE)
    public void removeSiteAuthor(HttpServletResponse response,
                                 @PathVariable("id") String id,
                                 @RequestParam(value = "userId") String userId) throws IOException {
        try {
            registrationManager.removeSiteAuthor(id, userId);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot remove author site. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }
    }
}
