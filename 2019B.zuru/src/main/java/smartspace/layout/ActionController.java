package smartspace.layout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import smartspace.data.ActionEntity;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.logic.ActionService;
import smartspace.logic.UserService;

import java.util.Optional;


@RestController
public class ActionController {

    private ActionService actionService;
    private UserService userService;

    @Autowired
    public ActionController(ActionService actionService, UserService userService) {
        this.actionService = actionService;
        this.userService = userService;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/smartspace/admin/actions/{adminSmartspace}/{adminEmail}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ActionBoundary[] getAll(
            @PathVariable("adminSmartspace") String adminSmartspace,
            @PathVariable("adminEmail") String adminEmail,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
        if (validateAdmin(adminSmartspace, adminEmail)) {
            return this.actionService
                    .getAll(size, page)
                    .stream()
                    .map(ActionBoundary::new).toArray(ActionBoundary[]::new);
        } else
            throw new RuntimeException("Unauthorized operation");
    }


    @Transactional
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/smartspace/admin/actions/{adminSmartspace}/{adminEmail}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ActionBoundary[] store(
            @PathVariable("adminSmartspace") String adminSmartspace,
            @PathVariable("adminEmail") String adminEmail,
            @RequestBody ActionBoundary[] actionBoundary) {
        if (validateAdmin(adminSmartspace, adminEmail)) {
            ActionEntity[] convertedEntities = new ActionEntity[actionBoundary.length];
            for (int i = 0; i <actionBoundary.length ; i++) {
                convertedEntities[i] = actionBoundary[i].convertToEntity();
            }
            ActionEntity[] actionEntities = this.actionService.store(convertedEntities);
            ActionBoundary[] actionBoundaries = new ActionBoundary[actionEntities.length];
            for (int i = 0; i <actionBoundary.length ; i++) {
                actionBoundaries[i] =new ActionBoundary(actionEntities[i]);
            }
            return actionBoundaries;
        } else
            throw new RuntimeException("Unauthorized operation");
    }


    @Transactional
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/smartspace/actions",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ActionBoundary invoke(@RequestBody ActionBoundary actionBoundary) {
        UserKeyType player = actionBoundary.getPlayer();
        if (validatePlayer(player.getSmartspace(), player.getEmail())) {
            return new ActionBoundary(this.actionService.invoke(actionBoundary.convertToEntity()));
        }else
            throw new RuntimeException("Unauthorized operation");
    }

    private boolean validateAdmin(String adminSmartspace, String adminEmail) {
        Optional<UserEntity> dbUser = userService.getUserByMailAndSmartSpace(adminEmail, adminSmartspace);
        if (dbUser.isPresent() && dbUser.get().getRole().equals(UserRole.ADMIN))
            return true;
        return false;
    }

    private boolean validatePlayer(String playerSmartspace, String playerEmail) {
        Optional<UserEntity> dbUser = userService.getUserByMailAndSmartSpace(playerEmail, playerSmartspace);
        if (dbUser.isPresent() && dbUser.get().getRole().equals(UserRole.PLAYER))
            return true;
        return false;
    }
}
