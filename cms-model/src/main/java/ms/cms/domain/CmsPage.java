package ms.cms.domain;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * CmsContent
 * Created by thebaz on 24/03/15.
 */
@Document
@Deprecated
public class CmsPage extends CmsAbstractContent {


    public CmsPage() {
        super("PAGE");
    }

    @PersistenceConstructor
    public CmsPage(String siteId, String name, String title, String uri, Date modificationDate, String summary, String content) {
        super("PAGE", siteId, name, title, uri, modificationDate, summary, content);
    }
}
