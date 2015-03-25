package ms.cms.domain;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * CmsPost
 * Created by thebaz on 24/03/15.
 */
@Document
public class CmsPage extends CmsContent {


    public CmsPage() {
        super("PAGE");
    }

    @PersistenceConstructor
    public CmsPage(String name, String title, String uri, Date modificationDate, String summary, String content) {
        super("PAGE", name, title, uri, modificationDate, summary, content);
    }
}
