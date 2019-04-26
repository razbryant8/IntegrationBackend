package smartspace.layout;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import smartspace.data.UserEntity;
import smartspace.logic.UserService;
import org.springframework.beans.factory.annotation.Autowired;

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
                    .toArray(new UserBoundary[0]);        else
            throw new RuntimeException("Unauthorized operation");

    }

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

    private boolean validate(String adminSmartspace, String adminEmail) {
        return true;
        //TODO complete the user servica getByKey usage in order to validate user's credentials
        //return this.userService.//to be completed
    }


}
