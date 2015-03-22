package ms.cms.service.authentication.business;

import ms.cms.data.CmsUserRepository;
import ms.cms.domain.CmsRole;
import ms.cms.domain.CmsUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * UserManager
 * Created by thebaz on 21/03/15.
 */
public class UserManager implements UserDetailsManager {
    private final Log logger = LogFactory.getLog(getClass());
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    private CmsUserRepository cmsUserRepository;

//    @Autowired
//    public UserManager(DataSource dataSource) {
//        super.setDataSource(dataSource);
//    }

    public PasswordEncoder getEncoder() {
        return encoder;
    }

    /**
     * Create a new user with the supplied details.
     *
     * @param user
     */
    @Override
    public void createUser(UserDetails user) {

    }

    /**
     * Update the specified user.
     *
     * @param user
     */
    @Override
    public void updateUser(UserDetails user) {

    }

    /**
     * Remove the user with the given login name from the system.
     *
     * @param username
     */
    @Override
    public void deleteUser(String username) {

    }

    /**
     * Modify the current user's password. This should change the user's password in
     * the persistent user repository (datbase, LDAP etc).
     *
     * @param oldPassword current password (for re-authentication if required)
     * @param newPassword the password to change to
     */
    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    /**
     * Check if a user with the supplied login name exists in the system.
     *
     * @param username
     */
    @Override
    public boolean userExists(String username) {
        return false;
    }

    /**
     * Locates the user based on the username. In the actual implementation, the search may possibly be case
     * sensitive, or case insensitive depending on how the implementation instance is configured. In this case, the
     * <code>UserDetails</code> object that comes back may have a username that is of a different case than what was
     * actually requested..
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException if the user could not be found or the user has no GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

    /**
     * Find all users
     *
     * @return user list
     */
    public List<CmsUser> findAllUsers() {
        return cmsUserRepository.findAll();
    }

    public boolean userCreated(String username) {
        return !cmsUserRepository.findByUsername(username).isEmpty();
    }

    /**
     * Creates a new user
     *
     * @param cmsUser to be created
     */
    public void createUser(CmsUser cmsUser) {
        allocateUser(cmsUser);

        cmsUserRepository.save(cmsUser);
    }

    public void allocateUser(CmsUser cmsUser) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        for (CmsRole cmsRole : cmsUser.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(cmsRole.getRole().getName()));
        }
        User user = new User(cmsUser.getUsername(), encoder.encode(cmsUser.getPassword()),
                authorities);

        createUser(user);
    }

    /**
     * Deletes a user
     *
     * @param cmsUser to be deleted
     */
    public void deleteUser(CmsUser cmsUser) {
        for (CmsUser user : cmsUserRepository.findByUsername(cmsUser.getUsername())) {
            cmsUserRepository.delete(user);
        }
        deleteUser(cmsUser.getUsername());
    }

    /**
     * Delete a user given its username
     *
     * @param username username
     */
    public void deleteUserByUsername(String username) {
        for (CmsUser user : cmsUserRepository.findByUsername(username)) {
            cmsUserRepository.delete(user);
        }
        deleteUser(username);
    }
}

