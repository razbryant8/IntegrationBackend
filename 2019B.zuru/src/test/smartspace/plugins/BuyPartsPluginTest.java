package smartspace.plugins;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import smartspace.dao.EnhancedActionDao;
import smartspace.dao.EnhancedElementDao;
import smartspace.dao.EnhancedUserDao;
import smartspace.data.*;
import smartspace.data.util.EntityFactory;
import smartspace.logic.ElementService;

import javax.annotation.PostConstruct;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.profiles.active=default"})
public class BuyPartsPluginTest {

    private EnhancedUserDao userDao;

    private UserEntity playerUser;

    private EntityFactory factory;

    private String currentSmartspace;

    private BuyPartsPlugin partsPlugin;

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
    public void setPartsPlugin(BuyPartsPlugin partsPlugin) {
        this.partsPlugin = partsPlugin;
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
    public void BuyParts() {
        // GIVEN the database contains 3 users, and Shop one element with 2 parts
        HashMap<String, Object> moreAttributes = new HashMap<>();
        List<Part> parts = new ArrayList<>();
        parts.add(new Part("1", 100, "Battery"));
        parts.add(new Part("2", 100, "Wheel"));
        moreAttributes.put("Parts", parts);
        ElementEntity elementEntity1 = enhancedElementDao.create(factory.createNewElement("name", "Shop", new Location(5, 4), new Date(), "blabla@gmail.com", currentSmartspace, false, moreAttributes));

        //WHEN we buy some ammount of those parts
        String partID = "1";
        HashMap<String, Object> requestMoreAttributes = new HashMap<>();
        requestMoreAttributes.put("Part", new Part(partID, 50, "Wheel"));
        ActionEntity ac1 = enhancesActionDao.create(factory.createNewAction(elementEntity1.getElementId(), elementEntity1.getElementSmartspace(), "BuyParts", new Date(), this.playerUser.getUserEmail(), this.currentSmartspace, requestMoreAttributes));
        ActionEntity result = this.partsPlugin.execute(ac1);

        //THEN the action is done and we get back the action details.
        assertThat(enhancesActionDao.readAll(50, 0)).usingElementComparatorOnFields("actionId").contains(result);
        ElementEntity entity = (ElementEntity) enhancedElementDao.readById(elementEntity1.getKey()).get();
        List<Object> curElementParts = (List<Object>) entity.getMoreAttributes().get("Parts");
        for (int i = 0; i < curElementParts.size(); i++) {
            LinkedHashMap<?, ?> cur = (LinkedHashMap<?, ?>) curElementParts.get(i);
            if (cur.get("partId").equals(partID)) {
                assertEquals(cur.get("amount"), 50);
            }

        }

    }

    @Test(expected = Throwable.class)
    public void BuyPartsOnBadElementType() {
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
        ActionEntity result = this.partsPlugin.execute(ac1);

        //THEN exception is thrown.
    }

    @Test(expected = Throwable.class)
    public void BuyPartsOfNotExistsPart() {
        // GIVEN the database contains 3 users, and Not Shop element with 2 parts
        HashMap<String, Object> moreAttributes = new HashMap<>();
        List<Part> parts = new ArrayList<>();
        parts.add(new Part("1", 100, "Battery"));
        parts.add(new Part("2", 100, "Wheel"));
        moreAttributes.put("Parts", parts);
        ElementEntity elementEntity1 = enhancedElementDao.create(factory.createNewElement("name", "Shop", new Location(5, 4), new Date(), null, null, false, moreAttributes));

        //WHEN we buy some part that does not exists
        HashMap<String, Object> requestMoreAttributes = new HashMap<>();
        requestMoreAttributes.put("Part", new Part("5", 50, "something"));
        ActionEntity ac1 = enhancesActionDao.create(factory.createNewAction(elementEntity1.getElementId(), elementEntity1.getElementSmartspace(), "BuyParts", new Date(), this.playerUser.getUserEmail(), this.currentSmartspace, requestMoreAttributes));
        ActionEntity result = this.partsPlugin.execute(ac1);

        //THEN exception is thrown.
    }

    @Test(expected = Throwable.class)
    public void BuyPartsWithoutPartParameter() {
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
        ActionEntity result = this.partsPlugin.execute(ac1);

        //THEN exception is thrown.
    }

    @Test(expected = Throwable.class)
    public void BuyPartsWithMorePartsThenInTheShop() {
        // GIVEN the database contains 3 users, and Not Shop element with 2 parts
        HashMap<String, Object> moreAttributes = new HashMap<>();
        List<Part> parts = new ArrayList<>();
        parts.add(new Part("1", 100, "Battery"));
        parts.add(new Part("2", 100, "Wheel"));
        moreAttributes.put("Parts", parts);
        ElementEntity elementEntity1 = enhancedElementDao.create(factory.createNewElement("name", "Shop", new Location(5, 4), new Date(), null, null, false, moreAttributes));

        //WHEN we buy some part that does not exists
        HashMap<String, Object> requestMoreAttributes = new HashMap<>();
        requestMoreAttributes.put("NotPartParameter", new Part("1", 105, "something"));
        ActionEntity ac1 = enhancesActionDao.create(factory.createNewAction(elementEntity1.getElementId(), elementEntity1.getElementSmartspace(), "BuyParts", new Date(), this.playerUser.getUserEmail(), this.currentSmartspace, requestMoreAttributes));
        ActionEntity result = this.partsPlugin.execute(ac1);

        //THEN exception is thrown.
    }

    @Test(expected = Throwable.class)
    public void BuyPartsOfElementThatDeosNotContainsPartsParameter() {
        // GIVEN the database contains 3 users, and Shop one element with 2 parts
        ElementEntity elementEntity1 = enhancedElementDao.create(factory.createNewElement("name", "Shop", new Location(5, 4), new Date(), null, null, false, new HashMap<>()));
        List<Part> parts = new ArrayList<>();
        parts.add(new Part("1", 100, "Battery"));
        parts.add(new Part("2", 100, "Wheel"));
        //moreAttributes.put("Parts", parts);));

        //WHEN we buy some ammount of those parts
        HashMap<String, Object> requestMoreAttributes = new HashMap<>();
        requestMoreAttributes.put("Part", new Part("1", 50, "Wheel"));
        ActionEntity ac1 = enhancesActionDao.create(factory.createNewAction(elementEntity1.getElementId(), elementEntity1.getElementSmartspace(), "BuyParts", new Date(), this.playerUser.getUserEmail(), this.currentSmartspace, requestMoreAttributes));
        ActionEntity result = this.partsPlugin.execute(ac1);

        //THEN the action is done and we get back the action details.
        assertThat(enhancesActionDao.readAll(50, 0)).usingElementComparatorOnFields("actionId").contains(ac1);
        ElementEntity entity = (ElementEntity) enhancedElementDao.readById(elementEntity1.getKey()).get();
        List<Part> curElementParts = (List<Part>) entity.getMoreAttributes().get("Parts");
        assertEquals(curElementParts.get(0).getAmount(), 50);
    }

}