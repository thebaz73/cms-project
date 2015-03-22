package ms.cms.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * CmsSite
 * Created by thebaz on 21/03/15.
 */
@Document
public class CmsSite {
    @Id
    private String id;
    private String name;
    @Indexed(unique = true)
    private String address;
    @DBRef
    @Indexed(unique = true)
    private CmsUser webMaster;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public CmsUser getWebMaster() {
        return webMaster;
    }

    public void setWebMaster(CmsUser webMaster) {
        this.webMaster = webMaster;
    }
}
