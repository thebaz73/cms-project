package sparkle.cms.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CmsUser
 * Created by thebaz on 21/03/15.
 */
@Document
public class CmsUser {
    @Id
    private String id;
    private String name;
    @Indexed(unique = true)
    private String email;
    @Indexed(unique = true)
    private String username;
    private String password;
    @Indexed
    private Date creationDate;
    @DBRef
    private List<CmsRole> roles;
    @Indexed
    private String authoredSiteId;

    public CmsUser() {
        this.roles = new ArrayList<>();
    }

    public CmsUser(String username, String password, Collection<CmsRole> roles) {
        this.username = username;
        this.password = password;
        this.roles = new ArrayList<>();
        this.roles.addAll(roles.stream().collect(Collectors.toList()));
    }

    @PersistenceConstructor
    public CmsUser(String name, String email, String username, String password, Date creationDate, Collection<CmsRole> roles) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.creationDate = creationDate;
        this.roles = new ArrayList<>();
        this.roles.addAll(roles.stream().collect(Collectors.toList()));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public List<CmsRole> getRoles() {
        return roles;
    }

    public void setRoles(List<CmsRole> roles) {
        this.roles = roles;
    }

    public String getAuthoredSiteId() {
        return authoredSiteId;
    }

    public void setAuthoredSiteId(String authoredSiteId) {
        this.authoredSiteId = authoredSiteId;
    }
}
