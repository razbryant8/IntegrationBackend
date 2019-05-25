package smartspace.plugins;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import smartspace.data.*;
import smartspace.layout.ElementBoundary;
import smartspace.logic.ElementService;
import smartspace.logic.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            ElementEntity retrievedElementEntity = this.elementService.getById
                    (actionEntity.getElementId(), actionEntity.getElementSmartspace(), UserRole.PLAYER);
            if(retrievedElementEntity.getType().equals("Shop")) {
                if (actionEntity.getMoreAttributes().containsKey("Part")) {
                    BuyPartsInput buyPartsInput = this.jackson
                            .readValue(this.jackson.writeValueAsString(actionEntity.getMoreAttributes().get("Part")),
                                    BuyPartsInput.class);

                    if(retrievedElementEntity.getMoreAttributes().containsKey("Parts")) {
                        Part thePart = getPart(retrievedElementEntity, buyPartsInput.getPartId());
                        if (thePart.getAmount() >= buyPartsInput.getAmount()) {
                            Part newPart = new Part(thePart.getPartId(), thePart.getAmount() - buyPartsInput.getAmount(), thePart.getName());
                            ElementEntity modifiedElementEntity = modifyParts(retrievedElementEntity, thePart, newPart);
                            this.elementService.update(modifiedElementEntity, actionEntity.getElementId(),
                                    actionEntity.getElementSmartspace());
                        }
                        return actionEntity;
                    }
                    else {
                        throw new RuntimeException("The Element Does Not Contains Parts, Therefore Patch him first");
                    }
                } else {
                    throw new RuntimeException("Cant Locate Part parameter to update");
                }
            }
            else{
                throw new RuntimeException("Cant buy part on element that is not Shop");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private ElementEntity modifyParts(ElementEntity retrievedElementEntity, Part oldPart, Part newPart) {
        Map<String, Object> theMap = retrievedElementEntity.getMoreAttributes();
        ArrayList<Object> partsObj = (ArrayList<Object>) retrievedElementEntity.getMoreAttributes().get("Parts");
        for (int i = 0; i < partsObj.size(); i++) {
            LinkedHashMap<?,?> cur = (LinkedHashMap<?, ?>) partsObj.get(i);
            if (cur.get("partId").equals(oldPart.getPartId())) {
                partsObj.remove(i);
                break;
            }

        }
        partsObj.add(newPart);
        theMap.put("Parts", partsObj);
        retrievedElementEntity.setMoreAttributes(theMap);
        return retrievedElementEntity;
    }

    private Part getPart(ElementEntity elementEntity, String partId) {
            ArrayList<Object> partsObj = (ArrayList<Object>) elementEntity.getMoreAttributes().get("Parts");
            for (int i = 0; i < partsObj.size(); i++) {
                LinkedHashMap<?,?> cur = (LinkedHashMap<?, ?>) partsObj.get(i);
                if (cur.get("partId").equals(partId))
                    return new Part(cur.get("partId").toString(),Integer.parseInt(cur.get("amount").toString()),cur.get("name").toString());
            }
        throw new RuntimeException("Unable to perform action. No such part with id "
                + partId);
        }


}
