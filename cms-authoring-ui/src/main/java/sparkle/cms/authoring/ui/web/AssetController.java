package sparkle.cms.authoring.ui.web;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sparkle.cms.authoring.common.business.AssetManager;
import sparkle.cms.authoring.common.business.AuthoringException;
import sparkle.cms.authoring.common.business.ContentManager;
import sparkle.cms.domain.*;
import sparkle.cms.plugin.mgmt.asset.Asset;
import sparkle.cms.registration.common.business.RegistrationException;
import sparkle.cms.registration.common.business.RegistrationManager;
import sparkle.cms.registration.common.business.SiteManager;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static sparkle.cms.utils.UserUtils.isAuthor;
import static sparkle.cms.utils.UserUtils.isWebmaster;

/**
 * UserController Created by thebaz on 12/04/15.
 */
@Controller(value = "assetController")
public class AssetController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RegistrationManager registrationManager;
    @Autowired
    private SiteManager siteManager;
    @Autowired
    private ContentManager contentManager;
    @Autowired
    private AssetManager assetManager;

    @ModelAttribute("allSites")
    public List<CmsSite> allSites(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            CmsUser cmsUser = registrationManager.findUser(request.getRemoteUser());
            if (isWebmaster(cmsUser)) {
                return siteManager.findAllSites(cmsUser);
            } else if (isAuthor(cmsUser)) {
                return siteManager.findAuthoredSites(cmsUser);
            }
        } catch (RegistrationException e) {
            String msg = String.format("Cannot manage sites. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return null;
    }

    @ModelAttribute("allAssetTypes")
    public List<AssetType> assetTypes() {
        return Arrays.asList(AssetType.ALL);
    }

    @ModelAttribute("allContents")
    public List<CmsContent> allContents(HttpServletRequest request,
                                        HttpServletResponse response) throws IOException {
        try {
            CmsUser cmsUser = registrationManager.findUser(request
                    .getRemoteUser());
            if (isWebmaster(cmsUser)) {
                return contentManager.findAllContents(cmsUser);
            } else if (isAuthor(cmsUser)) {
                return contentManager.findAuthoredContents(cmsUser);
            }
        } catch (RegistrationException e) {
            String msg = String.format("Cannot manage contents. Reason: %s",
                    e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return null;
    }

    @ModelAttribute("allAssets")
    public Page<CmsAsset> allAssets(HttpServletRequest request,
                                    HttpServletResponse response,
                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                    @RequestParam(value = "pageSize", defaultValue = "12") int pageSize)
            throws IOException {
        try {
            CmsUser cmsUser = registrationManager.findUser(request
                    .getRemoteUser());
            Pageable pageable = new PageRequest(page, pageSize,
                    Sort.Direction.ASC, "name");
            if (isWebmaster(cmsUser)) {
                return assetManager.findAllAssets(cmsUser, pageable);
            } else if (isAuthor(cmsUser)) {
                return assetManager.findAuthoredAssets(cmsUser, pageable);
            }
        } catch (RegistrationException e) {
            String msg = String.format("Cannot manage assets. Reason: %s",
                    e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }

        return null;
    }

    @RequestMapping(value = {"/assets"}, method = RequestMethod.GET)
    public String show(ModelMap model) {
        model.put("assetData", new CmsAsset());
        model.put("mode", "add");
        return "assets";
    }

    @RequestMapping(value = {"/assets/{id}"}, method = RequestMethod.DELETE)
    public String delete(HttpServletResponse response, @PathVariable("id") String id) throws IOException {
        try {
            assetManager.deleteAsset(id);
        } catch (AuthoringException e) {
            String msg = String.format("Cannot manage assets. Reason: %s",
                    e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return "redirect:/assets";
    }

    @RequestMapping(value = {"/assets/download/**"}, method = {RequestMethod.GET, RequestMethod.HEAD})
    public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            final String prefix = "/assets/download/";
            final String requestURI = request.getRequestURI();
            String uri = requestURI.substring(prefix.length());
            Asset asset = assetManager.findAssetByUri(uri);
            if (asset.getContent() == null) {
                response.setHeader("Location", asset.getUri());
            }
            else {
                response.setContentType("application/force-download");
                response.setContentLength(-1);
                response.setHeader("Content-Transfer-Encoding", "binary");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + uri.substring(uri.lastIndexOf("/") + 1) + "\"");

                ByteArrayInputStream is = new ByteArrayInputStream(asset.getContent());
                IOUtils.copy(is, response.getOutputStream());
                response.flushBuffer();
            }
        } catch (AuthoringException e) {
            String msg = String.format("Cannot manage assets. Reason: %s",
                    e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
    }

    @RequestMapping(value = {"/assets/preview/**"}, method = {RequestMethod.GET, RequestMethod.HEAD})
    public void preview(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            final String prefix = "/assets/preview/";
            final String requestURI = request.getRequestURI();
            String uri = requestURI.substring(prefix.length());
            Asset asset = assetManager.findAssetByUri(uri);
            if (asset.getContent() == null) {
                response.setHeader("Location", asset.getUri());
            }
            else {
                MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
                // only by file name
                String mimeType = mimeTypesMap.getContentType(uri.substring(uri.lastIndexOf("/") + 1));
                response.setContentType(mimeType);
                response.setHeader("X-Frame-Options", "SAMEORIGIN");

                ByteArrayInputStream is = new ByteArrayInputStream(asset.getContent());
                IOUtils.copy(is, response.getOutputStream());
                response.flushBuffer();
            }
        } catch (AuthoringException e) {
            String msg = String.format("Cannot manage assets. Reason: %s",
                    e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
    }

    /**
     * POST /uploadFile -> receive and locally save a file.
     *
     * @param uploadFile The uploaded file as Multipart file parameter in the
     *                   HTTP request. The RequestParam name must be the same of the attribute
     *                   "name" in the input tag with type file.
     * @return An http OK status in case of success, an http 4xx status in case
     * of errors.
     */
    @RequestMapping(value = {"/assets"}, method = RequestMethod.POST)
    public String uploadFile(HttpServletResponse response, @ModelAttribute("assetData") CmsAsset assetData,
                             final BindingResult bindingResult, final ModelMap model, @RequestParam("uploadFile") MultipartFile uploadFile) throws IOException {
        if (bindingResult.hasErrors()) {
            return "assets";
        }
        try {
            assetManager.createAsset(assetData, uploadFile.getOriginalFilename(), uploadFile.getBytes(), uploadFile.getContentType());
            model.clear();
        } catch (AuthoringException e) {
            String msg = String.format("Cannot manage assets. Reason: %s",
                    e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }

        return "redirect:/assets";
    }
}
