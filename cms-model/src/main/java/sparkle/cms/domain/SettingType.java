package sparkle.cms.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * SettingType
 * Created by bazzoni on 12/05/2015.
 */
public enum SettingType {
    BOOL("BOOL"),
    TEXT("TEXT"),
    EMAIL("EMAIL"),
    INET("INET"),
    INTEGER("INTEGER"),
    DOUBLE("DOUBLE")/*,
    ENUM("ENUM")*/;

    public static final SettingType[] ALL = {BOOL, TEXT, EMAIL, INET, INTEGER, DOUBLE/*, ENUM*/};

    private final String name;

    SettingType(final String name) {
        this.name = name;
    }

    @JsonCreator
    public static SettingType forName(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null for type");
        }
        if (name.toUpperCase().equals("BOOL")) {
            return BOOL;
        } else if (name.toUpperCase().equals("TEXT")) {
            return TEXT;
        } else if (name.toUpperCase().equals("EMAIL")) {
            return EMAIL;
        } else if (name.toUpperCase().equals("INET")) {
            return INET;
        } else if (name.toUpperCase().equals("INTEGER")) {
            return INTEGER;
        } else if (name.toUpperCase().equals("DOUBLE")) {
            return DOUBLE;
        /*} else if (name.toUpperCase().equals("ENUM")) {
            return ENUM;*/
        }
        throw new IllegalArgumentException("Name \"" + name + "\" does not correspond to any Feature");
    }

    public String getName() {
        return this.name;
    }

    @JsonValue
    @Override
    public String toString() {
        return getName();
    }
}
