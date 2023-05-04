package com.spotlight.platform.userprofile.api.core.profile;

import com.spotlight.platform.userprofile.api.core.exceptions.EntityNotFoundException;
import com.spotlight.platform.userprofile.api.core.profile.persistence.UserProfileDao;
import com.spotlight.platform.userprofile.api.model.profile.UserProfile;
import com.spotlight.platform.userprofile.api.model.profile.primitives.Command;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfilePropertyName;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfilePropertyValue;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class ReplaceCommand implements UserProfiles {
    private Map<UserProfilePropertyName, UserProfilePropertyValue> properties;

    private final UserProfileDao userProfileDao;

    @Inject
    public ReplaceCommand(UserProfileDao userProfileDao) {
        this.userProfileDao = userProfileDao;
    }

    @Override
    public Response execute(Command command) {
        try {
            UserProfile userProfile = userProfileDao.get(command.getUserId()).orElse(new UserProfile(null, null, null));
            if (userProfile.userProfileProperties() == null) {
                // The userProfile object is null or empty
                Instant instant = Instant.now();
                userProfile = new UserProfile(command.getUserId(), instant, command.getProperties());
                userProfileDao.put(userProfile);
            } else {
                properties = command.getProperties();
                Set<UserProfilePropertyName> keys = properties.keySet();
                for (UserProfilePropertyName key : keys) {
                    UserProfilePropertyValue value = properties.get(key);
                    userProfile.userProfileProperties().put(key, value);
                    userProfileDao.put(userProfile);
                }
            }
            return Response.ok(userProfile).build();
        } catch (Exception e) {
            throw new EntityNotFoundException();
        }
    }
}