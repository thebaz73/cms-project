package sparkle.cms.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;

/**
 * CmsComment
 * Created by thebaz on 24/03/15.
 */
public class CmsComment {
    @Id
    private String id;
    private boolean approved;
    @Indexed
    private String contentId;
    @Indexed
    private Date timestamp;
    private String title;
    private String content;
    @DBRef
    private CmsUser viewer;

    public CmsComment() {
    }

    @PersistenceConstructor
    public CmsComment(String contentId, Date timestamp, String title, String content, CmsUser viewer) {
        this.contentId = contentId;
        this.timestamp = timestamp;
        this.title = title;
        this.content = content;
        this.viewer = viewer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CmsUser getViewer() {
        return viewer;
    }

    public void setViewer(CmsUser viewer) {
        this.viewer = viewer;
    }
}
