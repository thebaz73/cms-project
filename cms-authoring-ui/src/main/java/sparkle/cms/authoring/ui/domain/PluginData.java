package sparkle.cms.authoring.ui.domain;

import sparkle.cms.domain.CmsSetting;

import java.util.ArrayList;
import java.util.List;

/**
 * PluginData
 * Created by bazzoni on 13/05/2015.
 */
public class PluginData {
    private String id;
    private String name;
    private String type;
    private String status;
    private CmsSetting activate;
    private List<CmsSetting> cmsSettings = new ArrayList<>();

    public PluginData() {
    }

    public PluginData(String id, String name, String type, String status) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CmsSetting getActivate() {
        return activate;
    }

    public void setActivate(CmsSetting activate) {
        this.activate = activate;
    }

    public List<CmsSetting> getCmsSettings() {
        return cmsSettings;
    }

    public void setCmsSettings(List<CmsSetting> cmsSettings) {
        this.cmsSettings = cmsSettings;
    }
}
