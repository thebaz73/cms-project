package ms.cms.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

/**
 * CmsComment
 * Created by thebaz on 24/03/15.
 */
public class CmsComment {
    @Id
    private String id;
    private String title;
    private String content;
    @DBRef
    @Indexed
    private CmsUser viewer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
