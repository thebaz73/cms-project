package ms.cms.domain;

import org.springframework.data.annotation.PersistenceConstructor;
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
public class CmsContent extends CmsAbstractContent {

    @DBRef
    private List<CmsTag> tags;

    public CmsContent() {
        super("CONTENT");
    }

    @PersistenceConstructor
    public CmsContent(String siteId, String name, String title, String uri, Date modificationDate, String summary, String content) {
        super("POST", siteId, name, title, uri, modificationDate, summary, content);
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
}
