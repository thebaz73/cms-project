package ms.cms.authoring.ui.web;

import ms.cms.authoring.common.business.AuthoringManager;
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
import java.util.Date;
import java.util.Map;

/**
 * UserController
 * Created by thebaz on 12/04/15.
 */
@Controller(value = "userController")
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RegistrationManager registrationManager;
    @Autowired
    private AuthoringManager authoringManager;

    @RequestMapping({"/profile"})
    public String userProfile(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) throws IOException {
        model.put("date", new Date());
        try {
            CmsUser cmsUser = registrationManager.findUser(request.getRemoteUser());
            model.put("cmsUser", cmsUser);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot find user. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return "profile";
    }

    @RequestMapping(value = {"/profile"}, method = RequestMethod.PUT)
    public String editUserProfile(HttpServletRequest request, HttpServletResponse response, CmsUser editUser, Map<String, Object> model) throws IOException {
        model.put("date", new Date());
        try {
            registrationManager.editUser(editUser.getId(), editUser.getUsername(), editUser.getPassword(), editUser.getName());
            CmsUser cmsUser = registrationManager.findUser(request.getRemoteUser());
            model.put("cmsUser", cmsUser);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot edit user. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return "profile";
    }
}
