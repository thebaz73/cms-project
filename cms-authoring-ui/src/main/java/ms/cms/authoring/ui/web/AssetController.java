package ms.cms.authoring.ui.web;

import ms.cms.authoring.common.business.AssetManager;
import ms.cms.authoring.common.business.ContentManager;
import ms.cms.domain.AssetType;
import ms.cms.domain.CmsAsset;
import ms.cms.domain.CmsContent;
import ms.cms.domain.CmsUser;
import ms.cms.registration.common.business.RegistrationException;
import ms.cms.registration.common.business.RegistrationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ms.cms.utils.UserUtils.isAuthor;
import static ms.cms.utils.UserUtils.isWebmaster;

/**
 * UserController
 * Created by thebaz on 12/04/15.
 */
@Controller(value = "assetController")
public class AssetController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RegistrationManager registrationManager;
    @Autowired
    private ContentManager contentManager;
    @Autowired
    private AssetManager assetManager;

    @ModelAttribute("allContents")
    public List<CmsContent> allContents(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            CmsUser cmsUser = registrationManager.findUser(request.getRemoteUser());
            if (isWebmaster(cmsUser)) {
                return contentManager.findAllContents(cmsUser);
            } else if (isAuthor(cmsUser)) {
                return contentManager.findAuthoredContents(cmsUser);
            }
        } catch (RegistrationException e) {
            String msg = String.format("Cannot manage contents. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return null;
    }

    @ModelAttribute("allAssets")
    public Page<CmsAsset> allAssets(HttpServletRequest request, HttpServletResponse response,
                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                    @RequestParam(value = "pageSize", defaultValue = "12") int pageSize) throws IOException {
//        try {
//            CmsUser cmsUser = registrationManager.findUser(request.getRemoteUser());
            Pageable pageable = new PageRequest(page, pageSize, Sort.Direction.ASC, "name");
//            if (isWebmaster(cmsUser)) {
//                return assetManager.findAllAssets(cmsUser, pageable);
//            } else if (isAuthor(cmsUser)) {
//                return assetManager.findAuthoredAssets(cmsUser, pageable);
//            }
//        } catch (RegistrationException e) {
//            String msg = String.format("Cannot manage assets. Reason: %s", e.getMessage());
//            logger.info(msg, e);
//            response.sendError(400, msg);
//        }
        List<CmsAsset> cmsAssets = new ArrayList<>();
        for (int i = 0; i < AssetType.ALL.length; i++) {
            AssetType assetType = AssetType.ALL[i];
            CmsAsset asset = new CmsAsset("id" + i, "asset" + i, new Date(), "title" + i, "uri" + i);
            asset.setType(assetType);
            cmsAssets.add(asset);
        }
        return new PageImpl<>(cmsAssets, pageable, cmsAssets.size());
    }

    @RequestMapping({"/assets"})
    public String show(ModelMap model) {
        model.put("assetData", new CmsAsset());
        model.put("mode", "add");
        return "assets";
    }
}
