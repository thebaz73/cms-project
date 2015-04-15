package ms.cms.authoring.ui.web;

import ms.cms.authoring.common.business.AuthoringManager;
import ms.cms.registration.common.business.RegistrationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * UserController
 * Created by thebaz on 12/04/15.
 */
@Controller(value = "settingsController")
public class SettingsController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RegistrationManager registrationManager;
    @Autowired
    private AuthoringManager authoringManager;

    @RequestMapping({"/settings"})
    public String appSettings(Map<String, Object> model) {
        return "settings";
    }
}
