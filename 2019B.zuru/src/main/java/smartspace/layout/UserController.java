package smartspace.layout;


import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.logic.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


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
        if (validate(adminSmartspace, adminEmail))
            return this.userService.getAll(size, page)
                    .stream()
                    .map(UserBoundary::new)
                    .collect(Collectors.toList())
                    .toArray(new UserBoundary[0]);
        else
            throw new RuntimeException("Unauthorized operation");

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
        if (validate(adminSmartspace, adminEmail))
            return IntStream.range(0, users.length)
                    .mapToObj(i -> users[i].convertToEntity())
                    .map(this.userService::store)
                    .map(UserBoundary::new)
                    .collect(Collectors.toList())
                    .toArray(new UserBoundary[0]);
        else
            throw new RuntimeException("Unauthorized operation");

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
            return null;
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


    private boolean validate(String adminSmartspace, String adminEmail) {
        Optional<UserEntity> dbUser = userService.getUserByMailAndSmartSpace(adminEmail, adminSmartspace);
        if (!dbUser.isPresent() || !dbUser.get().getRole().equals(UserRole.ADMIN))
            // || adminSmartspace.equals(this.userService.getCurrentSmartspace()))
            return false;
        return true;
    }


}
