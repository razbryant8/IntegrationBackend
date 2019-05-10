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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import smartspace.dao.ElementNotFoundException;
import smartspace.dao.EnhancedElementDao;
import smartspace.dao.EnhancedUserDao;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.data.util.EntityFactory;
import smartspace.logic.ElementService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.profiles.active=default"})
public class ElementControllerTest {

    private String baseUrl;

    private String adminURL;

    private String relativeURL;

    private int port;

    private EnhancedElementDao elementDao;

    private EnhancedUserDao userDao;

    private ElementService elementService;

    private RestTemplate restTemplate;

    private UserEntity adminUser;

    private UserEntity managerUser;

    private UserEntity playerUser;

    private EntityFactory factory;

    private String currentSmartspace;

    @LocalServerPort
    //@Value("${server.port}")
    public void setPort(int port) {
        this.port = port;
    }

    @Value("${spring.application.name}")
    public void setCurrentSmartspace(String currentSmartspace) {
        this.currentSmartspace = currentSmartspace;
    }

    @Autowired
    public void setFactory(EntityFactory factory) {
        this.factory = factory;
    }

    @Autowired
    public void setElementService(ElementService elementService) {
        this.elementService = elementService;
    }

    @Autowired
    public void setUserDao(EnhancedUserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setElementDao(EnhancedElementDao elementDao) {
        this.elementDao = elementDao;
    }

    @PostConstruct
    public void init() {
        this.baseUrl = "http://localhost:" + port + "/smartspace/";
        this.adminURL = "admin/";
        this.relativeURL = "elements/";
        this.restTemplate = new RestTemplate();
    }

    @Before
    public void setUp() {
        adminUser = userDao.create(factory.createNewUser("zur@gmail.com", currentSmartspace, "Zur", "haha", UserRole.ADMIN, 0));
        managerUser = userDao.create(factory.createNewUser("manager@gmail.com", currentSmartspace, "Zur", "haha", UserRole.MANAGER, 0));
        playerUser = userDao.create(factory.createNewUser("player@gmail.com", currentSmartspace, "Zur", "haha", UserRole.PLAYER, 0));
    }

    @After
    public void tearDown() {
        this.elementDao
                .deleteAll();
        this.userDao.deleteAll();
        adminUser = null;
    }

    @Test(expected = Throwable.class)
    public void NotAdminImport() {
        // GIVEN the database contain one Admin user

        // WHEN someone that is not admin Import using REST API
        ArrayList<ElementBoundary> boundryArr = new ArrayList<ElementBoundary>();

        ElementBoundary newElementBoundary = new ElementBoundary();
        newElementBoundary.setCreated(new Date());
        newElementBoundary.setCreator(new UserKeyType("zur@gmail.com", "2019B.uu"));
        newElementBoundary.setElementProperties(new HashMap<>());
        newElementBoundary.setExpired(false);
        newElementBoundary.setElementType("scooter");
        newElementBoundary.setKey(new KeyType("5", "2019B.test"));
        newElementBoundary.setName("Name");
        newElementBoundary.setLatlng(new ElementLatLngType(35, 35));

        boundryArr.add(newElementBoundary);

        ElementBoundary[] actualResult = this.restTemplate
                .postForObject(
                        this.baseUrl + this.adminURL + this.relativeURL + "badSmartSpace/badEmail",
                        boundryArr,
                        ElementBoundary[].class);

        // THEN exception is thrown

    }

    @Test(expected = Throwable.class)
    public void NotAdminExport() {
        // GIVEN the database contain one Admin user

        // WHEN someone that is not admin Export using REST API
        int page = 0;
        int size = 5;
        ElementBoundary[] result =
                this.restTemplate
                        .getForObject(
                                this.baseUrl + this.adminURL + this.relativeURL + "badSmartSpace/BadEmail?size={size}&page={page}",
                                ElementBoundary[].class,
                                size, page);

        // THEN exception is thrown
    }

    @Test(expected = Throwable.class)
    public void importFromCurrentSmartSpace() {
        // GIVEN the database contain one Admin user

        // WHEN someone that is Import new elemeent that is from the current Smartspace using REST API
        ArrayList<ElementBoundary> boundryArr = new ArrayList<ElementBoundary>();

        ElementBoundary newElementBoundary = new ElementBoundary();
        newElementBoundary.setCreated(new Date());
        newElementBoundary.setCreator(new UserKeyType("zur@gmail.com", currentSmartspace));
        newElementBoundary.setElementProperties(new HashMap<>());
        newElementBoundary.setExpired(false);
        newElementBoundary.setElementType("scooter");
        newElementBoundary.setKey(new KeyType("5", currentSmartspace));
        newElementBoundary.setName("Name");
        newElementBoundary.setLatlng(new ElementLatLngType(35, 35));

        boundryArr.add(newElementBoundary);

        ElementBoundary[] actualResult = this.restTemplate
                .postForObject(
                        this.baseUrl + this.adminURL + this.relativeURL + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail(),
                        boundryArr,
                        ElementBoundary[].class);

        // THEN Exception is thrown
    }

    @Test
    public void validOneElementImport() {
        // GIVEN the database contain one Admin user

        // WHEN someone that is Import using REST API
        ArrayList<ElementBoundary> boundryArr = new ArrayList<ElementBoundary>();

        ElementBoundary newElementBoundary = new ElementBoundary();
        newElementBoundary.setCreated(new Date());
        newElementBoundary.setCreator(new UserKeyType("zur@gmail.com", "2019B.uu"));
        newElementBoundary.setElementProperties(new HashMap<>());
        newElementBoundary.setExpired(false);
        newElementBoundary.setElementType("scooter");
        newElementBoundary.setKey(new KeyType("5", "2019B.test"));
        newElementBoundary.setName("Name");
        newElementBoundary.setLatlng(new ElementLatLngType(35, 35));

        boundryArr.add(newElementBoundary);

        ElementBoundary[] actualResult = this.restTemplate
                .postForObject(
                        this.baseUrl + this.adminURL + this.relativeURL + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail(),
                        boundryArr.toArray(),
                        ElementBoundary[].class);

        // THEN the database contain those one elements
        List<ElementEntity> actualInDB = this.elementDao.readAll();
        assertThat(actualInDB).hasSize(1);
        assertThat(actualInDB).usingElementComparatorOnFields("key").contains(actualResult[0].convertToEntity());
    }

    @Test
    public void validMultiplElementImport() {
        // GIVEN the database contain one Admin user

        // WHEN someone that is Import two elements using REST
        ArrayList<ElementBoundary> boundryArr = new ArrayList<ElementBoundary>();

        ElementBoundary newElementBoundary = new ElementBoundary();
        newElementBoundary.setCreated(new Date());
        newElementBoundary.setCreator(new UserKeyType("zur@gmail.com", "2019B.uu"));
        newElementBoundary.setElementProperties(new HashMap<>());
        newElementBoundary.setExpired(false);
        newElementBoundary.setElementType("scooter");
        newElementBoundary.setKey(new KeyType("5", "2019B.test"));
        newElementBoundary.setName("Name");
        newElementBoundary.setLatlng(new ElementLatLngType(35, 35));

        ElementBoundary newElementBoundary2 = new ElementBoundary();
        newElementBoundary2.setCreated(new Date());
        newElementBoundary2.setCreator(new UserKeyType("zur@gmail.com", "2019B.uu"));
        newElementBoundary2.setElementProperties(new HashMap<>());
        newElementBoundary2.setExpired(false);
        newElementBoundary2.setElementType("scooter");
        newElementBoundary2.setKey(new KeyType("6", "2019B.test"));
        newElementBoundary2.setName("Name");
        newElementBoundary2.setLatlng(new ElementLatLngType(35, 35));

        boundryArr.add(newElementBoundary);
        boundryArr.add(newElementBoundary2);

        ElementBoundary[] actualResult1 = this.restTemplate
                .postForObject(
                        this.baseUrl + this.adminURL + this.relativeURL + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail(),
                        boundryArr,
                        ElementBoundary[].class);

        // THEN the database contain those two elements
        List<ElementEntity> actualInDB = this.elementDao.readAll();
        assertThat(actualInDB).hasSize(2);
        assertThat(actualInDB).usingElementComparatorOnFields("key").contains(actualResult1[0].convertToEntity());
        assertThat(actualInDB).usingElementComparatorOnFields("key").contains(actualResult1[1].convertToEntity());

    }

    @Test
    public void testExportUsingPagination() throws InterruptedException {
        // GIVEN the database contain one Admin user and two elements
        ElementEntity elementEntity = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", "2019b.zuru", false, new HashMap<>()));
        Thread.sleep(100);
        ElementEntity elementEntity2 = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", "2019b.zuru", false, new HashMap<>()));
        Thread.sleep(100);
        ElementEntity elementEntity3 = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", "2019b.zuru", false, new HashMap<>()));
        Thread.sleep(100);
        ElementEntity elementEntity4 = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", "2019b.zuru", false, new HashMap<>()));
        Thread.sleep(100);
        ElementEntity elementEntity5 = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", "2019b.zuru", false, new HashMap<>()));
        Thread.sleep(100);
        ElementEntity elementEntity6 = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", "2019b.zuru", false, new HashMap<>()));

        // WHEN I get all messages using page 1 and size 3
        int page = 1;
        int size = 3;
        ElementBoundary[] result =
                this.restTemplate
                        .getForObject(
                                this.baseUrl + this.adminURL + this.relativeURL + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail() + "?size={size}&page={page}",
                                ElementBoundary[].class,
                                size, page);

        // THEN the result contains 3 messages of the messages inserted to the database
        assertThat(result).hasSize(3);
        assertThat(result).usingElementComparatorOnFields("key").contains(new ElementBoundary(elementEntity4));
        assertThat(result).usingElementComparatorOnFields("key").contains(new ElementBoundary(elementEntity5));
        assertThat(result).usingElementComparatorOnFields("key").contains(new ElementBoundary(elementEntity6));
    }

    @Test(expected = Throwable.class)
    public void testCreateUserAsAdmin() {
        // GIVEN the database contain one Admin user, one Manager User and one Player


        // WHEN I create element As Admin
        ArrayList<ElementBoundary> boundryArr = new ArrayList<ElementBoundary>();

        ElementBoundary newElementBoundary = new ElementBoundary();
        newElementBoundary.setCreated(new Date());
        newElementBoundary.setCreator(new UserKeyType("zur@gmail.com", currentSmartspace));
        newElementBoundary.setElementProperties(new HashMap<>());
        newElementBoundary.setExpired(false);
        newElementBoundary.setElementType("scooter");
        newElementBoundary.setKey(new KeyType("5", currentSmartspace));
        newElementBoundary.setName("Name");
        newElementBoundary.setLatlng(new ElementLatLngType(35, 35));


        ElementBoundary actualResult = this.restTemplate
                .postForObject(
                        this.baseUrl + this.relativeURL + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail(),
                        newElementBoundary,
                        ElementBoundary.class);

        // THEN the exception is thrown
    }

    @Test(expected = Throwable.class)
    public void testCreateUserAsPlayer() {
        // GIVEN the database contain one Admin user, one Manager User and one Player

        // WHEN I create element As Player
        ElementBoundary newElementBoundary = new ElementBoundary();
        newElementBoundary.setCreated(new Date());
        newElementBoundary.setCreator(new UserKeyType("zur@gmail.com", currentSmartspace));
        newElementBoundary.setElementProperties(new HashMap<>());
        newElementBoundary.setExpired(false);
        newElementBoundary.setElementType("scooter");
        newElementBoundary.setKey(new KeyType("5", currentSmartspace));
        newElementBoundary.setName("Name");
        newElementBoundary.setLatlng(new ElementLatLngType(35, 35));


        ElementBoundary actualResult = this.restTemplate
                .postForObject(
                        this.baseUrl + this.relativeURL + playerUser.getUserSmartspace() + "/" + playerUser.getUserEmail(),
                        newElementBoundary,
                        ElementBoundary.class);

        // THEN the exception is thrown
    }

    @Test
    public void testCreateElement() {
        // GIVEN the database contain one Admin user, one Manager User and one Player

        // WHEN I create element As Manager

        ElementBoundary newElementBoundary = new ElementBoundary();
        newElementBoundary.setCreated(new Date());
        newElementBoundary.setCreator(new UserKeyType(managerUser.getUserEmail(), managerUser.getUserSmartspace()));
        newElementBoundary.setElementProperties(new HashMap<>());
        newElementBoundary.setExpired(false);
        newElementBoundary.setElementType("scooter");
        newElementBoundary.setKey(new KeyType("-1", currentSmartspace));
        newElementBoundary.setName("Name");
        newElementBoundary.setLatlng(new ElementLatLngType(35, 35));


        ElementBoundary actualResult = this.restTemplate
                .postForObject(
                        //this.baseUrl +"http://localhost:" + port +
                        this.baseUrl + this.relativeURL + managerUser.getUserSmartspace() + "/" + managerUser.getUserEmail(),
                        newElementBoundary,
                        ElementBoundary.class);
        // THEN the element is created with another id
        assertNotEquals(actualResult.getKey(), newElementBoundary.getKey());
    }


    @Test(expected = Throwable.class)
    public void testGetElementAsAdmin() {
        // GIVEN the database contain one Admin user, one Manager User and one Player and one Element
        UserEntity managerUser = userDao.create(factory.createNewUser("manager@gmail.com", currentSmartspace, "Zur", "haha", UserRole.MANAGER, 0));
        UserEntity playerUser = userDao.create(factory.createNewUser("player@gmail.com", currentSmartspace, "Zur", "haha", UserRole.PLAYER, 0));
        ElementEntity elementEntity = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, false, new HashMap<>()));

        // WHEN I Get element As Admin
        ElementBoundary result =
                this.restTemplate
                        .getForObject(
                                this.baseUrl + this.relativeURL + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail() + "/" + elementEntity.getElementSmartspace() + "/" + elementEntity.getElementId(),
                                ElementBoundary.class);

        // THEN the exception is thrown
    }

    @Test
    public void testGetNotExpiredElementAsPlayer() {
        // GIVEN the database contain one Admin user, one Manager User and one Player and one Element
        ElementEntity elementEntity = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, false, new HashMap<>()));

        // WHEN I Get element As Player
        ElementBoundary result =
                this.restTemplate
                        .getForObject(
                                this.baseUrl + this.relativeURL + playerUser.getUserSmartspace() + "/" + playerUser.getUserEmail() + "/" + elementEntity.getElementSmartspace() + "/" + elementEntity.getElementId(),
                                ElementBoundary.class);

        // THEN the exception is thrown
    }

    @Test(expected = HttpClientErrorException.NotFound.class)
    public void testGetExpiredElementAsPlayer() {
        // GIVEN the database contain one Admin user, one Manager User and one Player and one Element
        ElementEntity elementEntity = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, true, new HashMap<>()));

        // WHEN I Get element As Player
        ElementBoundary result =
                this.restTemplate
                        .getForObject(
                                this.baseUrl + this.relativeURL + playerUser.getUserSmartspace() + "/" + playerUser.getUserEmail() + "/" + elementEntity.getElementSmartspace() + "/" + elementEntity.getElementId(),
                                ElementBoundary.class);

        // THEN the exception is thrown
    }

    @Test(expected = HttpClientErrorException.NotFound.class)
    public void testNotExistsElementsmartspace() {
        // GIVEN the database contain one Admin user, one Manager User and one Player and one Element
        ElementEntity elementEntity = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, false, new HashMap<>()));

        // WHEN I Get element As Player
        ElementBoundary result =
                this.restTemplate
                        .getForObject(
                                this.baseUrl + this.relativeURL + managerUser.getUserSmartspace() + "/" + managerUser.getUserEmail() + "/testSpace/" + elementEntity.getElementId(),
                                ElementBoundary.class);

        // THEN the exception is thrown
    }

    @Test(expected = HttpClientErrorException.NotFound.class)
    public void testNotExistsElementID() {
        // GIVEN the database contain one Admin user, one Manager User and one Player and one Element
        ElementEntity elementEntity = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, false, new HashMap<>()));

        // WHEN I Get element As Player
        ElementBoundary result =
                this.restTemplate
                        .getForObject(
                                this.baseUrl + this.relativeURL + managerUser.getUserSmartspace() + "/" + managerUser.getUserEmail() + "/" + elementEntity.getElementSmartspace() + "/-1",
                                ElementBoundary.class);

        // THEN the exception is thrown
    }

    @Test
    public void testGetNotExpiredElementAsManager() {
        // GIVEN the database contain one Admin user, one Manager User and one Player and one Element
        ElementEntity elementEntity = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, false, new HashMap<>()));

        // WHEN I Get element As Manager
        ElementBoundary result =
                this.restTemplate
                        .getForObject(
                                this.baseUrl + this.relativeURL + managerUser.getUserSmartspace() + "/" + managerUser.getUserEmail() + "/" + elementEntity.getElementSmartspace() + "/" + elementEntity.getElementId(),
                                ElementBoundary.class);

        // THEN the element is retrieved
        assertEquals(elementEntity.getKey(), result.convertToEntity().getKey());
    }

    @Test
    public void testGetExpiredElementAsManager() {
        // GIVEN the database contain one Admin user, one Manager User and one Player and one Element
        ElementEntity elementEntity = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, true, new HashMap<>()));

        // WHEN I Get element As Manager
        ElementBoundary result =
                this.restTemplate
                        .getForObject(
                                this.baseUrl + this.relativeURL + managerUser.getUserSmartspace() + "/" + managerUser.getUserEmail() + "/" + elementEntity.getElementSmartspace() + "/" + elementEntity.getElementId(),
                                ElementBoundary.class);

        // THEN the element is retrieved
        assertEquals(elementEntity.getKey(), result.convertToEntity().getKey());
    }

    @Test(expected = Throwable.class)
    public void testGetElementDetailsAsAdmin() {
        // GIVEN the database contain one Admin user, one Manager User and one Player and one Element
        ElementEntity elementEntity = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, false, new HashMap<>()));

        // WHEN I Get Details as admin
        int size = 5;
        int page = 0;
        String search = Search.NAME.toString();
        String value = "test";
        ElementBoundary[] result =
                this.restTemplate
                        .getForObject(
                                this.baseUrl + this.relativeURL + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail() + "?search={search}&value={value}&size={size}&page={page}",
                                ElementBoundary[].class,
                                search, value, size, page);

        // THEN the exception is thrown
    }

    @Test
    public void testGetNotExistsElementDetailsByName() {
        // GIVEN the database contain one Admin user, one Manager User and one Player and one Element
        ElementEntity elementEntity = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, false, new HashMap<>()));

        // WHEN I Get Details of not exists name as manager
        int size = 5;
        int page = 0;
        String search = Search.NAME.toString();
        String value = "NotExists";
        ElementBoundary[] result =
                this.restTemplate
                        .getForObject(
                                this.baseUrl + this.relativeURL + managerUser.getUserSmartspace() + "/" + managerUser.getUserEmail() + "?search={search}&value={value}&size={size}&page={page}",
                                ElementBoundary[].class,
                                search, value, size, page);

        // THEN we recieve an empty array
        assertEquals(result.length, 0);
    }

    @Test
    public void testGetNotExistsElementDetailsByType() {
        // GIVEN the database contain one Admin user, one Manager User and one Player and one Element
        ElementEntity elementEntity = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, false, new HashMap<>()));

        // WHEN I Get Details details of not exists type as manager
        int size = 5;
        int page = 0;
        String search = Search.TYPE.toString();
        String value = "NotExists";
        ElementBoundary[] result =
                this.restTemplate
                        .getForObject(
                                this.baseUrl + this.relativeURL + managerUser.getUserSmartspace() + "/" + managerUser.getUserEmail() + "?search={search}&value={value}&size={size}&page={page}",
                                ElementBoundary[].class,
                                search, value, size, page);

        // THEN we recieve an empty array
        assertEquals(result.length, 0);
    }

    @Test
    public void testGetElementsAsManagerDetailsByName() {
        // GIVEN the database contain one Admin user, one Manager User and one Player and three Elements
        ElementEntity elementEntity1 = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, true, new HashMap<>()));
        ElementEntity elementEntity2 = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, false, new HashMap<>()));
        ElementEntity elementEntity3 = elementDao.create(factory.createNewElement("notHere", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, true, new HashMap<>()));

        // WHEN I Get Details by name as manager
        int size = 5;
        int page = 0;
        String search = Search.NAME.toString();
        String value = "name";
        ElementBoundary[] result =
                this.restTemplate
                        .getForObject(
                                this.baseUrl + this.relativeURL + managerUser.getUserSmartspace() + "/" + managerUser.getUserEmail() + "?search={search}&value={value}&size={size}&page={page}",
                                ElementBoundary[].class,
                                search, value, size, page);

        // THEN we recieve array of two elements with name name
        assertEquals(result.length, 2);
        assertThat(result).usingElementComparatorOnFields("key").contains(new ElementBoundary(elementEntity1));
        assertThat(result).usingElementComparatorOnFields("key").contains(new ElementBoundary(elementEntity2));
    }

    @Test
    public void testGetElementsAsManagerDetailsByType() {
        // GIVEN the database contain one Admin user, one Manager User and one Player and three Elements
        ElementEntity elementEntity1 = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, true, new HashMap<>()));
        ElementEntity elementEntity2 = elementDao.create(factory.createNewElement("name", "notHere", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, false, new HashMap<>()));
        ElementEntity elementEntity3 = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, false, new HashMap<>()));

        // WHEN I Get Details by type as manager
        int size = 5;
        int page = 0;
        String search = Search.TYPE.toString();
        String value = "type";
        ElementBoundary[] result =
                this.restTemplate
                        .getForObject(
                                this.baseUrl + this.relativeURL + managerUser.getUserSmartspace() + "/" + managerUser.getUserEmail() + "?search={search}&value={value}&size={size}&page={page}",
                                ElementBoundary[].class,
                                search, value, size, page);

        // THEN we recieve array of two elements with type name
        assertEquals(result.length, 2);
        assertThat(result).usingElementComparatorOnFields("key").contains(new ElementBoundary(elementEntity1));
        assertThat(result).usingElementComparatorOnFields("key").contains(new ElementBoundary(elementEntity3));
    }

    @Test
    public void testGetElementsAsManagerDetailsByTypeWithPagination() throws InterruptedException {
        // GIVEN the database contain one Admin user, one Manager User and one Player and three Elements
        ElementEntity elementEntity1 = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, true, new HashMap<>()));
        Thread.sleep(100);
        ElementEntity elementEntity2 = elementDao.create(factory.createNewElement("name", "notHere", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, false, new HashMap<>()));
        Thread.sleep(100);
        ElementEntity elementEntity3 = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, false, new HashMap<>()));
        Thread.sleep(100);
        ElementEntity elementEntity4 = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, true, new HashMap<>()));
        Thread.sleep(100);
        ElementEntity elementEntity5 = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, false, new HashMap<>()));
        Thread.sleep(100);

        // WHEN I Get Details by type as manager with page 0 size 2
        int size = 2;
        int page = 0;
        String search = Search.TYPE.toString();
        String value = "type";
        ElementBoundary[] result =
                this.restTemplate
                        .getForObject(
                                this.baseUrl + this.relativeURL + managerUser.getUserSmartspace() + "/" + managerUser.getUserEmail() + "?search={search}&value={value}&size={size}&page={page}",
                                ElementBoundary[].class,
                                search, value, size, page);

        // THEN we recieve array of two elements with type name
        assertEquals(result.length, 2);
        assertThat(result).usingElementComparatorOnFields("key").contains(new ElementBoundary(elementEntity1));
        assertThat(result).usingElementComparatorOnFields("key").contains(new ElementBoundary(elementEntity3));
    }

    @Test
    public void testGetElementsAsPlayerDetailsByName() {
        // GIVEN the database contain one Admin user, one Manager User and one Player and three Elements
        ElementEntity elementEntity1 = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, true, new HashMap<>()));
        ElementEntity elementEntity2 = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, false, new HashMap<>()));
        ElementEntity elementEntity3 = elementDao.create(factory.createNewElement("notHere", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, true, new HashMap<>()));

        // WHEN I Get Details by name as player
        int size = 5;
        int page = 0;
        String search = Search.NAME.toString();
        String value = "name";
        ElementBoundary[] result =
                this.restTemplate
                        .getForObject(
                                this.baseUrl + this.relativeURL + playerUser.getUserSmartspace() + "/" + playerUser.getUserEmail() + "?search={search}&value={value}&size={size}&page={page}",
                                ElementBoundary[].class,
                                search, value, size, page);

        // THEN we recieve array of one elements with name name -- only one not expired
        assertEquals(result.length, 1);
        assertThat(result).usingElementComparatorOnFields("key").contains(new ElementBoundary(elementEntity2));
    }

    @Test
    public void testGetElementsAsPlayerDetailsByType() {
        // GIVEN the database contain one Admin user, one Manager User and one Player and three Elements
        ElementEntity elementEntity1 = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, true, new HashMap<>()));
        ElementEntity elementEntity2 = elementDao.create(factory.createNewElement("name", "notHere", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, false, new HashMap<>()));
        ElementEntity elementEntity3 = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, false, new HashMap<>()));

        // WHEN I Get Details by type as player
        int size = 5;
        int page = 0;
        String search = Search.TYPE.toString();
        String value = "type";
        ElementBoundary[] result =
                this.restTemplate
                        .getForObject(
                                this.baseUrl + this.relativeURL + playerUser.getUserSmartspace() + "/" + playerUser.getUserEmail() + "?search={search}&value={value}&size={size}&page={page}",
                                ElementBoundary[].class,
                                search, value, size, page);

        // THEN we recieve array of one elements with type name -- one is expired
        assertEquals(result.length, 1);
        assertThat(result).usingElementComparatorOnFields("key").contains(new ElementBoundary(elementEntity3));
    }

    @Test
    public void testGetElementsAsPlayerDetailsByTypeWithPagination() throws InterruptedException {
        // GIVEN the database contain one Admin user, one Manager User and one Player and three Elements
        ElementEntity elementEntity1 = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, false, new HashMap<>()));
        Thread.sleep(100);
        ElementEntity elementEntity2 = elementDao.create(factory.createNewElement("name", "notHere", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, false, new HashMap<>()));
        Thread.sleep(100);
        ElementEntity elementEntity3 = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, true, new HashMap<>()));
        Thread.sleep(100);
        ElementEntity elementEntity4 = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, true, new HashMap<>()));
        Thread.sleep(100);
        ElementEntity elementEntity5 = elementDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", currentSmartspace, true, new HashMap<>()));
        Thread.sleep(100);

        // WHEN I Get Details by type as player with page 0 size 2
        int size = 2;
        int page = 0;
        String search = Search.TYPE.toString();
        String value = "type";
        ElementBoundary[] result =
                this.restTemplate
                        .getForObject(
                                this.baseUrl + this.relativeURL + playerUser.getUserSmartspace() + "/" + playerUser.getUserEmail() + "?search={search}&value={value}&size={size}&page={page}",
                                ElementBoundary[].class,
                                search, value, size, page);

        // THEN we recieve array of one elements with type name -- only one not expired
        assertEquals(result.length, 1);
        assertThat(result).usingElementComparatorOnFields("key").contains(new ElementBoundary(elementEntity1));
    }


}