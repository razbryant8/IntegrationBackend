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
import smartspace.dao.EnhancedUserDao;
import smartspace.data.ActionEntity;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.data.util.EntityFactory;
import smartspace.logic.ActionService;

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

    private int port;

    private EnhancedActionDao enhancedActionDao;

    private EnhancedUserDao enhancedUserDao;

    private EntityFactory entityFactory;

    private ActionService actionService;

    private RestTemplate restTemplate;

    private UserEntity adminUser;

    private String mySmartspace;

    @Autowired
    public void setActionService(ActionService actionService) {
        this.actionService = actionService;
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
    public void init(){
        this.baseUrl = "http://localhost:" + port + "/smartspace/admin/actions/";
        this.restTemplate = new RestTemplate();
    }

    @Before
    public void setUp(){
        adminUser = enhancedUserDao.create(entityFactory.createNewUser("omri@gmail.com",mySmartspace,"omri",":D", UserRole.ADMIN,10));
    }

    @After
    public void tearDown(){
        enhancedActionDao.deleteAll();
        enhancedUserDao.deleteAll();
    }

    @Test
    public void testCreateAction(){
        // GIVEN nothing

        // WHEN a new action is posted to the server
        ActionBoundary[] actionBoundaries = new ActionBoundary[1];
        actionBoundaries[0] = new ActionBoundary();
        actionBoundaries[0].setActionKey(new KeyType("1","2019B.othersmartspace"));
        actionBoundaries[0].setActionProperties(new HashMap<>());
        actionBoundaries[0].setCreated(new Date());
        actionBoundaries[0].setElement(new KeyType("1","2019B.element"));
        actionBoundaries[0].setType("actionType");
        actionBoundaries[0].setPlayer(new UserKeyType("omri@gmail.com","2019B.other"));



        ActionBoundary [] result = this.restTemplate.postForObject(this.baseUrl + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail(),
                actionBoundaries,
                ActionBoundary[].class);

        List<ActionEntity> actionEntities = this.enhancedActionDao.readAll();

        assertEquals("Data base contains more than one action in it" , actionEntities.size(),1);

        // THEN the database contains the new action
        assertThat(actionEntities.get(0)).extracting("actionId").containsExactly(result[0].convertToEntity().getActionId());

    }

    @Test
    public void testCreateActions(){
        // GIVEN nothing

        // WHEN a new action is posted to the server
        ActionBoundary[] actionBoundaries = new ActionBoundary[2];
        actionBoundaries[0] = new ActionBoundary();
        actionBoundaries[0].setActionKey(new KeyType("1","2019B.othersmartspace"));
        actionBoundaries[0].setActionProperties(new HashMap<>());
        actionBoundaries[0].setCreated(new Date());
        actionBoundaries[0].setElement(new KeyType("1","2019B.element"));
        actionBoundaries[0].setType("actionType");
        actionBoundaries[0].setPlayer(new UserKeyType("omri@gmail.com","2019B.other"));

        actionBoundaries[1] = new ActionBoundary();
        actionBoundaries[1].setActionKey(new KeyType("2","2019B.othersmartspace"));
        actionBoundaries[1].setActionProperties(new HashMap<>());
        actionBoundaries[1].setCreated(new Date());
        actionBoundaries[1].setElement(new KeyType("2","2019B.element"));
        actionBoundaries[1].setType("actionType");
        actionBoundaries[1].setPlayer(new UserKeyType("mark@gmail.com","2019B.other"));



        ActionBoundary [] result = this.restTemplate.postForObject(this.baseUrl + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail(),
                actionBoundaries,
                ActionBoundary[].class);

        List<ActionEntity> actionEntities = this.enhancedActionDao.readAll();

        assertEquals("Data base contains more than one action in it" , actionEntities.size(),2);


        // THEN the database contains the new actions
        assertThat(actionEntities.get(0)).extracting("actionId").containsExactly(result[0].convertToEntity().getActionId());
        assertThat(actionEntities.get(1)).extracting("actionId").containsExactly(result[1].convertToEntity().getActionId());

    }

    @Test(expected = Throwable.class)
    public void testImportActionFromCurrentSmartSpace(){
        // GIVEN the database contain one Admin user

        // WHEN we try to import a action from the same smartspace of our project.
        ActionBoundary[] actionBoundaries = new ActionBoundary[1];
        actionBoundaries[0] = new ActionBoundary();
        actionBoundaries[0].setActionKey(new KeyType("1","2019B.othersmartspace"));
        actionBoundaries[0].setActionProperties(new HashMap<>());
        actionBoundaries[0].setCreated(new Date());
        actionBoundaries[0].setElement(new KeyType("1",mySmartspace));
        actionBoundaries[0].setType("actionType");
        actionBoundaries[0].setPlayer(new UserKeyType("omri@gmail.com","2019B.other"));

        ActionBoundary [] result = this.restTemplate.postForObject(this.baseUrl + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail(),
                actionBoundaries,
                ActionBoundary[].class);

        // THEN Exception is thrown
    }



    @Test
    public void testExportUsingPagination() {
        // GIVEN the database contain one Admin user and two elements
        ActionEntity actionEntity = enhancedActionDao.create(entityFactory.createNewAction("1","smart","type",new Date(),"email","space",new HashMap<>()));
        ActionEntity actionEntity1 = enhancedActionDao.create(entityFactory.createNewAction("2","smart","type",new Date(),"email","space",new HashMap<>()));
        ActionEntity actionEntity2 = enhancedActionDao.create(entityFactory.createNewAction("3","smart","type",new Date(),"email","space",new HashMap<>()));
        ActionEntity actionEntity3 = enhancedActionDao.create(entityFactory.createNewAction("4","smart","type",new Date(),"email","space",new HashMap<>()));
        ActionEntity actionEntity4 = enhancedActionDao.create(entityFactory.createNewAction("5","smart","type",new Date(),"email","space",new HashMap<>()));


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
        // GIVEN the database contain one Admin user and two elements
        ActionEntity actionEntity = enhancedActionDao.create(entityFactory.createNewAction("1","smart","type",new Date(),"email","space",new HashMap<>()));
        ActionEntity actionEntity1 = enhancedActionDao.create(entityFactory.createNewAction("2","smart","type",new Date(),"email","space",new HashMap<>()));
        ActionEntity actionEntity2 = enhancedActionDao.create(entityFactory.createNewAction("3","smart","type",new Date(),"email","space",new HashMap<>()));
        ActionEntity actionEntity3 = enhancedActionDao.create(entityFactory.createNewAction("4","smart","type",new Date(),"email","space",new HashMap<>()));
        ActionEntity actionEntity4 = enhancedActionDao.create(entityFactory.createNewAction("5","smart","type",new Date(),"email","space",new HashMap<>()));


        // WHEN I get all actions
        ActionBoundary[] result =
                this.restTemplate
                        .getForObject(
                                this.baseUrl + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail(),
                                ActionBoundary[].class);

        // THEN the result contains 3 messages of the messages inserted to the database
        assertThat(result).hasSize(5);
        assertThat(result).usingElementComparatorOnFields("actionKey").contains(new ActionBoundary(actionEntity));
        assertThat(result).usingElementComparatorOnFields("actionKey").contains(new ActionBoundary(actionEntity1));
        assertThat(result).usingElementComparatorOnFields("actionKey").contains(new ActionBoundary(actionEntity2));
        assertThat(result).usingElementComparatorOnFields("actionKey").contains(new ActionBoundary(actionEntity3));
        assertThat(result).usingElementComparatorOnFields("actionKey").contains(new ActionBoundary(actionEntity4));
    }



}