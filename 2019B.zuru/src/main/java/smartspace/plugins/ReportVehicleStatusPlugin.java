package smartspace.plugins;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;
import smartspace.data.UserRole;
import smartspace.logic.ElementService;

import java.util.Map;

@Component
public class ReportVehicleStatusPlugin implements PluginCommand{

    private ObjectMapper jackson;
    private ElementService elementService;

    @Autowired
    public ReportVehicleStatusPlugin(ElementService elementService) {
        this.elementService = elementService;
        this.jackson = new ObjectMapper();
    }

    @Override
    public ActionEntity execute(ActionEntity actionEntity) {
        try {
            ReportVehicleStatusInput input = this.jackson
                    .readValue(this.jackson.writeValueAsString(
                            actionEntity.getMoreAttributes()),ReportVehicleStatusInput.class);
            ElementEntity retrievedElementEntity = this.elementService.getById
                    (actionEntity.getElementId(),actionEntity.getElementSmartspace(), UserRole.PLAYER);
            ElementEntity modifiedElementEntity = setVehicleStatus (retrievedElementEntity, input);
            this.elementService.update(modifiedElementEntity,modifiedElementEntity.getElementId(),
                    modifiedElementEntity.getElementSmartspace());
            return actionEntity;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private ElementEntity setVehicleStatus(ElementEntity elementEntity, ReportVehicleStatusInput input) {
        Map<String,Object> theMap = elementEntity.getMoreAttributes();
        if (theMap.containsKey("VehicleStatus"))
            theMap.put("VehicleStatus", input);
        else
            throw new RuntimeException("Element " + elementEntity.getName() + " ID: "
                + elementEntity.getElementId() + " has no current status");
        elementEntity.setMoreAttributes(theMap);
        return elementEntity;
    }
}
