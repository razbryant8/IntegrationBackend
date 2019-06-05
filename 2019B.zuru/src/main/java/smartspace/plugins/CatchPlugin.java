package smartspace.plugins;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import smartspace.data.ActionEntity;
import smartspace.data.UserEntity;
import smartspace.logic.ElementService;
import smartspace.logic.UserService;

import java.util.Map;
import java.util.Optional;

@Component
public class CatchPlugin extends AbsVehicleStatusPlugin  {

    private UserService userService;

    @Autowired
    public CatchPlugin(ElementService elementService , UserService userService) {
        this.elementService = elementService;
        this.userService = userService;
        this.jackson = new ObjectMapper();
    }

    @Override
    public ActionEntity execute(ActionEntity actionEntity) {
        Map<String, Object> moreAttributes = actionEntity.getMoreAttributes();
        moreAttributes.put("VehicleStatus","RENTED");
        actionEntity.setMoreAttributes(moreAttributes);
        System.out.println("CatchPlugin - execute called");
        ActionEntity ae = super.execute(actionEntity);
        Optional<UserEntity> user = userService.getUserByMailAndSmartSpace(actionEntity.getPlayerEmail(), actionEntity.getPlayerSmartspace());
        user.ifPresent((u)->{
            long points = u.getPoints();
            u.setPoints(points+1);
        });
        return ae;
    }
}
