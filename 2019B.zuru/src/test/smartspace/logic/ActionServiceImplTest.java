package smartspace.logic;

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
import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.util.EntityFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"spring.profiles.active=default"})
public class ActionServiceImplTest {

    private EnhancedActionDao enhancedActionDao;

    private EnhancedElementDao enhancedElementDao;

    private EntityFactory factory;

    private ActionServiceImpl actionService;

    private String currentSmartSpace;

    @Autowired
    public void setEnhancedElementDao(EnhancedElementDao enhancedElementDao) {
        this.enhancedElementDao = enhancedElementDao;
    }

    @Value("${spring.smartspace.name}")
    public void setCurrentSmartSpace(String currentSmartSpace) {
        this.currentSmartSpace = currentSmartSpace;

    }

    @Autowired
    public void setActionService(ActionServiceImpl actionService) {
        this.actionService = actionService;
    }

    @Autowired
    public void setFactory(EntityFactory factory) {
        this.factory = factory;
    }

    @Autowired
    public void setEnhancedActionDao(EnhancedActionDao enhancedActionDao) {
        this.enhancedActionDao = enhancedActionDao;
    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
        enhancedActionDao.deleteAll();
    }

    @Test()
    public void checkGetAllInEmptyPage() {
        // GIVEN The database contains one element
        ActionEntity actionEntity = enhancedActionDao.create(factory.createNewAction("someID", "smartspace", null, null, null, null, null));

        // WHEN getAll elements in an empty page
        int size = 5;
        int page = 1;
        List<ActionEntity> actionEntities = actionService.getAll(size, page);

        // THEN the List is empty
        assertEquals(0, actionEntities.size());
    }

    @Test()
    public void checkGetAllInPageWithOneAction() {
        // GIVEN The database contains one element
        ActionEntity actionEntity = enhancedActionDao.create(factory.createNewAction("someID", "smartspace", null, null, null, null, null));

        // WHEN getAll elements in the first page
        int size = 5;
        int page = 0;
        List<ActionEntity> actionEntities = actionService.getAll(size, page);

        // THEN the List contains exactly one element
        assertEquals(1, actionEntities.size());
        assertThat(actionEntities).usingElementComparatorOnFields("key").contains(actionEntity);
    }

    @Test()
    public void checkGetAllInPageWithThreeActions() {
        // GIVEN The database contains one element
        ActionEntity actionEntity1 = enhancedActionDao.create(factory.createNewAction("someID1", "smartspace", null, new Date(), "mark@gmail.com", "smartspace.zur", new HashMap<>()));
        ActionEntity actionEntity2 = enhancedActionDao.create(factory.createNewAction("someID2", "smartspace", null, new Date(), "mark@gmail.com", "smartspace.zur", new HashMap<>()));
        ActionEntity actionEntity3 = enhancedActionDao.create(factory.createNewAction("someID3", "smartspace", null, new Date(), "mark@gmail.com", "smartspace.zur", new HashMap<>()));

        // WHEN getAll elements in an empty page
        int size = 5;
        int page = 0;
        List<ActionEntity> actionEntities = actionService.getAll(size, page);

        // THEN the List contains exactly the three created elements
        assertEquals(3, actionEntities.size());
        assertThat(actionEntities).usingElementComparatorOnFields("key").contains(actionEntity1);
        assertThat(actionEntities).usingElementComparatorOnFields("key").contains(actionEntity2);
        assertThat(actionEntities).usingElementComparatorOnFields("key").contains(actionEntity3);
    }

    @Test()
    public void checkStore() {
        // GIVEN Valid Element Entity exists on db
        ElementEntity newElement = factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", "test", false, new HashMap<>());
        newElement.setKey("5#" + "anotherTeam");
        enhancedElementDao.upsert(newElement);

        ActionEntity actionEntity = factory.createNewAction("5#anotherTeam", "anotherTeam", "someType", new Date(), "mark@gmail.com", "anotherTeam", new HashMap<>());
        actionEntity.setKey("1#" + "anotherTeam");

        // WHEN we store the entity using ActionService Logic
        actionService.store(actionEntity);

        // THEN the entity is stored
        List<ActionEntity> entities = this.enhancedActionDao.readAll();
        assertEquals(1, entities.size());
        assertThat(entities).usingElementComparatorOnFields("key").contains(actionEntity);
    }

    @Test(expected = Throwable.class)
    public void checkValidateIllegalSmartSpace() {
        // GIVEN Entity with the current Smartspace
        ActionEntity actionEntity = enhancedActionDao.create(factory.createNewAction("someID", this.currentSmartSpace, "someType", new Date(), "mark@gmail.com", "zuru.bubu", new HashMap<>()));
        actionEntity.setKey("1#" + this.currentSmartSpace);

        // WHEN we store the entity using ActionService Logic
        actionService.store(actionEntity);

        // THEN we expect Exception to be thrown
    }

    @Test(expected = Throwable.class)
    public void checkValidateIllegalMoreAttributes() {
        // GIVEN Entity with null more attributes
        ActionEntity actionEntity = enhancedActionDao.create(factory.createNewAction("someID", this.currentSmartSpace, "someType", new Date(), "mark@gmail.com", "zuru.bubu", null));
        actionEntity.setKey("1#" + this.currentSmartSpace);

        // WHEN we store the entity using ActionService Logic
        actionService.store(actionEntity);

        // THEN we expect Exception to be thrown
    }

    @Test(expected = Throwable.class)
    public void checkValidateIllegalEmail() {
        // GIVEN Entity with null Email
        ActionEntity actionEntity = enhancedActionDao.create(factory.createNewAction("someID", this.currentSmartSpace, "someType", new Date(), null, "zuru.bubu", new HashMap<>()));
        actionEntity.setKey("1#" + this.currentSmartSpace);

        // WHEN we store the entity using ActionService Logic
        actionService.store(actionEntity);

        // THEN we expect Exception to be thrown
    }

    @Test(expected = Throwable.class)
    public void checkValidateIllegalActionType() {
        // GIVEN Entity with null action type
        ActionEntity actionEntity = enhancedActionDao.create(factory.createNewAction("someID", this.currentSmartSpace, null, new Date(), "mark@gmail.com", "zuru.bubu", new HashMap<>()));
        actionEntity.setKey("1#" + this.currentSmartSpace);

        // WHEN we store the entity using ActionService Logic
        actionService.store(actionEntity);

        // THEN we expect Exception to be thrown
    }

    @Test(expected = Throwable.class)
    public void checkValidateIllegalPlayerSmartSpace() {
        // GIVEN Entity with null player smartSpace
        ActionEntity actionEntity = enhancedActionDao.create(factory.createNewAction("someID", this.currentSmartSpace, "someType", new Date(), "mark@gmail.com", null, new HashMap<>()));
        actionEntity.setKey("1#" + this.currentSmartSpace);

        // WHEN we store the entity using ActionService Logic
        actionService.store(actionEntity);

        // THEN we expect Exception to be thrown
    }

    @Test(expected = Throwable.class)
    public void checkValidateIllegalActionSmartSpace() {
        // GIVEN Entity with null action smartSpace
        ActionEntity actionEntity = enhancedActionDao.create(factory.createNewAction("someID", this.currentSmartSpace, "someType", new Date(), "mark@gmail.com", this.currentSmartSpace, new HashMap<>()));
        actionEntity.setKey("1#" + null);

        // WHEN we store the entity using ActionService Logic
        actionService.store(actionEntity);

        // THEN we expect Exception to be thrown
    }

    @Test(expected = Throwable.class)
    public void checkValidateIllegalElementSmartSpace() {
        // GIVEN Entity with null element smartSpace
        ActionEntity actionEntity = enhancedActionDao.create(factory.createNewAction("someID", null, "someType", new Date(), "mark@gmail.com", this.currentSmartSpace, new HashMap<>()));
        actionEntity.setKey("1#" + this.currentSmartSpace);

        // WHEN we store the entity using ActionService Logic
        actionService.store(actionEntity);

        // THEN we expect Exception to be thrown
    }

    @Test(expected = Throwable.class)
    public void checkValidateIllegalActionID() {
        // GIVEN Entity with null action ID
        ActionEntity actionEntity = enhancedActionDao.create(factory.createNewAction("someID", this.currentSmartSpace, "someType", new Date(), "mark@gmail.com", this.currentSmartSpace, new HashMap<>()));
        actionEntity.setKey(null + this.currentSmartSpace);

        // WHEN we store the entity using ActionService Logic
        actionService.store(actionEntity);

        // THEN we expect Exception to be thrown
    }

    @Test(expected = Throwable.class)
    public void checkValidateIllegalElementID() {
        // GIVEN Entity with null Element ID
        ActionEntity actionEntity = enhancedActionDao.create(factory.createNewAction(null, this.currentSmartSpace, "someType", new Date(), "mark@gmail.com", this.currentSmartSpace, new HashMap<>()));
        actionEntity.setKey("1#" + this.currentSmartSpace);

        // WHEN we store the entity using ActionService Logic
        actionService.store(actionEntity);

        // THEN we expect Exception to be thrown
    }

    @Test()
    public void checkInvoke() {
        // GIVEN Valid Element Entity exists on db
        ElementEntity newElement = factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", "test", false, new HashMap<>());
        newElement.setKey("5#" + "anotherTeam");
        enhancedElementDao.upsert(newElement);

        ActionEntity actionEntity = factory.createNewAction("5#anotherTeam", "anotherTeam", "someType", new Date(), "mark@gmail.com", "anotherTeam", new HashMap<>());

        // WHEN we invoke the entity using ActionService Logic
        actionService.invoke(actionEntity);

        // THEN the entity is created with a generated action key
        List<ActionEntity> entities = this.enhancedActionDao.readAll();
        assertEquals(1, entities.size());
        assertEquals("1", entities.get(0).getActionId());
    }


    @Test(expected = Throwable.class)
    public void checkIllegalInvoke() {
        // GIVEN Valid Element Entity exists on db
        ElementEntity newElement = factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", "test", false, new HashMap<>());
        newElement.setKey("5#" + "anotherTeam");
        enhancedElementDao.upsert(newElement);

        ActionEntity actionEntity = factory.createNewAction("4#anotherTeam", "anotherTeam", "someType", new Date(), "mark@gmail.com", "anotherTeam", new HashMap<>());

        // WHEN we invoke an entity with element id that is not found in db using ActionService Logic
        actionService.invoke(actionEntity);

        // THEN we expect Exception to be thrown

    }


}