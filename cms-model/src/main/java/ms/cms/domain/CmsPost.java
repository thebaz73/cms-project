package ms.cms.domain;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * CmsPost
 * Created by thebaz on 24/03/15.
 */
@Document
public class CmsPost extends CmsPage {
    @DBRef
    private List<CmsComment> comments;

    public List<CmsComment> getComments() {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        return comments;
    }

    public void setComments(List<CmsComment> comments) {
        this.comments = comments;
    }
}
