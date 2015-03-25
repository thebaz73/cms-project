package ms.cms.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * CmsPage
 * Created by thebaz on 24/03/15.
 */
@Document
public class CmsPage {
    @Id
    private String id;
    private String name;
    private String title;
    private String uri;
    private Date modificationDate;
    private String summary;
    private String content;
    @DBRef
    private List<CmsAsset> assets;

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
