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
            ElementEntity retrievedElementEntity = this.elementService.getById
                    (actionEntity.getElementId(),actionEntity.getElementSmartspace(), UserRole.PLAYER);
            if(retrievedElementEntity.getType().equals("Scooter")) {
                if (actionEntity.getMoreAttributes().containsKey("VehicleStatus")) {
                    ReportVehicleStatusInput input = this.jackson
                            .readValue(this.jackson.writeValueAsString(
                                    actionEntity.getMoreAttributes().get(("VehicleStatus"))), ReportVehicleStatusInput.class);
                    if(retrievedElementEntity.getMoreAttributes().containsKey("VehicleStatus")) {
                        ElementEntity modifiedElementEntity = setVehicleStatus(retrievedElementEntity, input);
                        this.elementService.update(modifiedElementEntity, modifiedElementEntity.getElementId(),
                                modifiedElementEntity.getElementSmartspace());
                        return actionEntity;
                    }
                    else
                        throw new RuntimeException("Element does not contains VehicleStatus Parameter, Update first");
                }
                else
                    throw new RuntimeException(("Action Does not Contains VehicleStatus Parameter"));
            }
            else
                throw new RuntimeException("Cant update status on elemenet that is not Scooter");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private ElementEntity setVehicleStatus(ElementEntity elementEntity, ReportVehicleStatusInput input) {
        String value = input.getVehicleStatus();
        try
        {
            VehicleStatus status = VehicleStatus.valueOf(value);
            HashMap<String, Object> moreAttributes = new HashMap<>();
            moreAttributes.put("VehicleStatus",status);
            elementEntity.setMoreAttributes(moreAttributes);
            return elementEntity;
        }
        catch (Exception e){
            throw new RuntimeException("The Status is not valid");
        }
    }
}
