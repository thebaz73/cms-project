package sparkle.cms.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

/**
 * CmsHotspot
 * Created by bazzoni on 08/07/2015.
 */
@Document
public class CmsHotspot {
    @Id
    private String id;
    @NotNull
    private String name;
    @NotNull
    private String contentId;
    private String alternativeLinkName;
    private String alternativeLaunchContent;

    public CmsHotspot() {
    }

    @PersistenceConstructor
    public CmsHotspot(String id, String name, String contentId) {
        this.id = id;
        this.name = name;
        this.contentId = contentId;
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

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getAlternativeLinkName() {
        return alternativeLinkName;
    }

    public void setAlternativeLinkName(String alternativeLinkName) {
        this.alternativeLinkName = alternativeLinkName;
    }

    public String getAlternativeLaunchContent() {
        return alternativeLaunchContent;
    }

    public void setAlternativeLaunchContent(String alternativeLaunchContent) {
        this.alternativeLaunchContent = alternativeLaunchContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CmsHotspot cmsHotspot = (CmsHotspot) o;

        return !(id != null ? !id.equals(cmsHotspot.id) : cmsHotspot.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
