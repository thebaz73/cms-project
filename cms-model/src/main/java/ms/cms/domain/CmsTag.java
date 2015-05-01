package ms.cms.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * CmsTag
 * Created by thebaz on 28/03/15.
 */
@Document
@CompoundIndexes({
        @CompoundIndex(name = "site_tag_idx", def = "{'siteId' : 1, 'tag' : 1}")
})
public class CmsTag {
    @Id
    private String id;
    private String siteId;
    private String tag;
    private Integer popularity;
    private Set<String> contentIds;

    public CmsTag() {
        this.popularity = 0;
    }

    @PersistenceConstructor
    public CmsTag(String siteId, String tag) {
        this();
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

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }

    public Set<String> getContentIds() {
        if (contentIds == null) {
            contentIds = new HashSet<>();
        }
        return contentIds;
    }

    public void setContentIds(Set<String> contentIds) {
        this.contentIds = contentIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CmsTag cmsTag = (CmsTag) o;

        if (id != null ? !id.equals(cmsTag.id) : cmsTag.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
