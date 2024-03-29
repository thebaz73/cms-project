package sparkle.cms.authoring.common.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import sparkle.cms.data.CmsAssetRepository;
import sparkle.cms.data.CmsSiteRepository;
import sparkle.cms.domain.AssetType;
import sparkle.cms.domain.CmsAsset;
import sparkle.cms.domain.CmsSite;
import sparkle.cms.domain.CmsUser;
import sparkle.cms.plugin.mgmt.PluginOperationException;
import sparkle.cms.plugin.mgmt.PluginService;
import sparkle.cms.plugin.mgmt.asset.Asset;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static sparkle.cms.authoring.common.utils.AuthoringUtils.toPrettyFileURI;
import static sparkle.cms.plugin.mgmt.asset.AssetUtils.findAssetTypeByContentType;

/**
 * AssetManager
 * Created by bazzoni on 04/05/2015.
 */
@Component
public class AssetManager {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CmsSiteRepository cmsSiteRepository;
    @Autowired
    private CmsAssetRepository cmsAssetRepository;
    @Autowired
    private PluginService pluginService;

    public boolean checkPluginAvailable() {
        return pluginService.getAssetManagementPlugin() != null;
    }

    public Page<CmsAsset> findAllAssets(CmsUser cmsUser, Pageable pageable) {
        List<CmsSite> cmsSites = cmsSiteRepository.findByWebMaster(cmsUser);
        List<CmsAsset> cmsAssets = new ArrayList<>();
        for (CmsSite cmsSite : cmsSites) {
            cmsAssets.addAll(cmsAssetRepository.findBySiteId(cmsSite.getId()));
        }
        return new PageImpl<>(cmsAssets, pageable, cmsAssets.size());
    }

    public Page<CmsAsset> findAssetsByType(CmsUser cmsUser, AssetType type, Pageable pageable) {
        List<CmsSite> cmsSites = cmsSiteRepository.findByWebMaster(cmsUser);
        List<CmsAsset> cmsAssets = new ArrayList<>();
        for (CmsSite cmsSite : cmsSites) {
            cmsAssets.addAll(cmsAssetRepository.findBySiteIdAndType(cmsSite.getId(), type));
        }
        return new PageImpl<>(cmsAssets, pageable, cmsAssets.size());
    }

    public Page<CmsAsset> findAuthoredAssetsByType(CmsUser cmsUser, AssetType type, Pageable pageable) {
        CmsSite cmsSite = cmsSiteRepository.findOne(cmsUser.getAuthoredSiteId());
        return cmsAssetRepository.findBySiteIdAndType(cmsSite.getId(), type, pageable);
    }

    public Page<CmsAsset> findAuthoredAssets(CmsUser cmsUser, Pageable pageable) {
        CmsSite cmsSite = cmsSiteRepository.findOne(cmsUser.getAuthoredSiteId());
        return cmsAssetRepository.findBySiteId(cmsSite.getId(), pageable);
    }

    public void createSiteRepository(String siteId) throws AuthoringException {
        try {
            pluginService.getAssetManagementPlugin().createSiteRepository(siteId);
        } catch (PluginOperationException e) {
            logger.error("Cannot create site repository", e);
            throw new AuthoringException("Cannot create site repository", e);
        }
    }

    public void deleteSiteRepository(String siteId) throws AuthoringException {
        try {
            pluginService.getAssetManagementPlugin().deleteSiteRepository(siteId);
        } catch (PluginOperationException e) {
            logger.error("Cannot delete site repository", e);
            throw new AuthoringException("Cannot create site repository", e);
        }
    }

    public void createFolder(String siteId, String path) throws AuthoringException {
        try {
            pluginService.getAssetManagementPlugin().createFolder(siteId, toPrettyFileURI(path));
        } catch (PluginOperationException e) {
            logger.error("Cannot create folder", e);
            throw new AuthoringException("Cannot create folder ", e);
        }
    }

    public void deleteFolder(String siteId, String path) throws AuthoringException {
        try {
            pluginService.getAssetManagementPlugin().deleteFolder(siteId, toPrettyFileURI(path));
        } catch (PluginOperationException e) {
            logger.error("Cannot delete folder", e);
            throw new AuthoringException("Cannot create folder", e);
        }
    }

    public void createAsset(String siteId, String path, String name, byte[] data, String contentType) throws AuthoringException {
        try {
            pluginService.getAssetManagementPlugin().createAsset(siteId, toPrettyFileURI(path), toPrettyFileURI(name), data, contentType);
        } catch (PluginOperationException e) {
            logger.error("Cannot create folder", e);
            throw new AuthoringException("Cannot create asset ", e);
        }
    }

    public void deleteAsset(String siteId, String path, String name) throws AuthoringException {
        try {
            pluginService.getAssetManagementPlugin().deleteAsset(siteId, toPrettyFileURI(path), toPrettyFileURI(name));
        } catch (PluginOperationException e) {
            logger.error("Cannot delete folder", e);
            throw new AuthoringException("Cannot create folder", e);
        }
    }

    public void createAsset(CmsAsset cmsAsset, String originalFilename, byte[] bytes, String contentType) throws AuthoringException {
        if(cmsAsset.getName().isEmpty()) cmsAsset.setName(originalFilename);
        if(cmsAsset.getTitle().isEmpty()) cmsAsset.setTitle(originalFilename);
        cmsAsset.setType(findAssetTypeByContentType(contentType));
        cmsAsset.setModificationDate(new Date());
        cmsAsset.setUri(String.format("%s/%s", cmsAsset.getSiteId(), toPrettyFileURI(originalFilename)));

        createAsset(cmsAsset.getSiteId(), "", cmsAsset.getName(), bytes, contentType);
        cmsAssetRepository.save(cmsAsset);
    }

    public void deleteAsset(String id) throws AuthoringException {
        final CmsAsset cmsAsset = cmsAssetRepository.findOne(id);
        deleteAsset(cmsAsset.getSiteId(), "", cmsAsset.getName());
        cmsAssetRepository.delete(cmsAsset);
    }

    public Asset findAssetByUri(String uri) throws AuthoringException {
        try {
            final List<CmsAsset> bySiteIdAndUri = cmsAssetRepository.findBySiteIdAndUri(uri.substring(0, uri.indexOf("/")), uri);
            if(!bySiteIdAndUri.isEmpty()) {
                CmsAsset cmsAsset = bySiteIdAndUri.get(0);
                return pluginService.getAssetManagementPlugin().findAsset(cmsAsset.getSiteId(), "", uri.substring(uri.lastIndexOf("/") + 1));
            }
        } catch (PluginOperationException e) {
            logger.error("Cannot find asset", e);
            throw new AuthoringException("Cannot find asset ", e);
        }
        return null;
    }
}
