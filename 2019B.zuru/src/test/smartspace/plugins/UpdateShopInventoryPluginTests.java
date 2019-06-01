package smartspace.plugins;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import smartspace.dao.EnhancedActionDao;
import smartspace.dao.EnhancedElementDao;
import smartspace.dao.EnhancedUserDao;
import smartspace.data.*;
import smartspace.data.util.EntityFactory;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.profiles.active=default"})
public class UpdateShopInventoryPluginTests {
    private EnhancedUserDao userDao;
    private UserEntity playerUser;
    private EntityFactory factory;
    private String currentSmartspace;
    private UpdateShopInventoryPlugin updateShopInventoryPlugin;
    private EnhancedElementDao enhancedElementDao;
    private EnhancedActionDao enhancesActionDao;

    @Autowired
    public void setEnhancesActionDao(EnhancedActionDao enhancesActionDao) {
        this.enhancesActionDao = enhancesActionDao;
    }

    @Autowired
    public void setEnhancedElementDao(EnhancedElementDao enhancedElementDao) {
        this.enhancedElementDao = enhancedElementDao;
    }

    @Value("${spring.smartspace.name}")
    public void setCurrentSmartspace(String currentSmartspace) {
        this.currentSmartspace = currentSmartspace;
    }

    @Autowired
    public void setFactory(EntityFactory factory) {
        this.factory = factory;
    }

    @Autowired
    public void setUpdateShopInventoryPlugin(UpdateShopInventoryPlugin updateShopInventoryPlugin) {
        this.updateShopInventoryPlugin = updateShopInventoryPlugin;
    }

    @Autowired
    public void setUserDao(EnhancedUserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setElementDao(EnhancedElementDao elementDao) {
        this.enhancedElementDao = elementDao;
    }


    @Before
    public void setUp() {
        playerUser = userDao.create(factory.createNewUser("player@gmail.com", currentSmartspace, "Zur", "haha", UserRole.PLAYER, 0));
    }

    @After
    public void tearDown() {
        this.enhancedElementDao
                .deleteAll();
        this.userDao.deleteAll();
    }


    @Test
    public void updateInventory() {
        // GIVEN the database contains 1 user, and Shop one element with 2 parts
        HashMap<String, Object> moreAttributes = new HashMap<>();
        List<Part> parts = new ArrayList<>();
        parts.add(new Part("1", 100, "Battery"));
        parts.add(new Part("2", 100, "Wheel"));
        moreAttributes.put("Parts", parts);
        ElementEntity elementEntity1 = enhancedElementDao.create(factory.createNewElement("name", "Shop", new Location(5, 4), new Date(), "blabla@gmail.com", currentSmartspace, false, moreAttributes));

        //WHEN update amount of wheels to 200
        String partID = "1";
        HashMap<String, Object> requestMoreAttributes = new HashMap<>();
        List<Part> updatePartsInventoryList = new ArrayList<>();
        updatePartsInventoryList.add(new Part(partID, 200, "Wheel"));
        requestMoreAttributes.put("Parts",updatePartsInventoryList);
        ActionEntity ac1 = enhancesActionDao.create(factory.createNewAction(elementEntity1.getElementId(), elementEntity1.getElementSmartspace(), "UpdateInventory", new Date(), this.playerUser.getUserEmail(), this.currentSmartspace, requestMoreAttributes));
        ActionEntity result = this.updateShopInventoryPlugin.execute(ac1);


        //THEN the amount will change to 200
        assertThat(enhancesActionDao.readAll(50, 0)).usingElementComparatorOnFields("actionId").contains(result);
        ElementEntity entity = (ElementEntity) enhancedElementDao.readById(elementEntity1.getKey()).get();
        List<Object> curElementParts = (List<Object>) entity.getMoreAttributes().get("Parts");
        for (int i = 0; i < curElementParts.size(); i++) {
            LinkedHashMap<?, ?> cur = (LinkedHashMap<?, ?>) curElementParts.get(i);
            if(cur.get("name").equals("Wheel"))
                assertEquals(cur.get("amount"), 200);
        }
    }

    @Test
    public void updateEmptyInventory() {
        // GIVEN the database contains 1 user, and Shop one element with 0 parts
        HashMap<String, Object> moreAttributes = new HashMap<>();
        List<Part> parts = new ArrayList<>();
        parts.add(new Part("1", 100, "Battery"));
        parts.add(new Part("2", 100, "Wheel"));
        moreAttributes.put("Parts", parts);
        ElementEntity elementEntity1 = enhancedElementDao.create(factory.createNewElement("name", "Shop", new Location(5, 4), new Date(), "blabla@gmail.com", currentSmartspace, false, moreAttributes));

        //WHEN update amount of wheels to 200
        String partID = "1";
        HashMap<String, Object> requestMoreAttributes = new HashMap<>();
        List<Part> updatePartsInventoryList = new ArrayList<>();
        updatePartsInventoryList.add(new Part(partID, 200, "Wheel"));
        requestMoreAttributes.put("Parts",updatePartsInventoryList);
        ActionEntity ac1 = enhancesActionDao.create(factory.createNewAction(elementEntity1.getElementId(), elementEntity1.getElementSmartspace(), "UpdateInventory", new Date(), this.playerUser.getUserEmail(), this.currentSmartspace, requestMoreAttributes));
        ActionEntity result = this.updateShopInventoryPlugin.execute(ac1);


        //THEN the amount will change to 200
        assertThat(enhancesActionDao.readAll(50, 0)).usingElementComparatorOnFields("actionId").contains(result);
        ElementEntity entity = (ElementEntity) enhancedElementDao.readById(elementEntity1.getKey()).get();
        List<Object> curElementParts = (List<Object>) entity.getMoreAttributes().get("Parts");
        for (int i = 0; i < curElementParts.size(); i++) {
            LinkedHashMap<?, ?> cur = (LinkedHashMap<?, ?>) curElementParts.get(i);
            if(cur.get("name").equals("Wheel"))
                assertEquals(cur.get("amount"), 200);
        }
    }

    @Test
    public void updateNonOfPartInventory() {
        // GIVEN the database contains 1 user, and Shop one element with 1 part
        HashMap<String, Object> moreAttributes = new HashMap<>();
        List<Part> parts = new ArrayList<>();
        parts.add(new Part("2", 100, "Wheel"));
        moreAttributes.put("Parts", parts);
        ElementEntity elementEntity1 = enhancedElementDao.create(factory.createNewElement("name", "Shop", new Location(5, 4), new Date(), "blabla@gmail.com", currentSmartspace, false, moreAttributes));

        //WHEN update amount of Battery to 50
        String partID = "1";
        HashMap<String, Object> requestMoreAttributes = new HashMap<>();
        List<Part> updatePartsInventoryList = new ArrayList<>();
        updatePartsInventoryList.add(new Part(partID, 50, "Battery"));
        requestMoreAttributes.put("Parts",updatePartsInventoryList);
        ActionEntity ac1 = enhancesActionDao.create(factory.createNewAction(elementEntity1.getElementId(), elementEntity1.getElementSmartspace(), "UpdateInventory", new Date(), this.playerUser.getUserEmail(), this.currentSmartspace, requestMoreAttributes));
        ActionEntity result = this.updateShopInventoryPlugin.execute(ac1);


        //THEN the amount will change to 200
        assertThat(enhancesActionDao.readAll(50, 0)).usingElementComparatorOnFields("actionId").contains(result);
        ElementEntity entity = (ElementEntity) enhancedElementDao.readById(elementEntity1.getKey()).get();
        List<Object> curElementParts = (List<Object>) entity.getMoreAttributes().get("Parts");
        for (int i = 0; i < curElementParts.size(); i++) {
            LinkedHashMap<?, ?> cur = (LinkedHashMap<?, ?>) curElementParts.get(i);
            if(cur.get("name").equals("Battery"))
                assertEquals(cur.get("amount"), 50);
        }
    }

    @Test(expected = Throwable.class)
    public void UpdateInventoryOnBadElementType() {
        // GIVEN the database contains 3 users, and Not Shop element with 2 parts
        HashMap<String, Object> moreAttributes = new HashMap<>();
        List<Part> parts = new ArrayList<>();
        parts.add(new Part("1", 100, "Battery"));
        parts.add(new Part("2", 100, "Wheel"));
        moreAttributes.put("Parts", parts);
        ElementEntity elementEntity1 = enhancedElementDao.create(factory.createNewElement("name", "NotShop", new Location(5, 4), new Date(), null, null, false, moreAttributes));

        //WHEN we buy some ammount of those parts
        HashMap<String, Object> requestMoreAttributes = new HashMap<>();
        requestMoreAttributes.put("Part", new Part("1", 50, "Wheel"));
        ActionEntity ac1 = enhancesActionDao.create(factory.createNewAction(elementEntity1.getElementId(), elementEntity1.getElementSmartspace(), "BuyParts", new Date(), this.playerUser.getUserEmail(), this.currentSmartspace, requestMoreAttributes));
        ActionEntity result = this.updateShopInventoryPlugin.execute(ac1);

        //THEN exception is thrown.
    }

    @Test(expected = Throwable.class)
    public void UpdateInventoryOnBadActionType() {
        // GIVEN the database contains 3 users, and Not Shop element with 2 parts
        HashMap<String, Object> moreAttributes = new HashMap<>();
        List<Part> parts = new ArrayList<>();
        parts.add(new Part("1", 100, "Battery"));
        parts.add(new Part("2", 100, "Wheel"));
        moreAttributes.put("Parts", parts);
        ElementEntity elementEntity1 = enhancedElementDao.create(factory.createNewElement("name", "Shop", new Location(5, 4), new Date(), null, null, false, moreAttributes));

        //WHEN we buy some part that does not exists
        HashMap<String, Object> requestMoreAttributes = new HashMap<>();
        requestMoreAttributes.put("NotPartParameter", new Part("5", 50, "something"));
        ActionEntity ac1 = enhancesActionDao.create(factory.createNewAction(elementEntity1.getElementId(), elementEntity1.getElementSmartspace(), "BuyParts", new Date(), this.playerUser.getUserEmail(), this.currentSmartspace, requestMoreAttributes));
        ActionEntity result = this.updateShopInventoryPlugin.execute(ac1);

        //THEN exception is thrown.
    }

    @Test(expected = Throwable.class)
    public void updateInventoryWithInvalidAmount() {
        // GIVEN the database contains 1 user, and Shop one element with 2 parts
        HashMap<String, Object> moreAttributes = new HashMap<>();
        List<Part> parts = new ArrayList<>();
        parts.add(new Part("1", 100, "Battery"));
        parts.add(new Part("2", 100, "Wheel"));
        moreAttributes.put("Parts", parts);
        ElementEntity elementEntity1 = enhancedElementDao.create(factory.createNewElement("name", "Shop", new Location(5, 4), new Date(), "blabla@gmail.com", currentSmartspace, false, moreAttributes));

        //WHEN update amount of wheels to 200
        String partID = "1";
        HashMap<String, Object> requestMoreAttributes = new HashMap<>();
        List<Part> updatePartsInventoryList = new ArrayList<>();
        updatePartsInventoryList.add(new Part(partID, -30, "Wheel"));
        requestMoreAttributes.put("Parts",updatePartsInventoryList);
        ActionEntity ac1 = enhancesActionDao.create(factory.createNewAction(elementEntity1.getElementId(), elementEntity1.getElementSmartspace(), "UpdateInventory", new Date(), this.playerUser.getUserEmail(), this.currentSmartspace, requestMoreAttributes));
        this.updateShopInventoryPlugin.execute(ac1);


        //THEN exception will be thrown
    }

    @Test(expected = Throwable.class)
    public void updateEmptyShopInventoryWithInvalidAmount() {
        // GIVEN the database contains 1 user, and Shop one element with 2 parts
        HashMap<String, Object> moreAttributes = new HashMap<>();
        List<Part> parts = new ArrayList<>();
        ElementEntity elementEntity1 = enhancedElementDao.create(factory.createNewElement("name", "Shop", new Location(5, 4), new Date(), "blabla@gmail.com", currentSmartspace, false, moreAttributes));

        //WHEN update amount of wheels to 200
        String partID = "1";
        HashMap<String, Object> requestMoreAttributes = new HashMap<>();
        List<Part> updatePartsInventoryList = new ArrayList<>();
        updatePartsInventoryList.add(new Part(partID, -30, "Wheel"));
        requestMoreAttributes.put("Parts",updatePartsInventoryList);
        ActionEntity ac1 = enhancesActionDao.create(factory.createNewAction(elementEntity1.getElementId(), elementEntity1.getElementSmartspace(), "UpdateInventory", new Date(), this.playerUser.getUserEmail(), this.currentSmartspace, requestMoreAttributes));
        this.updateShopInventoryPlugin.execute(ac1);


        //THEN exception will be thrown
    }

}
