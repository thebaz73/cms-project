package ms.cms.authoring.common.business;

import ms.cms.data.CmsAssetRepository;
import ms.cms.data.CmsSiteRepository;
import ms.cms.domain.CmsAsset;
import ms.cms.domain.CmsSite;
import ms.cms.domain.CmsUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * AssetManager
 * Created by bazzoni on 04/05/2015.
 */
@Component
public class AssetManager {
    @Autowired
    private CmsSiteRepository cmsSiteRepository;
    @Autowired
    private CmsAssetRepository cmsAssetRepository;

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
}
