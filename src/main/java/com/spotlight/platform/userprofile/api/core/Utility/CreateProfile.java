package com.spotlight.platform.userprofile.api.core.Utility;

import com.spotlight.platform.userprofile.api.model.profile.UserProfile;
import com.spotlight.platform.userprofile.api.model.profile.primitives.Command;

import java.time.Instant;

public class CreateProfile {

    public static UserProfile createUserProfileForNewUser(Command command) {
        Instant instant = Instant.now();
        return new UserProfile(command.getUserId(), instant, command.getProperties());
    }
}
