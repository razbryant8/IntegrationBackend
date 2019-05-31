package smartspace.plugins;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import smartspace.data.ActionEntity;
import smartspace.logic.ElementService;

@Component
public class ReportVehicleLowBatteryPlugin extends AbsVehicleStatusPlugin {

    @Autowired
    public ReportVehicleLowBatteryPlugin(ElementService elementService) {
        this.elementService = elementService;
        this.jackson = new ObjectMapper();
    }

    @Override
    public ActionEntity execute(ActionEntity actionEntity) {
      System.out.println("ReportVehicleLowBatteryPlugin - execute called");
      return super.execute(actionEntity);
    }

}
