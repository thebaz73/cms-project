package ms.cms.registration.ui.web;

import ms.cms.registration.ui.business.RegistrationProxy;
import ms.cms.registration.ui.domain.UserRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * RegistrationController
 * Created by thebaz on 07/04/15.
 */
@Controller
public class RegistrationController {
    @Autowired
    private RegistrationProxy registrationProxy;
    @Autowired
    @Qualifier("registrationValidator")
    private Validator validator;

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @RequestMapping(value = {"", "/", "index"}, method = GET)
    public String show(Model model) {
        model.addAttribute("userRegistration", new UserRegistration());
        return "index";
    }

    @RequestMapping(value = {"", "/", "index"}, method = POST)
    public String createUser(@ModelAttribute @Validated UserRegistration userRegistration,
                             final BindingResult bindingResult,
                             final ModelMap model,
                             HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Input error found");
            model.addAttribute("userRegistration", userRegistration);
            return "index";
        }
        if (registrationProxy.userExists(request.getSession(), userRegistration)) {
            model.addAttribute("userRegistration", userRegistration);
            bindingResult.rejectValue("email", "email.present");
            model.addAttribute("message", "User already created");
            return "index";
        }

        if (!registrationProxy.createWebmaster(request.getSession(), userRegistration)) {
            bindingResult.reject("cannot.create");
            model.addAttribute("userRegistration", userRegistration);
            model.addAttribute("message", "Cannot create user. Retry later.");
            return "index";
        }
        model.clear();
        return "redirect:/confirm#register";
    }

    @RequestMapping(value = {"confirm"}, method = GET)
    public String doConfirm(Model model) {
        model.addAttribute("confirm", "An email has been sent on address you provided. Click on confirmation link.");
        model.addAttribute("userRegistration", new UserRegistration());
        return "index";
    }
}
