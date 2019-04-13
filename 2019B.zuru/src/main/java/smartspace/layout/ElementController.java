package smartspace.layout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import smartspace.logic.ElementService;

import java.util.stream.Collectors;

@RestController
public class ElementController {

    private ElementService elementService;

    @Autowired
    public ElementController(ElementService elementService) {
        this.elementService = elementService;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/messagedemo",//didn't change that because didn't know how to retrieve the necessary values
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ElementBoundary[] getAll(
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
        return this.elementService
                .getAll(size, page)
                .stream()
                .map(ElementBoundary::new)
                .collect(Collectors.toList())
                .toArray(new ElementBoundary[0]);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            path = "/messagedemo",//same as above
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ElementBoundary store(
            @RequestBody ElementBoundary elementBoundary) {
        return
                new ElementBoundary(
                        this.elementService
                                .store(elementBoundary
                                        .convertToEntity()));
    }
}
