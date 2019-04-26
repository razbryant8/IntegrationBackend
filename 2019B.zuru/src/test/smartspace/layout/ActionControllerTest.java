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
import javax.swing.*;

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
        ActionBoundary actionBoundary = new ActionBoundary();
        actionBoundary.setActionKey(new KeyType("1","2019B.othersmartspace"));
        actionBoundary.setActionProperties(new HashMap<>());
        actionBoundary.setCreated(new Date());
        actionBoundary.setElement(new KeyType("1","2019B.element"));
        actionBoundary.setType("actionType");
        actionBoundary.setPlayer(new UserKeyType("omri@gmail.com","2019B.other"));

        ActionBoundary result = this.restTemplate.postForObject(this.baseUrl + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail(),
                actionBoundary,
                ActionBoundary.class);

        List<ActionEntity> actionEntities = this.enhancedActionDao.readAll();

        assertEquals("Data base contains more than one action in it" , actionEntities.size(),1);

        // THEN the database contains the new message
        assertThat(actionEntities.get(0)).extracting("actionkey").containsExactly(result.getActionKey());

    }



}