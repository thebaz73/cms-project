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
 * CmsContent
 * Created by thebaz on 24/03/15.
 */
@Document
public abstract class CmsContent {
    private final String type;
    @Id
    private String id;
    private String name;
    private String title;
    @Indexed
    private String uri;
    @Indexed
    private Date modificationDate;
    private String summary;
    private String content;
    @DBRef
    private List<CmsAsset> assets;

    protected CmsContent() {
        this.type = "UNDEFINED";
    }

    public CmsContent(String type) {
        this.type = type;
    }

    @PersistenceConstructor
    public CmsContent(String type, String name, String title, String uri, Date modificationDate, String summary, String content) {
        this(type);
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

    public String getType() {
        return type;
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
}
