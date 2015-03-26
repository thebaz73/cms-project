package ms.cms.service.registration.business;

import ms.cms.data.CmsRoleRepository;
import ms.cms.data.CmsSiteRepository;
import ms.cms.data.CmsUserRepository;
import ms.cms.domain.CmsRole;
import ms.cms.domain.CmsSite;
import ms.cms.domain.CmsUser;
import ms.cms.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
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
        CmsUser cmsUser = new CmsUser(fullName, email, username, password, new Date(), roles);

        cmsUserRepository.save(cmsUser);
    }

    public CmsUser findUser(String param) throws RegistrationException {
        List<CmsUser> byUsername = cmsUserRepository.findByUsername(param);
        if (!byUsername.isEmpty()) {
            return byUsername.get(0);
        }

        List<CmsUser> byEmail = cmsUserRepository.findByEmail(param);
        if (!byEmail.isEmpty()) {
            return byEmail.get(0);
        }

        throw new RegistrationException("Wrong search parameter");
    }

    public void editUser(String id, String password, String firstName, String lastName) throws RegistrationException {
        CmsUser cmsUser = cmsUserRepository.findOne(id);
        if (cmsUser == null) {
            throw new RegistrationException("User id not found");
        }
        boolean modified = false;
        if (!firstName.isEmpty() && !lastName.isEmpty()) {
            String fullName = firstName + " " + lastName;
            cmsUser.setName(fullName);
            modified = true;
        }
        if (!password.isEmpty()) {
            cmsUser.setPassword(password);
            modified = true;
        }

        if (modified) {
            cmsUserRepository.save(cmsUser);
        }
    }

    public void deleteUser(String id) throws RegistrationException {
        CmsUser cmsUser = cmsUserRepository.findOne(id);
        if (cmsUser == null) {
            throw new RegistrationException("User id not found");
        }

        List<CmsSite> byWebMaster = cmsSiteRepository.findByWebMaster(cmsUser);
        for (CmsSite cmsSite : byWebMaster) {
            cmsSiteRepository.delete(cmsSite);
        }

        cmsUserRepository.delete(cmsUser);
    }

    public void createSite(String userId, String name, String address) throws RegistrationException {
        CmsUser cmsUser = cmsUserRepository.findOne(userId);
        if (cmsUser == null) {
            throw new RegistrationException("User id not found");
        }
        if (!cmsSiteRepository.findByAddress(address).isEmpty()) {
            throw new RegistrationException("Site already registered");
        }

        CmsSite cmsSite = new CmsSite();
        cmsSite.setName(name);
        cmsSite.setAddress(address);
        cmsSite.setWebMaster(cmsUser);

        cmsSiteRepository.save(cmsSite);
    }

    public CmsSite findSite(String param) throws RegistrationException {
        List<CmsUser> byUsername = cmsUserRepository.findByUsername(param);
        if (!byUsername.isEmpty()) {
            List<CmsSite> byWebMaster = cmsSiteRepository.findByWebMaster(byUsername.get(0));
            if (!byWebMaster.isEmpty()) {
                return byWebMaster.get(0);
            }
        }

        List<CmsSite> byAddress = cmsSiteRepository.findByAddress(param);
        if (!byAddress.isEmpty()) {
            return byAddress.get(0);
        }

        throw new RegistrationException("Wrong search parameter");
    }

    public void editSite(String id, String name) throws RegistrationException {
        CmsSite cmsSite = cmsSiteRepository.findOne(id);
        if (cmsSite == null) {
            throw new RegistrationException("Site id not found");
        }

        boolean modified = false;
        if (!name.isEmpty()) {
            cmsSite.setName(name);
            modified = true;
        }

        if (modified) {
            cmsSiteRepository.save(cmsSite);
        }
    }

    public void deleteSite(String id) throws RegistrationException {
        CmsSite cmsSite = cmsSiteRepository.findOne(id);
        if (cmsSite == null) {
            throw new RegistrationException("Site id not found");
        }

        cmsSiteRepository.delete(cmsSite);
    }

    public void addSiteAuthor(String id, String userId) throws RegistrationException {
        CmsUser cmsUser = cmsUserRepository.findOne(userId);
        if (cmsUser == null) {
            throw new RegistrationException("User id not found");
        }
        if (cmsUser.getRoles().stream().noneMatch(r -> r.getRole().equals(Role.ROLE_AUTHOR))) {
            throw new RegistrationException("Author not found");
        }

        CmsSite cmsSite = cmsSiteRepository.findOne(id);
        if (cmsSite == null) {
            throw new RegistrationException("Site id not found");
        }

        cmsSite.getAuthors().add(cmsUser);
        cmsSiteRepository.save(cmsSite);
    }

    public void removeSiteAuthor(String id, String userId) throws RegistrationException {
        CmsUser cmsUser = cmsUserRepository.findOne(userId);
        if (cmsUser == null) {
            throw new RegistrationException("User id not found");
        }

        CmsSite cmsSite = cmsSiteRepository.findOne(id);
        if (cmsSite == null) {
            throw new RegistrationException("Site id not found");
        }

        cmsSite.getAuthors().stream().filter(user -> user.getId().equals(cmsUser.getId())).forEach(user -> {
            cmsSite.getAuthors().remove(user);
        });
        cmsSiteRepository.save(cmsSite);
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
