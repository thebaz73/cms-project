package sparkle.cms.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;
import sparkle.cms.data.CmsUserRepository;
import sparkle.cms.domain.CmsUser;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ReadOnlyUserManager
 * Created by thebaz on 21/03/15.
 */
@Component(value = "userService")
public class ReadOnlyUserManager implements UserDetailsManager {
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    private CmsUserRepository cmsUserRepository;

//    @Autowired
//    public ReadOnlyUserManager(DataSource dataSource) {
//        super.setDataSource(dataSource);
//    }

    public PasswordEncoder getEncoder() {
        return encoder;
    }

    /**
     * Create a new user with the supplied details.
     *
     * @param user user name
     */
    @Override
    public void createUser(UserDetails user) {
        throw new UnsupportedOperationException();
    }

    /**
     * Update the specified user.
     *
     * @param user user name
     */
    @Override
    public void updateUser(UserDetails user) {
        throw new UnsupportedOperationException();
    }

    /**
     * Remove the user with the given login name from the system.
     *
     * @param username user name
     */
    @Override
    public void deleteUser(String username) {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    /**
     * Check if a user with the supplied login name exists in the system.
     *
     * @param username user name
     */
    @Override
    public boolean userExists(String username) {
        return !cmsUserRepository.findByUsername(username).isEmpty();
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
        UserDetails user = null;
        List<CmsUser> byUsername = cmsUserRepository.findByUsername(username);
        for (CmsUser cmsUser : byUsername) {
            user = allocateUser(cmsUser);
        }
        if (user == null) {
            throw new UsernameNotFoundException("No user found.");
        }
        return user;
    }

    /**
     * Load a user given username
     *
     * @param username user name
     * @return user or null if not found
     */
    public CmsUser findUser(String username) {
        CmsUser cmsUser = null;
        List<CmsUser> byUsername = cmsUserRepository.findByUsername(username);
        for (CmsUser user : byUsername) {
            cmsUser = user;
        }

        return cmsUser;
    }

    private User allocateUser(CmsUser cmsUser) {
        List<SimpleGrantedAuthority> authorities = cmsUser.getRoles().stream().map(cmsRole -> new SimpleGrantedAuthority(cmsRole.getRole().getName())).collect(Collectors.toList());
        if (authorities.isEmpty()) {
            throw new UsernameNotFoundException("No authorities found.");
        }

        return new User(cmsUser.getUsername(), /*encoder.encode*/(cmsUser.getPassword()),
                authorities);
    }
}

