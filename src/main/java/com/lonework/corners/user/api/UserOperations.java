package com.lonework.corners.user.api;

import com.lonework.corners.user.model.User;

import java.util.List;

public interface UserOperations {

    User getRequiredUserByEmail(String email);

    List<User> getUsersByIds(List<Long> ids);
}
