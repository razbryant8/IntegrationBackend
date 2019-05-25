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
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.profiles.active=default"})
public class ReportVehicleStatusPluginTest {
    private EnhancedUserDao userDao;

    private UserEntity playerUser;

    private EntityFactory factory;

    private String currentSmartspace;

    private ReportVehicleStatusPlugin reportPlugin;

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
    public void setReportPlugin(ReportVehicleStatusPlugin reportPlugin) {
        this.reportPlugin = reportPlugin;
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
    public void SetStatus() {
        // GIVEN the database contains 3 users, and Scooter  element with with Free Status
        HashMap<String, Object> moreAttributes = new HashMap<>();
        moreAttributes.put("VehicleStatus", VehicleStatus.FREE);
        ElementEntity elementEntity1 = enhancedElementDao.create(factory.createNewElement("name", "Scooter", new Location(5, 4), new Date(), "blabla@gmail.com", currentSmartspace, false, moreAttributes));

        //WHEN we set status of the elemeent to rented
        HashMap<String, Object> requestMoreAttributes = new HashMap<>();
        requestMoreAttributes.put("VehicleStatus", VehicleStatus.RENTED);
        ActionEntity ac1 = enhancesActionDao.create(factory.createNewAction(elementEntity1.getElementId(), elementEntity1.getElementSmartspace(), "ReportVehicleStatus", new Date(), this.playerUser.getUserEmail(), this.currentSmartspace, requestMoreAttributes));
        ActionEntity result = this.reportPlugin.execute(ac1);

        //THEN the action is done and the status of the elemenet is set to RENT
        assertThat(enhancesActionDao.readAll(50, 0)).usingElementComparatorOnFields("actionId").contains(result);
        ElementEntity entity = (ElementEntity) enhancedElementDao.readById(elementEntity1.getKey()).get();
        String status = (String) entity.getMoreAttributes().get("VehicleStatus");
        assertEquals(VehicleStatus.RENTED.toString(), status);
    }

    @Test(expected = Throwable.class)
    public void SetBadStatus() {
        // GIVEN the database contains 3 users, and Scooter  element with with Free Status
        HashMap<String, Object> moreAttributes = new HashMap<>();
        moreAttributes.put("VehicleStatus", VehicleStatus.FREE);
        ElementEntity elementEntity1 = enhancedElementDao.create(factory.createNewElement("name", "Scooter", new Location(5, 4), new Date(), "blabla@gmail.com", currentSmartspace, false, moreAttributes));

        //WHEN we set status of the elemeent to BadStatus
        HashMap<String, Object> requestMoreAttributes = new HashMap<>();
        requestMoreAttributes.put("VehicleStatus", "BadStatus");
        ActionEntity ac1 = enhancesActionDao.create(factory.createNewAction(elementEntity1.getElementId(), elementEntity1.getElementSmartspace(), "ReportVehicleStatus", new Date(), this.playerUser.getUserEmail(), this.currentSmartspace, requestMoreAttributes));
        ActionEntity result = this.reportPlugin.execute(ac1);

        //THEN exception is thrown
    }

    @Test(expected = Throwable.class)
    public void SetStatusOfElementWithoutPrevStatus() {
        // GIVEN the database contains 3 users, and Scooter  element with with Free Status
        HashMap<String, Object> moreAttributes = new HashMap<>();
        //moreAttributes.put("VehicleStatus", VehicleStatus.FREE);
        ElementEntity elementEntity1 = enhancedElementDao.create(factory.createNewElement("name", "Scooter", new Location(5, 4), new Date(), "blabla@gmail.com", currentSmartspace, false, moreAttributes));

        //WHEN we set status of the elemeent to RENTED
        HashMap<String, Object> requestMoreAttributes = new HashMap<>();
        requestMoreAttributes.put("VehicleStatus", VehicleStatus.RENTED);
        ActionEntity ac1 = enhancesActionDao.create(factory.createNewAction(elementEntity1.getElementId(), elementEntity1.getElementSmartspace(), "ReportVehicleStatus", new Date(), this.playerUser.getUserEmail(), this.currentSmartspace, requestMoreAttributes));
        ActionEntity result = this.reportPlugin.execute(ac1);

        //THEN exception is thrown
    }

    @Test(expected = Throwable.class)
    public void SetStatusOfElementWithoutSendingVehicleStatusParameter() {
        // GIVEN the database contains 3 users, and Scooter  element with with Free Status
        HashMap<String, Object> moreAttributes = new HashMap<>();
        //moreAttributes.put("VehicleStatus", VehicleStatus.FREE);
        ElementEntity elementEntity1 = enhancedElementDao.create(factory.createNewElement("name", "Scooter", new Location(5, 4), new Date(), "blabla@gmail.com", currentSmartspace, false, moreAttributes));

        //WHEN we set status of the elemeent to RENTED but the VehicleStatus Parameter is missing
        HashMap<String, Object> requestMoreAttributes = new HashMap<>();
        requestMoreAttributes.put("BadStatusParam", VehicleStatus.RENTED);
        ActionEntity ac1 = enhancesActionDao.create(factory.createNewAction(elementEntity1.getElementId(), elementEntity1.getElementSmartspace(), "ReportVehicleStatus", new Date(), this.playerUser.getUserEmail(), this.currentSmartspace, requestMoreAttributes));
        ActionEntity result = this.reportPlugin.execute(ac1);

        //THEN exception is thrown
    }

    @Test(expected = Throwable.class)
    public void SetShopStatus() {
        // GIVEN the database contains 3 users, and Shop  element
        HashMap<String, Object> moreAttributes = new HashMap<>();
        //moreAttributes.put("VehicleStatus", VehicleStatus.FREE);
        ElementEntity elementEntity1 = enhancedElementDao.create(factory.createNewElement("name", "Shop", new Location(5, 4), new Date(), "blabla@gmail.com", currentSmartspace, false, moreAttributes));

        //WHEN we set status of the elemeent to RENTED 
        HashMap<String, Object> requestMoreAttributes = new HashMap<>();
        requestMoreAttributes.put("BadStatusParam", VehicleStatus.RENTED);
        ActionEntity ac1 = enhancesActionDao.create(factory.createNewAction(elementEntity1.getElementId(), elementEntity1.getElementSmartspace(), "ReportVehicleStatus", new Date(), this.playerUser.getUserEmail(), this.currentSmartspace, requestMoreAttributes));
        ActionEntity result = this.reportPlugin.execute(ac1);

        //THEN exception is thrown
    }

}