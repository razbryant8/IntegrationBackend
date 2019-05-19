package smartspace.plugins;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import smartspace.data.*;
import smartspace.logic.ElementService;
import smartspace.logic.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class BuyPartsPlugin implements PluginCommand {

    private ObjectMapper jackson;
    private ElementService elementService;

    @Autowired
    public BuyPartsPlugin(ElementService elementService) {
        this.jackson = new ObjectMapper();
        this.elementService = elementService;
    }

    @Override
    public ActionEntity execute(ActionEntity actionEntity) {

        try {
            BuyPartsInput buyPartsInput = this.jackson
                    .readValue(this.jackson.writeValueAsString(actionEntity.getMoreAttributes()),
                            BuyPartsInput.class);
            ElementEntity retrievedElementEntity = this.elementService.getById
                    (actionEntity.getElementId(), actionEntity.getElementSmartspace(), UserRole.PLAYER);
            Part thePart = getPart(retrievedElementEntity, buyPartsInput.getPartId());
            if (thePart.getAmount() >= buyPartsInput.getAmount()) {
                Part newPart = new Part(thePart.getPartId(), thePart.getAmount() - buyPartsInput.getAmount());
                ElementEntity modifiedElementEntity = modifyParts(retrievedElementEntity, thePart, newPart);
                this.elementService.update(modifiedElementEntity, actionEntity.getElementId(),
                        actionEntity.getElementSmartspace());
            }
            return actionEntity;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private ElementEntity modifyParts(ElementEntity retrievedElementEntity, Part oldPart, Part newPart) {
        Map<String, Object> theMap = retrievedElementEntity.getMoreAttributes();
        List<Part> theParts = ((List<Part>) theMap.get("Parts"));
        theParts.remove(oldPart);
        theParts.add(newPart);
        theMap.put("Parts", theParts);
        retrievedElementEntity.setMoreAttributes(theMap);
        return retrievedElementEntity;
    }

    private Part getPart(ElementEntity elementEntity, String partId) {
        if (elementEntity.getMoreAttributes().containsKey("Parts")) {
            List<Part> parts = ((List<Part>) this.elementService.getById(
                    elementEntity.getElementId(), elementEntity.getElementSmartspace(),
                    UserRole.PLAYER).getMoreAttributes().get("Parts"));
            for (int i = 0; i < parts.size(); i++) {
                if (parts.get(i).getPartId().equals(partId))
                    return parts.get(i);
            }
        }
        throw new RuntimeException("Unable to perform action. No such part with id "
                + partId);
    }

}
