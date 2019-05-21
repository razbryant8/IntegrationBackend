package smartspace.layout;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import smartspace.dao.UserNotFoundException;
import smartspace.data.UserEntity;
import smartspace.logic.UserService;

import java.util.Optional;
import java.util.stream.Collectors;


@RestController
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @RequestMapping(
            method = RequestMethod.GET,
            path = "/smartspace/admin/users/{adminSmartspace}/{adminEmail}",
            produces = MediaType.APPLICATION_JSON_VALUE)

    public UserBoundary[] getAll(
            @PathVariable("adminSmartspace") String adminSmartspace,
            @PathVariable("adminEmail") String adminEmail,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
            return this.userService.getAll(adminSmartspace,adminEmail,size, page)
                    .stream()
                    .map(UserBoundary::new)
                    .collect(Collectors.toList())
                    .toArray(new UserBoundary[0]);

    }


    @Transactional
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/smartspace/admin/users/{adminSmartspace}/{adminEmail}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserBoundary[] store(
            @PathVariable("adminSmartspace") String adminSmartspace,
            @PathVariable("adminEmail") String adminEmail,
            @RequestBody UserBoundary[] users) {
            UserEntity[] convertedUserEntities = new UserEntity[users.length];
            for (int i = 0; i < users.length; i++)
                convertedUserEntities[i] = users[i].convertToEntity();
            UserEntity[] usersEntities = this.userService.store(adminSmartspace, adminEmail, convertedUserEntities);

            UserBoundary[] usersBoundary = new UserBoundary[usersEntities.length];
            for (int i = 0; i < usersEntities.length; i++)
                usersBoundary[i] = new UserBoundary(usersEntities[i]);

            return usersBoundary;

    }

    @Transactional
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/smartspace/users",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserBoundary create(
            @RequestBody NewUserFormBoundary user) {

        return
                new UserBoundary(
                        this.userService
                                .create(user
                                        .convertToEntity()));
    }


    @RequestMapping(
            method = RequestMethod.GET,
            path = "/smartspace/users/login/{userSmartspace}/{userEmail}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserBoundary getUser(
            @PathVariable("userSmartspace") String userSmartspace,
            @PathVariable("userEmail") String userEmail) {
        Optional<UserEntity> rv;
        rv = this.userService.getUserByMailAndSmartSpace(userEmail, userSmartspace);
        if (rv.isPresent())
            return new UserBoundary(rv.get());
        else
            throw new UserNotFoundException("User not found!");
    }


    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/smartspace/users/login/{userSmartspace}/{userEmail}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateUser(
            @PathVariable("userSmartspace") String userSmartspace,
            @PathVariable("userEmail") String userEmail,
            @RequestBody UserBoundary updateBoundary) {
        this.userService
                .update(userSmartspace, userEmail, updateBoundary.convertToEntity());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleException(UserNotFoundException e) {
        String message = e.getMessage();
        if (message == null) {
            message = "message not found";
        }
        return new ErrorMessage(message);
    }

/*
    private boolean validate(String adminSmartspace, String adminEmail) {
        Optional<UserEntity> dbUser = userService.getUserByMailAndSmartSpace(adminEmail, adminSmartspace);
        if (!dbUser.isPresent() || !dbUser.get().getRole().equals(UserRole.ADMIN))
            // || adminSmartspace.equals(this.userService.getCurrentSmartspace()))
            return false;
        return true;
    }
*/

}
