package ms.cms.service.registration.web;

import ms.cms.domain.CmsSite;
import ms.cms.service.registration.business.RegistrationException;
import ms.cms.service.registration.business.RegistrationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * SiteService
 * Created by thebaz on 24/03/15.
 */
@RestController
@RequestMapping(value = "/site")
public class SiteService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RegistrationManager registrationManager;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void createUser(HttpServletResponse response,
                           @RequestParam(value = "userId") String userId,
                           @RequestParam(value = "name") String name,
                           @RequestParam(value = "address") String address) throws IOException {
        try {
            registrationManager.createSite(userId, name, address);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot create site. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }
    }

    @RequestMapping(value = "/find", method = RequestMethod.GET)
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

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.PUT)
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

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
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
}
