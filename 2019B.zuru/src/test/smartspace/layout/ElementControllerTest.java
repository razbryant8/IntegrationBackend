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

    private int port;

    private EnhancedElementDao elementDao;

    private EnhancedUserDao userDao;

    private ElementService elementService;

    private RestTemplate restTemplate;

    private UserEntity adminUser;

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
        this.baseUrl = "http://localhost:" + port + "/smartspace/admin/elements/";
        this.restTemplate = new RestTemplate();
    }

    @Before
    public void setUp() {
        adminUser = userDao.create(factory.createNewUser("zur@gmail.com", currentSmartspace, "Zur", "haha", UserRole.ADMIN, 0));
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
                        this.baseUrl + "badSmartSpace/badEmail",
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
                                this.baseUrl + "badSmartSpace/BadEmail?size={size}&page={page}",
                                ElementBoundary[].class,
                                size, page);

        // THEN exception is thrown
    }

    @Test(expected = Throwable.class)
    public void importFromCurrentSmartSpace(){
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
                        this.baseUrl + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail(),
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
                        this.baseUrl + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail(),
                        boundryArr.toArray(),
                        ElementBoundary[].class);

        // THEN the database contain those one elements
        List<ElementEntity> actualInDB = this.elementDao.readAll();
        assertThat(actualInDB).hasSize(1);
        assertThat(actualInDB).usingElementComparatorOnFields("key").contains(actualResult[0] .convertToEntity());
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
                        this.baseUrl + "/" + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail(),
                        boundryArr,
                        ElementBoundary[].class);

        // THEN the database contain those two elements
        List<ElementEntity> actualInDB = this.elementDao.readAll();
        assertThat(actualInDB).hasSize(2);
        assertThat(actualInDB).usingElementComparatorOnFields("key").contains(actualResult1[0] .convertToEntity());
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
                                this.baseUrl + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail() + "?size={size}&page={page}",
                                ElementBoundary[].class,
                                size, page);

        // THEN the result contains 3 messages of the messages inserted to the database
        assertThat(result).hasSize(3);
        assertThat(result).usingElementComparatorOnFields("key").contains(new ElementBoundary(elementEntity4));
        assertThat(result).usingElementComparatorOnFields("key").contains(new ElementBoundary(elementEntity5));
        assertThat(result).usingElementComparatorOnFields("key").contains(new ElementBoundary(elementEntity6));
    }

}