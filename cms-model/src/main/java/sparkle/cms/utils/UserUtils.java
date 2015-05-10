package sparkle.cms.utils;

import sparkle.cms.domain.CmsUser;
import sparkle.cms.domain.Role;

/**
 * UserUtils
 * Created by thebaz on 11/04/15.
 */
public class UserUtils {

    public static boolean isAdmin(CmsUser cmsUser) {
        return cmsUser.getRoles().stream().anyMatch(r -> r.getRole().equals(Role.ROLE_ADMIN));
    }

    public static boolean isWebmaster(CmsUser cmsUser) {
        return cmsUser.getRoles().stream().anyMatch(r -> r.getRole().equals(Role.ROLE_MANAGER));
    }

    public static boolean isAuthor(CmsUser cmsUser) {
        return cmsUser.getRoles().stream().anyMatch(r -> r.getRole().equals(Role.ROLE_AUTHOR));
    }
}
