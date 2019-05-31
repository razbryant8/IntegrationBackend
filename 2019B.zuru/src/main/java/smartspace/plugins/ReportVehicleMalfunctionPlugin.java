package smartspace.plugins;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;
import smartspace.data.UserRole;
import smartspace.data.VehicleStatus;
import smartspace.logic.ElementService;

import java.util.HashMap;
import java.util.Map;

@Component
public class ReportVehicleMalfunctionPlugin extends AbsVehicleStatusPlugin {

    @Autowired
    public ReportVehicleMalfunctionPlugin(ElementService elementService) {
        this.elementService = elementService;
        this.jackson = new ObjectMapper();
    }

    @Override
    public ActionEntity execute(ActionEntity actionEntity) {
      System.out.println("ReportVehicleMalfunctionPlugin - execute called");
      return super.execute(actionEntity);
    }

}
