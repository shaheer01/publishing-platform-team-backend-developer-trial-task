package com.spotlight.platform.userprofile.api.model.profile;

import com.spotlight.platform.userprofile.api.model.profile.primitives.Command;

import java.util.List;

public class CommandList {
    private List<Command> userProfileBodies;

    public List<Command> getCommands() {
        return userProfileBodies;
    }

    public void setCommands(List<Command> userProfileBodies) {
        this.userProfileBodies = userProfileBodies;
    }
}

