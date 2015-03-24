package ms.cms.service.registration.web;

import ms.cms.domain.CmsUser;
import ms.cms.service.registration.business.RegistrationException;
import ms.cms.service.registration.business.RegistrationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * RegistrationService
 * Created by bazzoni on 24/03/2015.
 */
@RestController
@RequestMapping(value = "/registration")
public class RegistrationService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RegistrationManager registrationManager;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void createUser(HttpServletResponse response,
                           @RequestParam(value = "type") String type,
                           @RequestParam(value = "username") String username,
                           @RequestParam(value = "password") String password,
                           @RequestParam(value = "email") String email,
                           @RequestParam(value = "firstName") String firstName,
                           @RequestParam(value = "lastName") String lastName) throws IOException {
        try {
            registrationManager.createUser(RegistrationManager.getUserType(type), username, password, email, firstName, lastName);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot create user. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }
    }

    @RequestMapping(value = "/by-username", method = RequestMethod.GET)
    public CmsUser findByUsername(HttpServletResponse response, @RequestParam(value = "username") String username) throws IOException {
        try {
            return registrationManager.findByUsername(username);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot find user. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }

        return null;
    }

    @RequestMapping(value = "/by-email", method = RequestMethod.GET)
    public CmsUser findByEmail(HttpServletResponse response, @RequestParam(value = "email") String email) throws IOException {
        try {
            return registrationManager.findByEmail(email);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot find user. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }

        return null;
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.PUT)
    public void editUser(HttpServletResponse response,
                         @PathVariable(value = "id") String id,
                         @RequestParam(value = "password", defaultValue = "") String password,
                         @RequestParam(value = "firstName", defaultValue = "") String firstName,
                         @RequestParam(value = "lastName", defaultValue = "") String lastName) throws IOException {
        try {
            registrationManager.editUser(id, password, firstName, lastName);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot edit user. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }
    }
}
