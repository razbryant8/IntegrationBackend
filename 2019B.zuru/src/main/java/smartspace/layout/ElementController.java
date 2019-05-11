package smartspace.layout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import smartspace.dao.ElementNotFoundException;
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
        return this.elementService
                .getAll(size, page, getUserRole(adminSmartspace, adminEmail))
                .stream()
                .map(ElementBoundary::new)
                .collect(Collectors.toList())
                .toArray(new ElementBoundary[0]);

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
        return IntStream.range(0, elementBoundary.length)
                .mapToObj(i -> elementBoundary[i].convertToEntity())
                .map(x -> this.elementService.store(x, getUserRole(adminSmartspace, adminEmail)))
                .map(ElementBoundary::new)
                .collect(Collectors.toList())
                .toArray(new ElementBoundary[0]);
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
        return new ElementBoundary(this.elementService
                .create(elementBoundary.convertToEntity(), getUserRole(managerSmartspace, managerEmail)));
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
        return new ElementBoundary(this.elementService.
                getById(elementId, elementSmartspace, getUserRole(userSmartspace, userEmail)));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/smartspace/elements/{userSmartspace}/{userEmail}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ElementBoundary[] getByAttribute(
            @PathVariable("userSmartspace") String userSmartspace,
            @PathVariable("userEmail") String userEmail,
            @RequestParam(name = "search") String search,
            @RequestParam(name = "value" ,required = false) String value,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
        if (search.toLowerCase().equals(Search.TYPE.toString().toLowerCase())) {
            return this.elementService
                    .getByType(size, page, value, getUserRole(userSmartspace, userEmail))
                    .stream()
                    .map(ElementBoundary::new)
                    .collect(Collectors.toList())
                    .toArray(new ElementBoundary[0]);
        } else if (search.toLowerCase().equals((Search.NAME.toString().toLowerCase()))) {
            return this.elementService
                    .getByName(size, page, value, getUserRole(userSmartspace, userEmail))
                    .stream()
                    .map(ElementBoundary::new)
                    .collect(Collectors.toList())
                    .toArray(new ElementBoundary[0]);
        }
        //TODO else if search.equals(Search.location)...
        else
            throw new ElementNotFoundException("Invalid search value");
    }


    @Transactional
    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/smartspace/elements/{managerSmartspace}/{managerEmail}/{elementSmartspace}/{elementId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(
            @PathVariable("managerSmartspace") String managerSmartspace,
            @PathVariable("managerEmail") String managerEmail,
            @PathVariable("elementSmartspace") String elementSmartspace,
            @PathVariable("elementId") String elementId,
            @RequestBody ElementBoundary elementBoundary) {
            this.elementService.update(elementBoundary.convertToEntity(),elementId,elementSmartspace,getUserRole(managerSmartspace,managerEmail));
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleException(ElementNotFoundException e) {
        String message = e.getMessage();
        if (message == null) {
            message = "message not found";
        }
        return new ErrorMessage(message);
    }

    private UserRole getUserRole(String smartspace, String email) {
        //If the user does not exist there should be an exception in the userService
        //and it shouldn't reach this point
        return userService.getUserByMailAndSmartSpace(email, smartspace).get().getRole();
        //return dbUser.isPresent() && dbUser.get().getRole().equals(userRole);
    }
}
