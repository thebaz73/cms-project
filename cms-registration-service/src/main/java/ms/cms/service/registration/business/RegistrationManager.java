package ms.cms.service.registration.business;

import ms.cms.data.CmsRoleRepository;
import ms.cms.data.CmsSiteRepository;
import ms.cms.data.CmsUserRepository;
import ms.cms.domain.CmsRole;
import ms.cms.domain.CmsUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * RegistrationManager
 * Created by bazzoni on 24/03/2015.
 */
@Component
public class RegistrationManager {
    private CmsRole roleUser;
    private CmsRole roleManager;
    private CmsRole roleAuthor;
    private CmsRole roleViewer;
    @Autowired
    private CmsRoleRepository cmsRoleRepository;
    @Autowired
    private CmsUserRepository cmsUserRepository;
    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    public static UserType getUserType(String type) throws RegistrationException {
        if (type.equalsIgnoreCase("MANAGER")) return UserType.MANAGER;
        if (type.equalsIgnoreCase("AUTHOR")) return UserType.AUTHOR;
        if (type.equalsIgnoreCase("VIEWER")) return UserType.VIEWER;

        throw new RegistrationException("Wrong user type");
    }

    @PostConstruct
    public void initialize() {
        roleUser = cmsRoleRepository.findByRole("ROLE_USER").get(0);
        roleManager = cmsRoleRepository.findByRole("ROLE_MANAGER").get(0);
        roleAuthor = cmsRoleRepository.findByRole("ROLE_AUTHOR").get(0);
        roleViewer = cmsRoleRepository.findByRole("ROLE_VIEWER").get(0);
    }

    public void createUser(UserType userType, String username, String password, String email, String firstName, String lastName) throws RegistrationException {
        if (!cmsUserRepository.findByUsername(username).isEmpty()) {
            throw new RegistrationException("Username already registered");
        }
        if (!cmsUserRepository.findByEmail(email).isEmpty()) {
            throw new RegistrationException("Email already registered");
        }
        List<CmsRole> roles = doGuessRoles(userType);
        String fullName = firstName + " " + lastName;
        CmsUser user = new CmsUser(fullName, email, username, password, roles);
        cmsUserRepository.save(user);
    }

    public CmsUser findByUsername(String username) throws RegistrationException {
        List<CmsUser> byUsername = cmsUserRepository.findByUsername(username);
        if (byUsername.isEmpty()) {
            throw new RegistrationException("Username not found");
        }
        return byUsername.get(0);
    }

    public CmsUser findByEmail(String email) throws RegistrationException {
        List<CmsUser> byEmail = cmsUserRepository.findByEmail(email);
        if (byEmail.isEmpty()) {
            throw new RegistrationException("Email not found");
        }
        return byEmail.get(0);
    }

    private List<CmsRole> doGuessRoles(UserType userType) {
        List<CmsRole> roles = new ArrayList<>();
        roles.add(roleUser);
        switch (userType) {
            case MANAGER:
                roles.add(roleManager);
                break;
            case AUTHOR:
                roles.add(roleAuthor);
                break;
            case VIEWER:
                roles.add(roleViewer);
                break;
        }

        return roles;
    }

    public static enum UserType {
        MANAGER, AUTHOR, VIEWER
    }
}
