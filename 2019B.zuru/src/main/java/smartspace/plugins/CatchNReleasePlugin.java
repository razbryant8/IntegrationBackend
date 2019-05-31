package smartspace.plugins;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import smartspace.data.ActionEntity;
import smartspace.logic.ElementService;

@Component
public class CatchNReleasePlugin extends AbsVehicleStatusPlugin {

    @Autowired
    public CatchNReleasePlugin(ElementService elementService) {
        this.elementService = elementService;
        this.jackson = new ObjectMapper();
    }

    @Override
    public ActionEntity execute(ActionEntity actionEntity) {
        System.out.println("CatchNReleasePlugin - execute called");
        return super.execute(actionEntity);
    }

}
