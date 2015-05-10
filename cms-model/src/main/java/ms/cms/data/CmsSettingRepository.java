package ms.cms.data;

import ms.cms.domain.CmsSetting;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * CmsTagRepository
 * Created by thebaz on 24/03/15.
 */
public interface CmsSettingRepository extends MongoRepository<CmsSetting, String> {
    /**
     * Finds @CmsSetting given its address
     *
     * @param key key key
     * @return list of @CmsSetting
     */
    List<CmsSetting> findByKey(String key);
}
