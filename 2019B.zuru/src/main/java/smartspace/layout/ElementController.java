package smartspace.layout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import smartspace.logic.ElementService;
import smartspace.logic.UserService;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
public class ElementController {

    private ElementService elementService;
    private UserService userService;

    @Autowired
    public ElementController(ElementService elementService, UserService userService) {
        this.elementService = elementService;
        this.userService = userService;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/smartspace/admin/elements/{adminSmartspace}/{adminEmail}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ElementBoundary[] getAll(
            @PathVariable("adminSmartspace") String adminSmartspace,
            @PathVariable("adminEmail") String adminEmail,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
        if (validate(adminSmartspace, adminEmail)) {
            return this.elementService
                    .getAll(size, page)
                    .stream()
                    .map(ElementBoundary::new)
                    .collect(Collectors.toList())
                    .toArray(new ElementBoundary[0]);
        } else
            throw new RuntimeException("Unauthorized operation");
    }

    @Transactional
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/smartspace/admin/elements/{adminSmartspace}/{adminEmail}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ElementBoundary[] store(
            @PathVariable("adminSmartspace") String adminSmartspace,
            @PathVariable("adminEmail") String adminEmail,
            @RequestBody ElementBoundary[] elementBoundary) {
        if (validate(adminSmartspace, adminEmail))
            return IntStream.range(0, elementBoundary.length)
                    .mapToObj(i -> elementBoundary[i].convertToEntity())
                    .map(this.elementService::store)
                    .map(ElementBoundary::new)
                    .collect(Collectors.toList())
                    .toArray(new ElementBoundary[0]);
        else
            throw new RuntimeException("Unauthorized operation");
    }

    private boolean validate(String adminSmartspace, String adminEmail) {
        return true;
        //TODO complete the user servica getByKey usage in order to validate user's credentials
        //return this.userService.//to be completed
    }
}
