package smartspace.layout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import smartspace.logic.ActionService;

import java.util.stream.Collectors;

@RestController
public class ActionController {

    private ActionService actionService;

    @Autowired
    public ActionController(ActionService actionService) {
        this.actionService = actionService;
    }


//    @RequestMapping(
//            method = RequestMethod.GET,
//            path = "/smartspace/admin/actions/{adminSmartspace}/{adminEmail}",
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    public ElementBoundary[] getAll(
//            @PathVariable("adminSmartspace") String adminSmartspace,
//            @PathVariable("adminEmail") String adminEmail,
//            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
//            @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
//        if (validate(adminSmartspace, adminEmail)) {
//            return this.actionService
//                    .getAll(size, page)
//                    .stream()
//                    .map(ActionB::new)
//                    .collect(Collectors.toList())
//                    .toArray(new ElementBoundary[0]);
//        } else
//            throw new RuntimeException("Unauthorized operation");
//    }

    private boolean validate(String adminSmartspace, String adminEmail) {
        return true;
        //TODO complete the user servica getByKey usage in order to validate user's credentials
        //return this.userService.//to be completed
    }
}
