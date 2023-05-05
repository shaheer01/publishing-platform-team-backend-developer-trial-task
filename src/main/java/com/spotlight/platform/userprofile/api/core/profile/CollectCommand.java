package com.spotlight.platform.userprofile.api.core.profile;

import com.spotlight.platform.userprofile.api.core.Utility.CreateProfile;
import com.spotlight.platform.userprofile.api.core.exceptions.EntityNotFoundException;
import com.spotlight.platform.userprofile.api.core.profile.persistence.UserProfileDao;
import com.spotlight.platform.userprofile.api.model.profile.UserProfile;
import com.spotlight.platform.userprofile.api.model.profile.primitives.Command;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserId;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfilePropertyName;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfilePropertyValue;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.*;

@Getter
@Setter
@Slf4j
public class CollectCommand implements UserProfiles {
    private Map<UserProfilePropertyName, UserProfilePropertyValue> properties;

    private final UserProfileDao userProfileDao;

    @Inject
    public CollectCommand(UserProfileDao userProfileDao) {
        this.userProfileDao = userProfileDao;
    }
    @SuppressWarnings("unchecked")
    @Override
    public Response execute(Command command) {
        try {
            UserId userId = command.getUserId();
            Map<UserProfilePropertyName, UserProfilePropertyValue> properties = command.getProperties();
            UserProfile userProfile = userProfileDao.get(userId).orElseThrow(EntityNotFoundException::new);
            if (userProfile.userProfileProperties() == null) {
                // The userProfile object is null or empty
                userProfile = CreateProfile.createUserProfileForNewUser(command);
                userProfileDao.put(userProfile);
            } else {
                for (Map.Entry<UserProfilePropertyName, UserProfilePropertyValue> entry : properties.entrySet()) {
                    UserProfilePropertyName propertyName = entry.getKey();
                    UserProfilePropertyValue propertyValue = entry.getValue();

                    UserProfilePropertyValue currentValue = userProfile.userProfileProperties().get(propertyName);
                    if (currentValue == null) {
                        // If the current value is null, add the list to the user profile
                        userProfile.userProfileProperties().put(propertyName, propertyValue);
                    } else if (currentValue instanceof List<?>) {
                        // If the current value is a list, append the new values to it
                        List<UserProfilePropertyValue> currentList = (List<UserProfilePropertyValue>) currentValue;
                        List<UserProfilePropertyValue> newValueList = (List<UserProfilePropertyValue>) propertyValue;
                        currentList.addAll(newValueList);
                        userProfileDao.put(userProfile);
                    } else {
                        // If the current value is not a list, create a new list and add the current and new values to it
                        List<UserProfilePropertyValue> newList = new ArrayList<>();
                        newList.add(currentValue);
                        newList.add(propertyValue);
                        userProfile.userProfileProperties().put(propertyName, new UserProfilePropertyValue(newList));
                        userProfileDao.put(userProfile);
                    }
                }
            }
            userProfileDao.put(userProfile);
            return Response.ok(userProfile).build();
        } catch (Exception e) {
            log.error("Problem in collect Command in execution.Reason:", e);
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}
