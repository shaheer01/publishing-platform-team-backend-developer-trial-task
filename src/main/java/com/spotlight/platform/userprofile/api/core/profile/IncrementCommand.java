package com.spotlight.platform.userprofile.api.core.profile;

import com.spotlight.platform.userprofile.api.core.Utility.CreateProfile;
import com.spotlight.platform.userprofile.api.core.exceptions.EntityNotFoundException;
import com.spotlight.platform.userprofile.api.core.profile.persistence.UserProfileDao;
import com.spotlight.platform.userprofile.api.model.profile.UserProfile;
import com.spotlight.platform.userprofile.api.model.profile.primitives.Command;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfilePropertyName;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfilePropertyValue;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Slf4j
public class IncrementCommand implements UserProfiles {
    private Map<UserProfilePropertyName, UserProfilePropertyValue> properties;

    private final UserProfileDao userProfileDao;

    @Inject
    public IncrementCommand(UserProfileDao userProfileDao) {
        this.userProfileDao = userProfileDao;
    }

    @Override
    public Response execute(Command command) {
        try {
            UserProfile userProfile = userProfileDao.get(command.getUserId()).orElse(new UserProfile(null, null, null));
            if (userProfile.userProfileProperties() == null) {
                // The userProfile object is null or empty
                userProfile = CreateProfile.createUserProfileForNewUser(command);
                userProfileDao.put(userProfile);
            } else {
                Map<UserProfilePropertyName, UserProfilePropertyValue> properties = command.getProperties();
                // iterate over the properties and increment/decrement them in the userProfile
                for (Map.Entry<UserProfilePropertyName, UserProfilePropertyValue> entry : properties.entrySet()) {
                    UserProfilePropertyName key = entry.getKey();
                    UserProfilePropertyValue value = entry.getValue();
                    if (userProfile.userProfileProperties().containsKey(key)) {
                        UserProfilePropertyValue currentValue = userProfile.userProfileProperties().get(key);
                        if (currentValue.getValue() instanceof Number && value.getValue() instanceof Number) {
                            Number newValue = ((Number) currentValue.getValue()).doubleValue() + ((Number) value.getValue()).doubleValue();
                            userProfile.userProfileProperties().put(key, new UserProfilePropertyValue(newValue));
                        } else {
                            log.warn("Cannot increment/decrement non-numeric property: {}", key);
                        }
                    } else {
                        log.warn("Property not found in userProfile: {}", key);
                    }
                }
            }
            return Response.ok(userProfile).build();
        } catch (Exception e) {
            throw new EntityNotFoundException();
        }
    }
}
