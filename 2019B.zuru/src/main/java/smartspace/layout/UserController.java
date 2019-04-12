package smartspace.layout;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import smartspace.data.UserEntity;
import smartspace.logic.UserService;
import org.springframework.beans.factory.annotation.Autowired;

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
            path = "/USER",
            produces = MediaType.APPLICATION_JSON_VALUE)

    public UserBoundary[] getAll(
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
        return this.userService
                .getAll(size, page)
                .stream()
                .map(UserBoundary::new)
                .collect(Collectors.toList())
                .toArray(new UserBoundary[0]);
    }


    /*
need to complete
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/USER",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)

    public UserBoundary store(
            @RequestBody UserBoundary user) {


        UserEntity userEntity = user.convertToEntity();
        UserEntity responseEntity = this.userService
                .store(userEntity);
        UserBoundary result = new UserBoundary(responseEntity);
        return result;


    }
*/
}
