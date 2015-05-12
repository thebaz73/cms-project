package sparkle.cms.service;

/**
 * CmsSettingAware
 * Created by bazzoni on 12/05/2015.
 */
public interface CmsSettingAware {
    /**
     * Do reload all setting aware objects
     *
     * @param force force setting reload
     */
    public void doSettingAwareReload(boolean force);
}
