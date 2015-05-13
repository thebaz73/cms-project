package sparkle.cms.authoring.ui.domain;

import sparkle.cms.domain.CmsSetting;

import java.util.List;

/**
 * PluginData
 * Created by bazzoni on 13/05/2015.
 */
public class PluginData {
    private String id;
    private String name;
    private String status;
    private List<CmsSetting> cmsSettings;

    public PluginData() {
    }

    public PluginData(String id, String name, String status, List<CmsSetting> cmsSettings) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.cmsSettings = cmsSettings;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CmsSetting> getCmsSettings() {
        return cmsSettings;
    }

    public void setCmsSettings(List<CmsSetting> cmsSettings) {
        this.cmsSettings = cmsSettings;
    }
}
