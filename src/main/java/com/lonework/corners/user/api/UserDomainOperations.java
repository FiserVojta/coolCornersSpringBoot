package com.lonework.corners.user.api;

import com.lonework.corners.user.model.User;
import com.lonework.corners.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDomainOperations implements UserOperations {

    private final UserService userService;

    public UserDomainOperations(UserService userService) {
        this.userService = userService;
    }

    @Override
    public User getRequiredUserByEmail(String email) {
        User user = userService.getUser(email);
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }
        return user;
    }

    @Override
    public List<User> getUsersByIds(List<Long> ids) {
        List<User> users = userService.getUsersByIds(ids);
        if (users.size() != ids.size()) {
            throw new EntityNotFoundException("Some users were not found");
        }
        return users;
    }
}
