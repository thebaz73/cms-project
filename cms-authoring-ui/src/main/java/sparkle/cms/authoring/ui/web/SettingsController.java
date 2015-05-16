package sparkle.cms.authoring.ui.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sparkle.cms.authoring.common.business.SettingManager;
import sparkle.cms.authoring.ui.domain.PluginData;
import sparkle.cms.domain.CmsSetting;
import sparkle.cms.domain.CmsUser;
import sparkle.cms.domain.SettingType;
import sparkle.cms.plugin.mgmt.Plugin;
import sparkle.cms.registration.common.business.RegistrationException;
import sparkle.cms.registration.common.business.RegistrationManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SettingsController
 * Created by thebaz on 12/04/15.
 */
@Controller(value = "settingsController")
public class SettingsController {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final List<CmsSetting> settings = new ArrayList<>();
    private final List<PluginData> plugins = new ArrayList<>();
    @Autowired
    private RegistrationManager registrationManager;
    @Autowired
    private SettingManager settingManager;

    @ModelAttribute("plugins")
    public List<PluginData> allPlugins(HttpServletRequest request, HttpServletResponse response) throws IOException {
        loadSettings(request, response);
        return plugins;
    }

    @ModelAttribute("allSettings")
    public List<CmsSetting> allSites(HttpServletRequest request, HttpServletResponse response) throws IOException {
        loadSettings(request, response);
        return settings;
    }

    private void loadSettings(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (plugins.isEmpty() && settings.isEmpty()) {
            try {
                CmsUser cmsUser = registrationManager.findUser(request.getRemoteUser());
                Map<String, CmsSetting> cmsSettingMap = new HashMap<>();
                for (CmsSetting cmsSetting : settingManager.findSettings(cmsUser)) {
                    cmsSettingMap.put(cmsSetting.getKey(), cmsSetting);
                }

                final Map<String, Plugin> pluginMap = settingManager.findPlugins();
                for (Map.Entry<String, Plugin> entry : pluginMap.entrySet()) {
                    logger.debug("Processing bean {}", entry.getKey());
                    Plugin plugin = entry.getValue();
                    PluginData pluginData = new PluginData(plugin.getId(), plugin.getName(), plugin.getStatus().toString());
                    for (CmsSetting cmsSetting : plugin.getSettings()) {
                        pluginData.getCmsSettings().add(cmsSettingMap.remove(cmsSetting.getKey()));
                    }
                    plugins.add(pluginData);
                }
                settings.addAll(cmsSettingMap.values());
            } catch (RegistrationException e) {
                String msg = String.format("Cannot manage settings. Reason: %s", e.getMessage());
                logger.info(msg, e);
                response.sendError(400, msg);
            }
        }
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    @RequestMapping(value = {"/settings/reload"}, method = RequestMethod.GET)
    public String reload(HttpServletRequest request, HttpServletResponse response) throws IOException {
        settingManager.reloadSettings();
        settings.clear();
        plugins.clear();
        loadSettings(request, response);
        return "redirect:/settings";
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    @RequestMapping(value = {"/settings"}, method = RequestMethod.GET)
    public String show(ModelMap model) {
        model.put("cmsSetting", new CmsSetting());
        return "settings";
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    @RequestMapping(value = {"/settings/{settingId}"}, method = RequestMethod.GET)
    public String editMode(ModelMap model, @PathVariable("settingId") String settingId) throws IOException {
        CmsSetting cmsSetting = settingManager.findSetting(settingId);
        model.put("cmsSetting", cmsSetting);
        return "settings";
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    @RequestMapping(value = {"/settings"}, method = RequestMethod.PUT)
    public String editSetting(HttpServletRequest request, HttpServletResponse response, @ModelAttribute("cmsSetting") CmsSetting cmsSetting,
                              final BindingResult bindingResult, final ModelMap model) throws IOException {
        if (bindingResult.hasErrors()) {
            return "settings";
        }
        CmsSetting editableSetting = settingManager.findSetting(cmsSetting.getId());
        editableSetting.setValue(getTypedValue((String) cmsSetting.getValue(), cmsSetting.getType()));
        settingManager.editSetting(editableSetting);
        model.clear();
        return reload(request, response);
    }

    private Object getTypedValue(String value, SettingType type) {
        switch (type) {
            case BOOL:
                return Boolean.parseBoolean(value);
            case INTEGER:
                return Integer.parseInt(value);
            case DOUBLE:
                return Double.parseDouble(value);
            default:
                return value;
        }
    }
}
