package sparkle.cms.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Role
 * Created by thebaz on 21/03/15.
 */
public enum CommentApprovalMode {
    SELF_APPROVAL("SELF_APPROVAL"),
    MANAGER_APPROVAL("MANAGER_APPROVAL");

    public static final CommentApprovalMode[] ALL = {SELF_APPROVAL, MANAGER_APPROVAL};

    private final String name;

    CommentApprovalMode(final String name) {
        this.name = name;
    }

    @JsonCreator
    public static CommentApprovalMode forName(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null for role");
        }
        if (name.toUpperCase().equals("SELF_APPROVAL")) {
            return SELF_APPROVAL;
        } else if (name.toUpperCase().equals("MANAGER_APPROVAL")) {
            return MANAGER_APPROVAL;
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
