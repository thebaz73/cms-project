package sparkle.cms.authoring.ui.web;

import org.apache.catalina.core.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sparkle.cms.authoring.common.business.SettingManager;
import sparkle.cms.domain.CmsSetting;
import sparkle.cms.domain.CmsUser;
import sparkle.cms.domain.SettingType;
import sparkle.cms.registration.common.business.RegistrationException;
import sparkle.cms.registration.common.business.RegistrationManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * SettingsController
 * Created by thebaz on 12/04/15.
 */
@Controller(value = "settingsController")
public class SettingsController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RegistrationManager registrationManager;
    @Autowired
    private SettingManager settingManager;

    @ModelAttribute("allSettings")
    public Page<CmsSetting> allSites(HttpServletRequest request, HttpServletResponse response,
                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) throws IOException {
        try {
            CmsUser cmsUser = registrationManager.findUser(request.getRemoteUser());
            Pageable pageable = new PageRequest(page, pageSize, Sort.Direction.ASC, "key");
            return settingManager.findSettings(cmsUser, pageable);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot manage settings. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return null;
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    @RequestMapping(value = {"/settings/reload"}, method = RequestMethod.GET)
    public String reload() {
        settingManager.reloadSettings();
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
    public String editSetting(@ModelAttribute("cmsSetting") CmsSetting cmsSetting,
                              final BindingResult bindingResult, final ModelMap model) throws IOException {
        if (bindingResult.hasErrors()) {
            return "settings";
        }
        CmsSetting editableSetting = settingManager.findSetting(cmsSetting.getId());
        editableSetting.setValue(getTypedValue((String) cmsSetting.getValue(), cmsSetting.getType()));
        settingManager.editSetting(editableSetting);
        model.clear();
        return "redirect:/settings";
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
