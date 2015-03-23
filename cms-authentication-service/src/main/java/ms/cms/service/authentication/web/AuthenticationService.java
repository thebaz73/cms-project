package ms.cms.service.authentication.web;

import ms.cms.domain.CmsUser;
import ms.cms.service.authentication.business.ReadOnlyUserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * AuthenticationService
 * Created by bazzoni on 23/03/2015.
 */
@RestController
@RequestMapping(value = "/auth")
public class AuthenticationService {
    @Autowired
    private ReadOnlyUserManager userManager;

    @Secured("ROLE_USER")
    @RequestMapping(value = "/whoami")
    public CmsUser whoAmI(HttpServletRequest request) {
        CmsUser cmsUser = userManager.findUser(request.getRemoteUser());
        //password hiding
        cmsUser.setPassword("");

        return cmsUser;
    }
}
