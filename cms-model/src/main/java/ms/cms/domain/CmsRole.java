package ms.cms.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * CmsRole
 * Created by thebaz on 21/03/15.
 */
@Document
public class CmsRole {
    @Id
    private String id;

    private Role role;

    public CmsRole() {
    }

    public CmsRole(String name) {
        this.role = Role.forName(name);
    }

    public CmsRole(Role role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
