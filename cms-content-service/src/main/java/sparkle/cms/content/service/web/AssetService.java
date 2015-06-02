package sparkle.cms.content.service.web;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import sparkle.cms.data.CmsAssetRepository;
import sparkle.cms.data.CmsContentRepository;
import sparkle.cms.data.CmsTagRepository;
import sparkle.cms.domain.CmsAsset;
import sparkle.cms.domain.CmsContent;
import sparkle.cms.domain.CmsTag;
import sparkle.cms.plugin.mgmt.PluginOperationException;
import sparkle.cms.plugin.mgmt.PluginService;
import sparkle.cms.plugin.mgmt.asset.Asset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static sparkle.cms.plugin.mgmt.asset.AssetUtils.findContentTypeByFileName;

/**
 * AssetService
 * Created by bazzoni on 02/04/2015.
 */
@RestController(value = "assetWebService")
@RequestMapping(value = "/api")
public class AssetService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CmsAssetRepository cmsAssetRepository;
    @Autowired
    private PluginService pluginService;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Secured({"ROLE_MANAGER"})
    @RequestMapping(value = "/assets/**", method = RequestMethod.GET)
    public void assets(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            final String prefix = "/assets/preview/";
            final String requestURI = request.getRequestURI();
            String uri = requestURI.substring(prefix.length());
            Asset asset = findAssetByUri(uri);
            if (asset != null) {
                if (asset.getContent() == null) {
                    response.setHeader("Location", asset.getUri());
                    response.sendRedirect(asset.getUri());
                }
                else {
                    String mimeType = findContentTypeByFileName(uri.substring(uri.lastIndexOf("/") + 1));
                    response.setContentType(mimeType);
                    response.setHeader("X-Frame-Options", "SAMEORIGIN");

                    ByteArrayInputStream is = new ByteArrayInputStream(asset.getContent());
                    IOUtils.copy(is, response.getOutputStream());
                    response.flushBuffer();
                }
            }
        } catch (Exception e) {
            String msg = String.format("Cannot manage assets. Reason: %s",
                    e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
    }

    private Asset findAssetByUri(String uri) throws PluginOperationException {
        final List<CmsAsset> bySiteIdAndUri = cmsAssetRepository.findBySiteIdAndUri(uri.substring(0, uri.indexOf("/")), uri);
        if(!bySiteIdAndUri.isEmpty()) {
            CmsAsset cmsAsset = bySiteIdAndUri.get(0);
            return pluginService.getAssetManagementPlugin().findAsset(cmsAsset.getSiteId(), "", uri.substring(uri.lastIndexOf("/") + 1));
        }
        return null;
    }
}
