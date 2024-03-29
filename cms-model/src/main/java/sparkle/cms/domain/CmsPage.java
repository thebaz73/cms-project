package sparkle.cms.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * CmsNode
 * Created by bazzoni on 08/07/2015.
 */
@Document
public class CmsPage {
    @Id
    private String id;
    @Indexed
    private String siteId;
    private String name;
    private String title;
    private String uri;
    private boolean menu;
    @DBRef
    private CmsPage parent;
    private String templateId;
    @DBRef
    private List<CmsHotspot> hotspots;

    public CmsPage() {
    }

    @PersistenceConstructor
    public CmsPage(String id, String siteId, String name, String title, String uri, boolean menu, CmsPage parent) {
        this.id = id;
        this.siteId = siteId;
        this.name = name;
        this.title = title;
        this.uri = uri;
        this.menu = menu;
        this.parent = parent;
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

    public boolean isMenu() {
        return menu;
    }

    public void setMenu(boolean menu) {
        this.menu = menu;
    }

    public CmsPage getParent() {
        return parent;
    }

    public void setParent(CmsPage parent) {
        this.parent = parent;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public List<CmsHotspot> getHotspots() {
        if (hotspots == null) {
            hotspots = new ArrayList<>();
        }
        return hotspots;
    }

    public void setHotspots(List<CmsHotspot> hotspots) {
        this.hotspots = hotspots;
    }
}
