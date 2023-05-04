package com.spotlight.platform.userprofile.api.web.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotlight.platform.userprofile.api.core.profile.IncrementCommand;
import com.spotlight.platform.userprofile.api.core.profile.ReplaceCommand;
import com.spotlight.platform.userprofile.api.core.profile.UserProfileService;
import com.spotlight.platform.userprofile.api.model.profile.UserProfile;
import com.spotlight.platform.userprofile.api.model.profile.primitives.*;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class UserProfileCreation {

    private final UserProfileService userProfileService;
    Map<UserProfilePropertyName, UserProfilePropertyValue> userProfileProperties = new HashMap<>();

    @Inject
    public UserProfileCreation(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
        this.replaceCommand = new ReplaceCommand(userProfileService.getUserProfileDao());
        this.incrementCommand = new IncrementCommand(userProfileService.getUserProfileDao());
    }

    private ReplaceCommand replaceCommand;

    private IncrementCommand incrementCommand;

    @POST
    @Path("/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putUserProfile(@PathParam ("userId") String userId, String requestBody)  throws JsonProcessingException {
        try {
        UserId userID = new UserId(userId);
        Instant instant = Instant.now();
        String uuid = UUID.randomUUID().toString();
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

    @POST
    @Path("/single-profile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUserProfile(String requestBody) {
        try {
            if (!Objects.equals(requestBody, "")) {

                Command command = new ObjectMapper().readValue(requestBody, Command.class);
                return userProfileProcess(command);
            } else {
                UserProfile userProfile = createUserProfile(requestBody);
                return Response.ok(userProfileService.put(userProfile)).build();
            }

            } catch (JsonProcessingException| RuntimeException ex) {
//            throw new RuntimeException(ex);
            log.error("Json parsing error ", ex);
        }
        return Response.status(400).build();
    }

    private Response userProfileProcess(Command command) {
        boolean output = false;
        //Check if User Profile already created for the userId.
        if (command.getUserId() != null) {

            switch (command.getType()) {
                case replace:
                    //function to replace
                    return replaceCommand.execute(command);
                case increment:
                    //function to increment
                    return incrementCommand.execute(command);
                case collect:
                    //function to collect
                default:
                    //default type would just print the result
            }
        } else {
            log.error("Missing UserId {}", command);
            return Response.status(400).build();
        }
        return Response.ok(output).build();
    }

    private UserProfile createUserProfile (String requestBody) throws JsonProcessingException {
        Instant instant = Instant.now();
        UserId userID = new UserId(UUID.randomUUID().toString());
        userProfileProperties = new ObjectMapper().readValue(requestBody, new TypeReference<Map<UserProfilePropertyName, UserProfilePropertyValue>>() {
        });
        UserProfile userProfile = new UserProfile(userID, instant, userProfileProperties);
        return userProfile;
    }
}

