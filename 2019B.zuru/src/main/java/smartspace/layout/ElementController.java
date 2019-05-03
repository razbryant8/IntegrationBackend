package smartspace.layout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.logic.ElementService;
import smartspace.logic.UserService;

import java.util.Optional;
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
        if (validate(adminSmartspace, adminEmail, UserRole.ADMIN)) {
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
        if (validate(adminSmartspace, adminEmail, UserRole.ADMIN))
            return IntStream.range(0, elementBoundary.length)
                    .mapToObj(i -> elementBoundary[i].convertToEntity())
                    .map(this.elementService::store)
                    .map(ElementBoundary::new)
                    .collect(Collectors.toList())
                    .toArray(new ElementBoundary[0]);
        else
            throw new RuntimeException("Unauthorized operation");
    }

    @Transactional
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/smartspace/elements/{managerSmartspace}/{managerEmail}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ElementBoundary create(
            @PathVariable("managerSmartspace") String managerSmartspace,
            @PathVariable("managerEmail") String managerEmail,
            @RequestBody ElementBoundary elementBoundary) {
        if (validate(managerSmartspace, managerEmail, UserRole.MANAGER))
            return new ElementBoundary(this.elementService.create(elementBoundary.convertToEntity()));
        else
            throw new RuntimeException("Unauthorized operation");
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/smartspace/elements/{userSmartspace}/{userEmail}/{elementSmartspace}/{elementId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ElementBoundary getElement(
            @PathVariable("userSmartspace") String userSmartspace,
            @PathVariable("userEmail") String userEmail,
            @PathVariable("elementSmartspace") String elementSmartspace,
            @PathVariable("elementId") String elementId) {

        return new ElementBoundary(this.elementService.getById(elementId, elementSmartspace).orElse(null));
    }


    @RequestMapping(
            method = RequestMethod.GET,
            path = "/smartspace/elements/{userSmartspace}/{userEmail}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ElementBoundary[] getByName(
            @PathVariable("userSmartspace") String userSmartspace,
            @PathVariable("userEmail") String userEmail,
            @RequestParam(name = "search") Search search,
            @RequestParam(name = "value") String value,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
        if (search.equals(Search.name)) {
            return this.elementService
                    .getByName(size, page, value)
                    .stream()
                    .map(ElementBoundary::new)
                    .collect(Collectors.toList())
                    .toArray(new ElementBoundary[0]);
        } else
            throw new RuntimeException("Invalid search value");
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/smartspace/elements/{userSmartspace}/{userEmail}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ElementBoundary[] getByType(
            @PathVariable("userSmartspace") String userSmartspace,
            @PathVariable("userEmail") String userEmail,
            @RequestParam(name = "search") Search search,
            @RequestParam(name = "value") String value,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
        if (search.equals(Search.type)) {
            return this.elementService
                    .getByType(size, page, value)
                    .stream()
                    .map(ElementBoundary::new)
                    .collect(Collectors.toList())
                    .toArray(new ElementBoundary[0]);
        } else
            throw new RuntimeException("Invalid search value");
    }


    private boolean validate(String smartspace, String email, UserRole userRole) {
        Optional<UserEntity> dbUser = userService.getUserByMailAndSmartSpace(email, smartspace);
        if (dbUser.isPresent() && dbUser.get().getRole().equals(userRole))
            return true;
        return false;
    }
}
