package sparkle.cms.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * CmsContent
 * Created by thebaz on 24/03/15.
 */
@Document
@CompoundIndexes({
        @CompoundIndex(name = "site_uri_idx", def = "{'siteId' : 1, 'uri' : 1}")
})

public class CmsContent {

    private final String type;
    @Id
    private String id;
    @NotNull
    private String siteId;
    @NotNull
    private String name;
    @NotNull
    private String title;
    @NotNull
    private String uri;
    @NotNull
    @Indexed
    private Date modificationDate;
    private boolean published;
    private String summary;
    private String content;
    @DBRef
    private List<CmsAsset> assets;
    @DBRef
    private List<CmsTag> tags;

    public CmsContent() {
        this.type = "CONTENT";
    }

    public CmsContent(String type) {
        this.type = type;
    }

    @PersistenceConstructor
    public CmsContent(String siteId, String name, String title, String uri, Date modificationDate, String summary, String content) {
        this("CONTENT");
        this.siteId = siteId;
        this.name = name;
        this.title = title;
        this.uri = uri;
        this.modificationDate = modificationDate;
        this.summary = summary;
        this.content = content;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<CmsAsset> getAssets() {
        if (assets == null) {
            assets = new ArrayList<>();
        }
        return assets;
    }

    public void setAssets(List<CmsAsset> assets) {
        this.assets = assets;
    }

    public List<CmsTag> getTags() {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        return tags;
    }

    public void setTags(List<CmsTag> tags) {
        this.tags = tags;
    }

    public String getType() {
        return type;
    }
}
