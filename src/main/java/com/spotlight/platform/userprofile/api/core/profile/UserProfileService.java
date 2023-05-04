package com.spotlight.platform.userprofile.api.core.profile;

import com.spotlight.platform.userprofile.api.core.exceptions.EntityNotFoundException;
import com.spotlight.platform.userprofile.api.core.profile.persistence.UserProfileDao;
import com.spotlight.platform.userprofile.api.model.profile.UserProfile;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserId;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
public class UserProfileService {
    private final UserProfileDao userProfileDao;

    @Inject
    public UserProfileService(UserProfileDao userProfileDao) {
        this.userProfileDao = userProfileDao;
    }

    public UserProfileDao getUserProfileDao() {
        return userProfileDao;
    }

    public UserProfile get(UserId userId) {
        return userProfileDao.get(userId).orElseThrow(EntityNotFoundException::new);
    }

    public boolean put(UserProfile userProfile) {
        try {
            userProfileDao.put(userProfile);
            return true;
        } catch ( EntityNotFoundException e) {
            log.error("Creation of UserId failed!");
            return false;
        }
    }
}
