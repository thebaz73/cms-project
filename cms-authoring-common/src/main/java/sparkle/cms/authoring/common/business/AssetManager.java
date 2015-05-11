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
import sparkle.cms.domain.CmsAsset;
import sparkle.cms.domain.CmsSite;
import sparkle.cms.domain.CmsUser;
import sparkle.cms.plugin.mgmt.PluginOperationException;
import sparkle.cms.plugin.mgmt.PluginService;
import sparkle.cms.plugin.mgmt.asset.AssetManagementPlugin;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

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

    private AssetManagementPlugin assetManagementPlugin;
    @PostConstruct
    public void initialize() {
        assetManagementPlugin = pluginService.getAssetManagementPlugin();
    }

    public Page<CmsAsset> findAllAssets(CmsUser cmsUser, Pageable pageable) {
        List<CmsSite> cmsSites = cmsSiteRepository.findByWebMaster(cmsUser);
        List<CmsAsset> cmsAssets = new ArrayList<>();
        for (CmsSite cmsSite : cmsSites) {
            cmsAssets.addAll(cmsAssetRepository.findBySiteId(cmsSite.getId()));
        }
        return new PageImpl<>(cmsAssets, pageable, cmsAssets.size());
    }

    public Page<CmsAsset> findAuthoredAssets(CmsUser cmsUser, Pageable pageable) {
        CmsSite cmsSite = cmsSiteRepository.findOne(cmsUser.getAuthoredSiteId());
        return cmsAssetRepository.findBySiteId(cmsSite.getId(), pageable);
    }

    public void createSiteRepository(String siteId) throws AuthoringException {
        try {
            assetManagementPlugin.createSiteRepository(siteId);
        } catch (PluginOperationException e) {
            logger.error("Cannot create site repository", e);
            throw new AuthoringException("Cannot create site repository", e);
        }
    }

    public void deleteSiteRepository(String siteId) throws AuthoringException {
        try {
            assetManagementPlugin.deleteSiteRepository(siteId);
        } catch (PluginOperationException e) {
            logger.error("Cannot delete site repository", e);
            throw new AuthoringException("Cannot create site repository", e);
        }
    }

    public void createFolder(String siteId, String path) throws AuthoringException {
        try {
            assetManagementPlugin.createFolder(siteId, path);
        } catch (PluginOperationException e) {
            logger.error("Cannot create folder", e);
            throw new AuthoringException("Cannot create folder ", e);
        }
    }

    public void deleteFolder(String siteId, String path) throws AuthoringException {
        try {
            assetManagementPlugin.deleteFolder(siteId, path);
        } catch (PluginOperationException e) {
            logger.error("Cannot delete folder", e);
            throw new AuthoringException("Cannot create folder", e);
        }
    }

    public void createAsset(String siteId, String path, String name, byte[] data, String contentType) throws AuthoringException {
        try {
            assetManagementPlugin.createAsset(siteId, path, name, data, contentType);
        } catch (PluginOperationException e) {
            logger.error("Cannot create folder", e);
            throw new AuthoringException("Cannot create folder ", e);
        }
    }

    public void deleteAsset(String siteId, String path, String name) throws AuthoringException {
        try {
            assetManagementPlugin.deleteAsset(siteId, path, name);
        } catch (PluginOperationException e) {
            logger.error("Cannot delete folder", e);
            throw new AuthoringException("Cannot create folder", e);
        }
    }
}
