package ms.cms.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * CmsAsset
 * Created by thebaz on 24/03/15.
 */
@Document
public class CmsAsset {
    @Id
    private String id;
    private String name;
    private Date modificationDate;
    private String title;
    @Indexed(unique = true)
    private String uri;

    public CmsAsset() {
    }

    @PersistenceConstructor
    public CmsAsset(String name, Date modificationDate, String title, String uri) {
        this.name = name;
        this.modificationDate = modificationDate;
        this.title = title;
        this.uri = uri;
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

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
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
}
