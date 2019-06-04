package smartspace.plugins;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import smartspace.data.ActionEntity;
import smartspace.logic.ElementService;

import java.util.HashMap;
import java.util.Map;

@Component
public class ReleasePlugin extends AbsVehicleStatusPlugin {

    @Autowired
    public ReleasePlugin(ElementService elementService) {
        this.elementService = elementService;
        this.jackson = new ObjectMapper();
    }

    @Override
    public ActionEntity execute(ActionEntity actionEntity) {
        Map<String, Object> moreAttributes = actionEntity.getMoreAttributes();
        moreAttributes.put("VehicleStatus","FREE");
        actionEntity.setMoreAttributes(moreAttributes);
        System.out.println("ReleasePlugin - execute called");
        return super.execute(actionEntity);
    }
}
