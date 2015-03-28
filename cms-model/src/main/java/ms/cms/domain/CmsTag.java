package ms.cms.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * CmsTag
 * Created by thebaz on 28/03/15.
 */
@Document
@CompoundIndexes({
        @CompoundIndex(name = "site_uri_idx", def = "{'siteId' : 1, 'uri' : 1}")
})
public class CmsTag {
    @Id
    private String id;
    private String siteId;
    private String tag;
    private List<String> commentIds;

    public CmsTag() {
    }

    @PersistenceConstructor
    public CmsTag(String siteId, String tag) {
        this.siteId = siteId;
        this.tag = tag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<String> getCommentIds() {
        if (commentIds == null) {
            commentIds = new ArrayList<>();
        }
        return commentIds;
    }

    public void setCommentIds(List<String> commentIds) {
        this.commentIds = commentIds;
    }
}
