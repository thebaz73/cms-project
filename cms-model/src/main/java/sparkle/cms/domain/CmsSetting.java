package sparkle.cms.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * CmsSetting
 * Created by bazzoni on 06/05/2015.
 */
@Document
public class CmsSetting {
    @Id
    private String id;

    @Indexed(unique = true)
    private String key;
    private Object value;

    public CmsSetting() {
    }

    @PersistenceConstructor
    public CmsSetting(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
