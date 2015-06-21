package sparkle.cms.registration.common.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sparkle.cms.data.CmsRoleRepository;
import sparkle.cms.data.CmsSiteRepository;
import sparkle.cms.data.CmsUserRepository;
import sparkle.cms.domain.*;

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
    private CmsRole roleAdmin;
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
        if (type.equalsIgnoreCase("ADMIN")) return UserType.ADMIN;
        if (type.equalsIgnoreCase("MANAGER")) return UserType.MANAGER;
        if (type.equalsIgnoreCase("AUTHOR")) return UserType.AUTHOR;
        if (type.equalsIgnoreCase("VIEWER")) return UserType.VIEWER;

        throw new RegistrationException("Wrong user type");
    }

    public void initialize() {
        for (Role role : Role.ALL) {
            List<CmsRole> byRole = cmsRoleRepository.findByRole(role.getName());
            if (byRole.isEmpty()) {
                CmsRole cmsRole = new CmsRole(role.getName());
                cmsRoleRepository.save(cmsRole);
            }
        }
        roleUser = cmsRoleRepository.findByRole("ROLE_USER").get(0);
        roleAdmin = cmsRoleRepository.findByRole("ROLE_ADMIN").get(0);
        roleManager = cmsRoleRepository.findByRole("ROLE_MANAGER").get(0);
        roleAuthor = cmsRoleRepository.findByRole("ROLE_AUTHOR").get(0);
        roleViewer = cmsRoleRepository.findByRole("ROLE_VIEWER").get(0);
    }

    public void createUser(UserType userType, String username, String password, String email, String fullName) throws RegistrationException {
        if (!cmsUserRepository.findByUsername(username).isEmpty()) {
            throw new RegistrationException("Username already registered");
        }
        if (!cmsUserRepository.findByEmail(email).isEmpty()) {
            throw new RegistrationException("Email already registered");
        }

        List<CmsRole> roles = doGuessRoles(userType);

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

    public void editUser(String id, String username, String password, String fullName) throws RegistrationException {
        CmsUser cmsUser = cmsUserRepository.findOne(id);
        if (cmsUser == null) {
            throw new RegistrationException("User id not found");
        }
        boolean modified = false;
        if (!username.isEmpty()) {
            cmsUser.setUsername(username);
            modified = true;
        }
        if (!fullName.isEmpty()) {
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
        byWebMaster.forEach(cmsSiteRepository::delete);

        cmsUserRepository.delete(cmsUser);
    }

    public void createSite(String userId, String name, String address, WorkflowType workflowType, CommentApprovalMode commentApprovalMode) throws RegistrationException {
        CmsUser cmsUser = cmsUserRepository.findOne(userId);
        if (cmsUser == null) {
            throw new RegistrationException("User id not found");
        }
        if (!cmsSiteRepository.findByAddress(address).isEmpty()) {
            throw new RegistrationException("Site already registered");
        }

        CmsSite cmsSite = new CmsSite(name, new Date(), address, workflowType, commentApprovalMode, cmsUser);

        cmsSiteRepository.save(cmsSite);
    }

    public List<CmsSite> findSites(String param) throws RegistrationException {
        CmsUser cmsUser = cmsUserRepository.findOne(param);
        if (cmsUser != null) {
            return cmsSiteRepository.findByWebMaster(cmsUser);
        }

        List<CmsUser> byUsername = cmsUserRepository.findByUsername(param);
        if (!byUsername.isEmpty()) {
            return cmsSiteRepository.findByWebMaster(byUsername.get(0));
        }

        throw new RegistrationException("Wrong search parameter");
    }

    public CmsSite findSite(String param) throws RegistrationException {
        CmsSite cmsSite = cmsSiteRepository.findOne(param);
        if (cmsSite != null) {
            return cmsSite;
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
        final CmsSite cmsSite = cmsSiteRepository.findOne(id);
        if (cmsSite == null) {
            throw new RegistrationException("Site id not found");
        }
        cmsSite.getAuthors().forEach(a -> removeSiteAuthor(cmsSite, a));

        cmsSiteRepository.delete(cmsSite);
    }

    public CmsSite findAuthoredSite(String param) throws RegistrationException {
        CmsUser cmsUser = cmsUserRepository.findOne(param);
        if (cmsUser != null && cmsUser.getAuthoredSiteId() != null) {
            return cmsSiteRepository.findOne(cmsUser.getAuthoredSiteId());
        }

        List<CmsUser> cmsUsers = cmsUserRepository.findByUsername(param);
        if (!cmsUsers.isEmpty()) {
            return cmsSiteRepository.findOne(cmsUsers.get(0).getAuthoredSiteId());
        }

        throw new RegistrationException("Wrong search parameter");
    }

    public List<CmsUser> findSiteAuthors(String param) throws RegistrationException {
        ArrayList<CmsUser> cmsUsers = new ArrayList<>();
        CmsSite cmsSite = cmsSiteRepository.findOne(param);
        cmsUsers.add(cmsSite.getWebMaster());
        cmsUsers.addAll(cmsSite.getAuthors());

        return cmsUsers;
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

        if (cmsSite.getAuthors().stream().noneMatch(a -> a.getId().equals(cmsUser.getId()))) {
            cmsSite.getAuthors().add(cmsUser);
            cmsSiteRepository.save(cmsSite);
            cmsUser.setAuthoredSiteId(cmsSite.getId());
            cmsUserRepository.save(cmsUser);
        }
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

        removeSiteAuthor(cmsSite, cmsUser);
    }

    private void removeSiteAuthor(CmsSite cmsSite, CmsUser cmsUser) {
        List<CmsUser> authors = new ArrayList<>(cmsSite.getAuthors());
        authors.stream().filter(user -> user.getId().equals(cmsUser.getId())).forEach(user -> cmsSite.getAuthors().remove(user));
        cmsSiteRepository.save(cmsSite);
        cmsUser.setAuthoredSiteId(null);
        cmsUserRepository.save(cmsUser);
    }

    private List<CmsRole> doGuessRoles(UserType userType) {
        List<CmsRole> roles = new ArrayList<>();
        roles.add(roleUser);
        switch (userType) {
            case ADMIN:
                roles.add(roleAdmin);
                break;
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


    public enum UserType {
        ADMIN, MANAGER, AUTHOR, VIEWER
    }
}
