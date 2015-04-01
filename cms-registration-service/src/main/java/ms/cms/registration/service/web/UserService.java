package ms.cms.registration.service.web;

import ms.cms.domain.CmsUser;
import ms.cms.registration.common.business.RegistrationException;
import ms.cms.registration.common.business.RegistrationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * UserService
 * Created by bazzoni on 24/03/2015.
 */
@RestController
@RequestMapping(value = "/public")
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RegistrationManager registrationManager;

    @RequestMapping(value = "/user/{type}", method = RequestMethod.POST)
    public void createUser(HttpServletResponse response,
                           @PathVariable(value = "type") String type,
                           @RequestBody CmsUser cmsUser) throws IOException {
        try {
            registrationManager.createUser(RegistrationManager.getUserType(type.toUpperCase()), cmsUser.getUsername(), cmsUser.getPassword(), cmsUser.getEmail(), cmsUser.getName());
        } catch (RegistrationException e) {
            String msg = String.format("Cannot create user. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }
    }

    @RequestMapping(value = "/user/{param}", method = RequestMethod.GET)
    public CmsUser findUser(HttpServletResponse response, @PathVariable(value = "param") String param) throws IOException {
        try {
            return registrationManager.findUser(param);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot find user. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }

        return null;
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
    public void editUser(HttpServletResponse response,
                         @PathVariable(value = "id") String id,
                         @RequestBody CmsUser cmsUser) throws IOException {
        try {
            registrationManager.editUser(id, cmsUser.getPassword(), cmsUser.getName());
        } catch (RegistrationException e) {
            String msg = String.format("Cannot edit user. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public void deleteUser(HttpServletResponse response,
                           @PathVariable(value = "id") String id) throws IOException {
        try {
            registrationManager.deleteUser(id);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot edit user. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }
    }
}
