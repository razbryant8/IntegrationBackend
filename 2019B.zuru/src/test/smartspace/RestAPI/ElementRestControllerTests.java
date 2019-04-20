package smartspace.RestAPI;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import smartspace.dao.EnhancedElementDao;
import smartspace.dao.EnhancedUserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.data.util.EntityFactory;
import smartspace.layout.ElementBoundary;
import smartspace.layout.ElementCreatorType;
import smartspace.layout.ElementKeyType;
import smartspace.layout.ElementLatLngType;
import smartspace.logic.ElementService;
import javax.annotation.PostConstruct;
import org.junit.After;

import java.util.Date;
import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties= {"spring.profiles.active=default"})
public class ElementRestControllerTests {
    private String baseUrl;

    private int port;

    private EnhancedElementDao elementDao;

    private EnhancedUserDao userDao;

    private ElementService elementService;

    private RestTemplate restTemplate;

    private UserEntity adminUser;

    private EntityFactory factory;

    @LocalServerPort
    public void setPort(int port) {
        this.port = port;
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
        this.baseUrl = "http://localhost:" + port + "/smartspace/admin/elements";
        this.restTemplate = new RestTemplate();
    }

    @Before
    public void setUp(){
        adminUser = userDao.create(factory.createNewUser("zur@gmail.com","2019B.zuru","Zur","haha", UserRole.ADMIN,0));
    }

    @After
    public void tearDown() {
        this.elementDao
                .deleteAll();
    }

    @Test(expected = Throwable.class)
    public void NotAdminImport(){
        // GIVEN the database contain one Admin user

        // WHEN someone that is not admin Import using REST API
        ElementBoundary newElementBoundary = new ElementBoundary();
        newElementBoundary.setCreated(new Date());
        newElementBoundary.setCreator(new ElementCreatorType("zur@gmail.com","2019B.uu"));
        newElementBoundary.setElementProperties(new HashMap<>());
        newElementBoundary.setExpired(false);
        newElementBoundary.setElementType("scooter");
        newElementBoundary.setKey(new ElementKeyType("5","2019B.test"));
        newElementBoundary.setName("Name");
        newElementBoundary.setLatlng(new ElementLatLngType(35,35));

        ElementBoundary actualResult = this.restTemplate
                .postForObject(
                        this.baseUrl+"/badSmartSpace/badEmail",
                        newElementBoundary,
                        ElementBoundary.class);

        // THEN exception is thrown

    }

    @Test(expected = Throwable.class)
    public void NotAdminExport(){
        // GIVEN the database contain one Admin user

        // WHEN someone that is not admin Export using REST API
        ElementBoundary newElementBoundary = new ElementBoundary();
        newElementBoundary.setCreated(new Date());
        newElementBoundary.setCreator(new ElementCreatorType("zur@gmail.com","2019B.uu"));
        newElementBoundary.setElementProperties(new HashMap<>());
        newElementBoundary.setExpired(false);
        newElementBoundary.setElementType("scooter");
        newElementBoundary.setKey(new ElementKeyType("5","2019B.test"));
        newElementBoundary.setName("Name");
        newElementBoundary.setLatlng(new ElementLatLngType(35,35));

        int page = 0;
        int size = 5;
        ElementBoundary[]result =
                this.restTemplate
                        .getForObject(
                                this.baseUrl+ "/badSmartSpace/BadEmail?size={size}&page={page}",
                                ElementBoundary[].class,
                                size, page);

        // THEN exception is thrown

    }
    @Test(expected = Throwable.class)
    public void importFromCurrentSmartSpace(){

    }

    @Test
    public void validOneElementImport(){

    }

    @Test
    public void validMultiplElementImport(){

    }
    @Test
    public void testExportUsingPagination(){
        
    }
}
