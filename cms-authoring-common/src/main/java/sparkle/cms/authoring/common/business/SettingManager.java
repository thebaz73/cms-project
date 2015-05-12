package sparkle.cms.authoring.common.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import sparkle.cms.data.CmsSettingRepository;
import sparkle.cms.domain.CmsSetting;
import sparkle.cms.domain.CmsUser;
import sparkle.cms.service.CmsSettingAware;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * SettingManager
 * Created by bazzoni on 11/05/2015.
 */
@Component
public class SettingManager {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CmsSettingRepository cmsSettingRepository;
    @Autowired
    ApplicationContext applicationContext;
    private Map<String, CmsSettingAware> settingAwareMap;

    @PostConstruct
    private void initialize() {
        settingAwareMap = applicationContext.getBeansOfType(CmsSettingAware.class);
    }

    public void reloadSettings() {
        for (Map.Entry<String, CmsSettingAware> entry : settingAwareMap.entrySet()) {
            logger.debug("Reloading settings for bean {}", entry.getKey());
            final CmsSettingAware cmsSettingAwareService = entry.getValue();
            cmsSettingAwareService.doSettingAwareReload(true);
        }
    }

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
