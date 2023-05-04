package com.spotlight.platform.userprofile.api.model.profile.primitives;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Command {

    private UserId userId;
    private Type type;
    private HashMap<UserProfilePropertyName, UserProfilePropertyValue> properties;

}
