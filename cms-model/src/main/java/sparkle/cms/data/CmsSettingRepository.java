package sparkle.cms.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import sparkle.cms.domain.CmsSetting;

import java.util.List;

/**
 * CmsTagRepository
 * Created by thebaz on 24/03/15.
 */
public interface CmsSettingRepository extends MongoRepository<CmsSetting, String> {
    /**
     * Finds @CmsSetting given its key
     *
     * @param key key key
     * @return list of @CmsSetting
     */
    List<CmsSetting> findByKey(String key);

    /**
     * Finds @CmsSetting given its key
     *
     * @param key key key
     * @return page of @CmsSetting
     */
    Page<CmsSetting> findByKey(String key, Pageable pageable);

    /**
     * Finds @CmsSetting given its filter
     *
     * @param filter filter
     * @return list of @CmsSetting
     */
    List<CmsSetting> findByUserId(String filter);

    /**
     * Finds @CmsSetting given its filter
     *
     * @param filter filter
     * @return page of @CmsSetting
     */
    Page<CmsSetting> findByUserId(String filter, Pageable pageable);

    /**
     * Finds @CmsSetting given its key and filter
     *
     * @param key    key key
     * @param filter filter
     * @return list of @CmsSetting
     */
    List<CmsSetting> findByKeyAndUserId(String key, String filter);

    /**
     * Finds @CmsSetting given its address
     *
     * @param key    key key
     * @param filter filter
     * @return list of @CmsSetting
     */
    Page<CmsSetting> findByKeyAndUserId(String key, String filter, Pageable pageable);
}
