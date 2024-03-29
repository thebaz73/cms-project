package sparkle.cms.authentication.service.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sparkle.cms.data.CmsUserRepository;
import sparkle.cms.domain.CmsUser;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * AuthenticationService
 * Created by bazzoni on 23/03/2015.
 */
@RestController(value = "authenticationWebService")
@RequestMapping(value = "/api")
public class AuthenticationService {
    @Autowired
    private CmsUserRepository cmsUserRepository;

    @Secured("ROLE_USER")
    @RequestMapping(value = "/whoami")
    public CmsUser whoAmI(HttpServletRequest request) {
        CmsUser cmsUser = null;
        List<CmsUser> byUsername = cmsUserRepository.findByUsername(request.getRemoteUser());
        if (!byUsername.isEmpty()) {
            cmsUser = byUsername.get(0);
            //password hiding
            cmsUser.setPassword("");
        }

        return cmsUser;
    }
}
