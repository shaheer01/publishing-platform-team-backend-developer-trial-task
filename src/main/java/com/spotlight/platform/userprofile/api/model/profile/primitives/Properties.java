package com.spotlight.platform.userprofile.api.model.profile.primitives;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class Properties {

        private HashMap<UserProfilePropertyName, UserProfilePropertyValue> property;

        public Properties() {
                this.property = new HashMap<>();
        }
}
