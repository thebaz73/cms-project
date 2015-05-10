package sparkle.cms.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Role
 * Created by thebaz on 21/03/15.
 */
public enum WorkflowType {
    SELF_APPROVAL_WF("SELF_APPROVAL_WF"),
    MANAGER_APPROVAL_WF("MANAGER_APPROVAL_WF");

    public static final WorkflowType[] ALL = {SELF_APPROVAL_WF, MANAGER_APPROVAL_WF};

    private final String name;

    WorkflowType(final String name) {
        this.name = name;
    }

    @JsonCreator
    public static WorkflowType forName(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null for role");
        }
        if (name.toUpperCase().equals("SELF_APPROVAL_WF")) {
            return SELF_APPROVAL_WF;
        } else if (name.toUpperCase().equals("MANAGER_APPROVAL_WF")) {
            return MANAGER_APPROVAL_WF;
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
