package smartspace.layout;

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
import smartspace.data.ActionEntity;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.data.util.EntityFactory;

import static org.assertj.core.api.Assertions.assertThat;

import javax.annotation.PostConstruct;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.profiles.active=default"})
public class ActionControllerTest {


    private String baseUrl;

    private String elementBaseUrl;

    private int port;

    private EnhancedActionDao enhancedActionDao;

    private EnhancedUserDao enhancedUserDao;

    private EnhancedElementDao enhancedElementDao;

    private EntityFactory entityFactory;

    private RestTemplate restTemplate;

    private UserEntity adminUser;

    private String mySmartspace;


    @Autowired
    public void setEnhancedElementDao(EnhancedElementDao enhancedElementDao) {
        this.enhancedElementDao = enhancedElementDao;
    }

    @Autowired
    public void setEnhancedActionDao(EnhancedActionDao enhancedActionDao) {
        this.enhancedActionDao = enhancedActionDao;
    }

    @Autowired
    public void setEnhancedUserDao(EnhancedUserDao enhancedUserDao) {
        this.enhancedUserDao = enhancedUserDao;
    }

    @Autowired
    public void setEntityFactory(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    @LocalServerPort
    public void setPort(int port) {
        this.port = port;
    }

    @Value("${spring.application.name}")
    public void setMySmartspace(String mySmartspace) {
        this.mySmartspace = mySmartspace;
    }

    @PostConstruct
    public void init() {
        this.baseUrl = "http://localhost:" + port + "/smartspace/admin/actions/";
        this.elementBaseUrl = "http://localhost:" + port + "/smartspace/admin/elements/";
        this.restTemplate = new RestTemplate();
    }

    @Before
    public void setUp() {
        adminUser = enhancedUserDao.create(entityFactory.createNewUser("omri@gmail.com", mySmartspace, "omri", ":D", UserRole.ADMIN, 10));
    }

    @After
    public void tearDown() {
        enhancedActionDao.deleteAll();
        enhancedUserDao.deleteAll();
        enhancedElementDao.deleteAll();
    }


    @Test(expected = Throwable.class)
    public void testIllegalImportPermissions() {

        // GIVEN the database contain one Admin user

        // WHEN a user with illegal permissions try to Import using REST API
        ActionBoundary[] actionBoundaries = new ActionBoundary[1];
        actionBoundaries[0] = new ActionBoundary();
        actionBoundaries[0].setActionKey(new KeyType("1", "2019B.othersmartspace"));
        actionBoundaries[0].setProperties(new HashMap<>());
        actionBoundaries[0].setCreated(new Date());
        actionBoundaries[0].setElement(new KeyType("1", "2019B.element"));
        actionBoundaries[0].setType("actionType");
        actionBoundaries[0].setPlayer(new UserKeyType("omri@gmail.com", "2019B.other"));

        this.restTemplate.postForObject(this.baseUrl + "badSmartSpace/badEmail",
                actionBoundaries,
                ActionBoundary[].class);

        // THEN exception is thrown
    }

    @Test(expected = Throwable.class)
    public void testIllegalExportPermissions() {

        // GIVEN the database contain one Admin user

        // WHEN a user with illegal permissions try to Export using REST API
        int page = 0;
        int size = 3;
        this.restTemplate.getForObject(
                this.baseUrl + "badSmartSpace/BadEmail?size={size}&page={page}",
                ActionBoundary[].class,
                size, page);

        // THEN exception is thrown
    }

    @Test(expected = Exception.class)
    public void testIllegalActionCreationCausedByMissingElement() {
        // GIVEN nothing

        // WHEN a new action is posted to the server but the matching element for that action is missing.
        ActionBoundary[] actionBoundaries = new ActionBoundary[1];
        actionBoundaries[0] = new ActionBoundary();
        actionBoundaries[0].setActionKey(new KeyType("1", "2019B.othersmartspace"));
        actionBoundaries[0].setProperties(new HashMap<>());
        actionBoundaries[0].setCreated(new Date());
        actionBoundaries[0].setElement(new KeyType("1", "2019B.element"));
        actionBoundaries[0].setType("actionType");
        actionBoundaries[0].setPlayer(new UserKeyType("omri@gmail.com", "2019B.other"));


        this.restTemplate.postForObject(
                this.baseUrl + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail(),
                actionBoundaries,
                ActionBoundary[].class);


        // THEN exception is thrown

    }

    @Test
    public void testCreateAction() {
        // GIVEN there's an element that the following action is preformed on

        ElementBoundary[] elementBoundaries = new ElementBoundary[1];
        elementBoundaries[0] = new ElementBoundary();
        elementBoundaries[0].setKey(new KeyType("1", "2019B.element"));
        elementBoundaries[0].setLatlng(new ElementLatLngType(35, 35));
        elementBoundaries[0].setName("Name");
        elementBoundaries[0].setElementType("scooter");
        elementBoundaries[0].setExpired(false);
        elementBoundaries[0].setElementProperties(new HashMap<>());
        elementBoundaries[0].setCreator(new UserKeyType("omri@gmail.com", "2019B.asdada"));
        elementBoundaries[0].setCreated(new Date());

        this.restTemplate.postForObject(
                this.elementBaseUrl + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail(),
                elementBoundaries,
                ElementBoundary[].class);


        // WHEN a new action is posted to the server
        ActionBoundary[] actionBoundaries = new ActionBoundary[1];
        actionBoundaries[0] = new ActionBoundary();
        actionBoundaries[0].setActionKey(new KeyType("1", "2019B.othersmartspace"));
        actionBoundaries[0].setProperties(new HashMap<>());
        actionBoundaries[0].setCreated(new Date());
        actionBoundaries[0].setElement(new KeyType("1", "2019B.element"));
        actionBoundaries[0].setType("actionType");
        actionBoundaries[0].setPlayer(new UserKeyType("omri@gmail.com", "2019B.other"));


        ActionBoundary[] result = this.restTemplate.postForObject(
                this.baseUrl + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail(),
                actionBoundaries,
                ActionBoundary[].class);

        List<ActionEntity> actionEntities = this.enhancedActionDao.readAll();

        assertEquals("Data base contains more than one action in it", actionEntities.size(), 1);

        // THEN the database contains the new action
        assert result != null;
        assertThat(actionEntities.get(0)).extracting("actionId").containsExactly(result[0].convertToEntity().getActionId());

    }



    @Test
    public void testCreateActions() {
        // GIVEN there are elements that the following actions are preformed on

        ElementBoundary[] elementBoundaries = new ElementBoundary[2];
        elementBoundaries[0] = new ElementBoundary();
        elementBoundaries[0].setKey(new KeyType("1", "2019B.element"));
        elementBoundaries[0].setLatlng(new ElementLatLngType(35, 35));
        elementBoundaries[0].setName("Name");
        elementBoundaries[0].setElementType("scooter");
        elementBoundaries[0].setExpired(false);
        elementBoundaries[0].setElementProperties(new HashMap<>());
        elementBoundaries[0].setCreator(new UserKeyType("omri@gmail.com", "2019B.asdada"));
        elementBoundaries[0].setCreated(new Date());

        elementBoundaries[1] = new ElementBoundary();
        elementBoundaries[1].setKey(new KeyType("2", "2019B.element"));
        elementBoundaries[1].setLatlng(new ElementLatLngType(35, 35));
        elementBoundaries[1].setName("Name");
        elementBoundaries[1].setElementType("scooter");
        elementBoundaries[1].setExpired(false);
        elementBoundaries[1].setElementProperties(new HashMap<>());
        elementBoundaries[1].setCreator(new UserKeyType("omri@gmail.com", "2019B.asdada"));
        elementBoundaries[1].setCreated(new Date());

        this.restTemplate.postForObject(
                this.elementBaseUrl + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail(),
                elementBoundaries,
                ElementBoundary[].class);

        // WHEN  new actions are posted to the server
        ActionBoundary[] actionBoundaries = new ActionBoundary[2];
        actionBoundaries[0] = new ActionBoundary();
        actionBoundaries[0].setActionKey(new KeyType("1", "2019B.othersmartspace"));
        actionBoundaries[0].setProperties(new HashMap<>());
        actionBoundaries[0].setCreated(new Date());
        actionBoundaries[0].setElement(new KeyType("1", "2019B.element"));
        actionBoundaries[0].setType("actionType");
        actionBoundaries[0].setPlayer(new UserKeyType("omri@gmail.com", "2019B.other"));

        actionBoundaries[1] = new ActionBoundary();
        actionBoundaries[1].setActionKey(new KeyType("2", "2019B.othersmartspace"));
        actionBoundaries[1].setProperties(new HashMap<>());
        actionBoundaries[1].setCreated(new Date());
        actionBoundaries[1].setElement(new KeyType("2", "2019B.element"));
        actionBoundaries[1].setType("actionType");
        actionBoundaries[1].setPlayer(new UserKeyType("mark@gmail.com", "2019B.other"));


        ActionBoundary[] result = this.restTemplate.postForObject(
                this.baseUrl + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail(),
                actionBoundaries,
                ActionBoundary[].class);

        List<ActionEntity> actionEntities = this.enhancedActionDao.readAll();

        assertEquals("Data base contains more than one action in it", actionEntities.size(), 2);


        // THEN the database contains the new actions
        assert result != null;
        assertThat(actionEntities.get(0)).extracting("actionId").containsExactly(result[0].convertToEntity().getActionId());
        assertThat(actionEntities.get(1)).extracting("actionId").containsExactly(result[1].convertToEntity().getActionId());

    }

    @Test(expected = Throwable.class)
    public void testImportActionFromCurrentSmartSpace() {
        // GIVEN the database contain one Admin user

        // WHEN we try to import a action from the same smartspace of our project.
        ActionBoundary[] actionBoundaries = new ActionBoundary[1];
        actionBoundaries[0] = new ActionBoundary();
        actionBoundaries[0].setActionKey(new KeyType("1", "2019B.othersmartspace"));
        actionBoundaries[0].setProperties(new HashMap<>());
        actionBoundaries[0].setCreated(new Date());
        actionBoundaries[0].setElement(new KeyType("1", mySmartspace));//Same smartspace
        actionBoundaries[0].setType("actionType");
        actionBoundaries[0].setPlayer(new UserKeyType("omri@gmail.com", "2019B.other"));

        this.restTemplate.postForObject(
                this.baseUrl + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail(),
                actionBoundaries,
                ActionBoundary[].class);

        // THEN Exception is thrown
    }


    @Test
    public void testExportUsingPagination() throws InterruptedException {
        // GIVEN the database contain one Admin user and five actions
        ActionEntity actionEntity = enhancedActionDao.create(entityFactory.createNewAction("1", "smart", "type", new Date(), "email", "space", new HashMap<>()));
        Thread.sleep(100);
        ActionEntity actionEntity1 = enhancedActionDao.create(entityFactory.createNewAction("2", "smart", "type", new Date(), "email", "space", new HashMap<>()));
        Thread.sleep(100);
        ActionEntity actionEntity2 = enhancedActionDao.create(entityFactory.createNewAction("3", "smart", "type", new Date(), "email", "space", new HashMap<>()));
        Thread.sleep(100);
        ActionEntity actionEntity3 = enhancedActionDao.create(entityFactory.createNewAction("4", "smart", "type", new Date(), "email", "space", new HashMap<>()));
        Thread.sleep(100);
        ActionEntity actionEntity4 = enhancedActionDao.create(entityFactory.createNewAction("5", "smart", "type", new Date(), "email", "space", new HashMap<>()));


        // WHEN I get all action using page 1 and size 3
        int page = 1;
        int size = 3;
        ActionBoundary[] result =
                this.restTemplate
                        .getForObject(
                                this.baseUrl + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail() + "?size={size}&page={page}",
                                ActionBoundary[].class,
                                size, page);

        // THEN the result contains 2 actions of the actions inserted to the database
        assertThat(result).hasSize(2);
        assertThat(result).usingElementComparatorOnFields("actionKey").contains(new ActionBoundary(actionEntity3));
        assertThat(result).usingElementComparatorOnFields("actionKey").contains(new ActionBoundary(actionEntity4));
    }

    @Test
    public void testExportingWithDefaultPagination() {
        // GIVEN the database contain one Admin user and five actions
        ActionEntity actionEntity = enhancedActionDao.create(entityFactory.createNewAction("1", "smart", "type", new Date(), "email", "space", new HashMap<>()));
        ActionEntity actionEntity1 = enhancedActionDao.create(entityFactory.createNewAction("2", "smart", "type", new Date(), "email", "space", new HashMap<>()));
        ActionEntity actionEntity2 = enhancedActionDao.create(entityFactory.createNewAction("3", "smart", "type", new Date(), "email", "space", new HashMap<>()));
        ActionEntity actionEntity3 = enhancedActionDao.create(entityFactory.createNewAction("4", "smart", "type", new Date(), "email", "space", new HashMap<>()));
        ActionEntity actionEntity4 = enhancedActionDao.create(entityFactory.createNewAction("5", "smart", "type", new Date(), "email", "space", new HashMap<>()));


        // WHEN I get all actions
        ActionBoundary[] result =
                this.restTemplate
                        .getForObject(
                                this.baseUrl + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail(),
                                ActionBoundary[].class);

        // THEN the result contains 5 actions of the actions inserted to the database
        assertThat(result).hasSize(5);
        assertThat(result).usingElementComparatorOnFields("actionKey").contains(new ActionBoundary(actionEntity));
        assertThat(result).usingElementComparatorOnFields("actionKey").contains(new ActionBoundary(actionEntity1));
        assertThat(result).usingElementComparatorOnFields("actionKey").contains(new ActionBoundary(actionEntity2));
        assertThat(result).usingElementComparatorOnFields("actionKey").contains(new ActionBoundary(actionEntity3));
        assertThat(result).usingElementComparatorOnFields("actionKey").contains(new ActionBoundary(actionEntity4));
    }


}