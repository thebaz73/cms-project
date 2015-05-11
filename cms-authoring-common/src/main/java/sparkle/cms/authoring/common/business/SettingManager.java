package sparkle.cms.authoring.common.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import sparkle.cms.data.CmsSettingRepository;
import sparkle.cms.domain.CmsSetting;
import sparkle.cms.domain.CmsUser;

/**
 * SettingManager
 * Created by bazzoni on 11/05/2015.
 */
@Component
public class SettingManager {
    @Autowired
    private CmsSettingRepository cmsSettingRepository;

    public Page<CmsSetting> findSettings(CmsUser cmsUser, Pageable pageable) {
        return cmsSettingRepository.findByFilter(cmsUser.getId(), pageable);
    }

    public void editSetting(CmsSetting cmsSetting) {
        cmsSettingRepository.save(cmsSetting);
    }

    public CmsSetting findSetting(String settingId) {
        return cmsSettingRepository.findOne(settingId);
    }
}
