package com.spotlight.platform.userprofile.api.web.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotlight.platform.userprofile.api.core.profile.UserProfileService;
import com.spotlight.platform.userprofile.api.model.profile.UserProfile;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserId;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfilePropertyName;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfilePropertyValue;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class UserProfileCreation {

    private final UserProfileService userProfileService;

    @Inject
    public UserProfileCreation(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }
    @POST
    @Path("/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putUserProfile(@PathParam ("userId") String userId, String requestBody)  throws JsonProcessingException {
        try {
        UserId userID = new UserId(userId);
        Instant instant = Instant.now();
        String uuid = UUID.randomUUID().toString();
        Map<UserProfilePropertyName, UserProfilePropertyValue> userProfileProperties = new HashMap<>();
        userProfileProperties.put(new UserProfilePropertyName("User Unique Id"), new UserProfilePropertyValue(uuid));
        userProfileProperties.put(new UserProfilePropertyName("userId"), new UserProfilePropertyValue(userId));
        if (!requestBody.equals("")) {
            userProfileProperties = new ObjectMapper().readValue(requestBody, new TypeReference<Map<UserProfilePropertyName, UserProfilePropertyValue>>() {
            });
        }
            UserProfile userProfile = new UserProfile(userID, instant, userProfileProperties);
            return Response.ok(userProfileService.put(userProfile)).build();
        } catch (Exception ex) {
            log.error("Error in parsing the json", ex);
            return Response.status(400).build();
        }
    }
}

