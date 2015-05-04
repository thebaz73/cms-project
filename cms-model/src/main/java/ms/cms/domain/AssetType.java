package ms.cms.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * AssetType
 * Created by bazzoni on 04/05/2015.
 */
public enum AssetType {
    IMAGE("IMAGE"),
    DOCUMENT("DOCUMENT"),
    SPREADSHEET("SPREADSHEET"),
    PRESENTATION("PRESENTATION"),
    PDF("PDF"),
    TEXT("TEXT"),
    VIDEO("VIDEO"),
    AUDIO("AUDIO"),
    ZIP("ZIP"),
    BINARY("BINARY");

    public static final AssetType[] ALL = {IMAGE, DOCUMENT, SPREADSHEET, PRESENTATION, PDF, TEXT, VIDEO, AUDIO, ZIP, BINARY};

    private final String name;

    AssetType(final String name) {
        this.name = name;
    }

    @JsonCreator
    public static AssetType forName(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null for role");
        }
        if (name.toUpperCase().equals("IMAGE")) {
            return IMAGE;
        } else if (name.toUpperCase().equals("DOCUMENT")) {
            return DOCUMENT;
        } else if (name.toUpperCase().equals("SPREADSHEET")) {
            return SPREADSHEET;
        } else if (name.toUpperCase().equals("PRESENTATION")) {
            return PRESENTATION;
        } else if (name.toUpperCase().equals("PDF")) {
            return PDF;
        } else if (name.toUpperCase().equals("TEXT")) {
            return TEXT;
        } else if (name.toUpperCase().equals("VIDEO")) {
            return VIDEO;
        } else if (name.toUpperCase().equals("AUDIO")) {
            return AUDIO;
        } else if (name.toUpperCase().equals("ZIP")) {
            return ZIP;
        } else if (name.toUpperCase().equals("BINARY")) {
            return BINARY;
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
