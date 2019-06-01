package smartspace.plugins;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;
import smartspace.data.Part;
import smartspace.data.UserRole;
import smartspace.logic.ElementService;
import java.util.*;

@Component
public class UpdateShopInventoryPlugin implements PluginCommand {
    private ObjectMapper jackson;
    private ElementService elementService;

    @Autowired
    public UpdateShopInventoryPlugin(ElementService elementService) {
        this.jackson = new ObjectMapper();
        this.elementService = elementService;
    }


    @Override
    public ActionEntity execute(ActionEntity actionEntity) {
        ElementEntity retrievedElementEntity = this.elementService.getById
                (actionEntity.getElementId(), actionEntity.getElementSmartspace(), UserRole.PLAYER);
        if(retrievedElementEntity.getType().equals("Shop")) {
            if (actionEntity.getMoreAttributes().containsKey("Parts")) {
                if(retrievedElementEntity.getMoreAttributes().containsKey("Parts")) {

                    ArrayList<Object> shopInventoryObj = (ArrayList<Object>) retrievedElementEntity.getMoreAttributes().get("Parts");
                    ArrayList<Object> actionUpdateInventoryObj = (ArrayList<Object>) actionEntity.getMoreAttributes().get("Parts");


                    List<Part> shopInventory = convertObjToPartsList(shopInventoryObj);
                    List<Part> actionUpdateInventory = convertObjToPartsList(actionUpdateInventoryObj);


                    Part inventoryPart;
                    for (int i = 0; i < actionUpdateInventory.size(); i++) {

                        inventoryPart = actionUpdateInventory.get(i);
                        int partIndex = getPartIndexInList(shopInventory, inventoryPart.getName());
                        if(inventoryPart.getAmount() < 0)
                            throw new RuntimeException("invalid amount of parts");
                        if (partIndex != -1) { // part in shop's inventory
                            shopInventory.get(partIndex).setAmount(inventoryPart.getAmount());
                        } else { // part NOT in shop's inventory
                            shopInventory.add(inventoryPart);
                        }
                    }

                    retrievedElementEntity.getMoreAttributes().put("Parts", shopInventory);

                    this.elementService.update(retrievedElementEntity, retrievedElementEntity.getElementId(), retrievedElementEntity.getElementSmartspace());


                }else { // the shop have empty inventory
                    List<Part> shopInventory = new ArrayList<>();

                    ArrayList<Object> actionUpdateInventoryObj = (ArrayList<Object>) actionEntity.getMoreAttributes().get("Parts");
                    List<Part> actionUpdateInventory = convertObjToPartsList(actionUpdateInventoryObj);


                    for (int i = 0; i < actionUpdateInventory.size(); i++) {
                        if(actionUpdateInventory.get(i).getAmount() < 0)
                            throw new RuntimeException("invalid amount of parts");
                        shopInventory.add(actionUpdateInventory.get(i));
                    }


                    retrievedElementEntity.getMoreAttributes().put("Parts", shopInventory);

                    this.elementService.update(retrievedElementEntity, retrievedElementEntity.getElementId(), retrievedElementEntity.getElementSmartspace());

                }
                return actionEntity;
            }else throw new RuntimeException("Cant Locate Inventory parameter to update inventory");
        }else throw new RuntimeException("Cant update inventory on element that is not Shop");

    }

    private int getPartIndexInList(List<Part> parts, String partName){
        for(int i=0; i<parts.size();i++){
            if(parts.get(i).getName().equals(partName))
                return i;
        }
        return -1;
    }

    private List<Part> convertObjToPartsList(ArrayList<Object> partsObj) {
        List<Part> partsList = new ArrayList<>();
        for (int i = 0; i < partsObj.size(); i++) {
            LinkedHashMap<?,?> cur = (LinkedHashMap<?, ?>) partsObj.get(i);
            partsList.add(new Part(cur.get("partId").toString(),Integer.parseInt(cur.get("amount").toString()),cur.get("name").toString()));
        }
        return partsList;
    }
}
