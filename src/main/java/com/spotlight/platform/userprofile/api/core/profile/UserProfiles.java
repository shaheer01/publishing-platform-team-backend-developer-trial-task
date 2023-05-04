package com.spotlight.platform.userprofile.api.core.profile;

import com.spotlight.platform.userprofile.api.model.profile.primitives.Command;

import javax.ws.rs.core.Response;

public interface UserProfiles {
    Response execute(Command command) throws Exception;
}

