package ms.cms.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * CmsSite
 * Created by thebaz on 21/03/15.
 */
@Document
public class CmsSite {
    @Id
    private String id;
    private String name;
    @Indexed
    private Date creationDate;
    @Indexed(unique = true)
    private String address;
    private WorkflowType workflowType;
    @DBRef
    @Indexed
    private CmsUser webMaster;
    @DBRef
    private List<CmsUser> authors;
    @DBRef
    private List<CmsPage> pages;
    @DBRef
    private List<CmsPost> posts;

    public CmsSite() {
    }

    @PersistenceConstructor
    public CmsSite(String name, Date creationDate, String address, WorkflowType workflowType, CmsUser webMaster) {
        this.name = name;
        this.creationDate = creationDate;
        this.address = address;
        this.workflowType = workflowType;
        this.webMaster = webMaster;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public WorkflowType getWorkflowType() {
        return workflowType;
    }

    public void setWorkflowType(WorkflowType workflowType) {
        this.workflowType = workflowType;
    }

    public CmsUser getWebMaster() {
        return webMaster;
    }

    public void setWebMaster(CmsUser webMaster) {
        this.webMaster = webMaster;
    }

    public List<CmsUser> getAuthors() {
        if (authors == null) {
            authors = new ArrayList<>();
        }
        return authors;
    }

    public void setAuthors(List<CmsUser> authors) {
        this.authors = authors;
    }

    public List<CmsPage> getPages() {
        if (pages == null) {
            pages = new ArrayList<>();
        }
        return pages;
    }

    public void setPages(List<CmsPage> pages) {
        this.pages = pages;
    }

    public List<CmsPost> getPosts() {
        if (posts == null) {
            posts = new ArrayList<>();
        }
        return posts;
    }

    public void setPosts(List<CmsPost> posts) {
        this.posts = posts;
    }
}
