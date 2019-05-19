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
import smartspace.dao.ElementNotFoundException;
import smartspace.dao.EnhancedElementDao;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.UserRole;
import smartspace.data.util.EntityFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"spring.profiles.active=default"})
public class ElementServiceTest {
    private EnhancedElementDao<String> enhancedDao;

    private EntityFactory factory;

    private ElementServiceImpl elementService;

    private UserService userService;

    private String currentSmartSpace;

    @Value("${spring.smartspace.name}")
    public void setCurrentSmartSpace(String currentSmartSpace) {
        this.currentSmartSpace = currentSmartSpace;
    }

    @Autowired
    public void setElementService(ElementServiceImpl elementService) {
        this.elementService = elementService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setFactory(EntityFactory factory) {
        this.factory = factory;
    }

    @Autowired
    public void setEnhancedDao(EnhancedElementDao<String> enhancedDao) {
        this.enhancedDao = enhancedDao;
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        this.enhancedDao.deleteAll();
    }

    @Test()
    public void checkGetAllInEmptyPageAsAdmin() {
        // GIVEN The database contains one element
        enhancedDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), null, null, false, new HashMap<>()));

        // WHEN getAll elements in an empty page as admin
        int size = 5;
        int page = 1;
        List<ElementEntity> elementEntities = elementService
                .getAll(size, page, UserRole.ADMIN);

        // THEN the List is empty
        assertEquals(0, elementEntities.size());
    }

    @Test()
    public void checkGetAllInPageWithOneElementAsAdmin() {
        // GIVEN The database contains one element
        ElementEntity elementEntity = enhancedDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), null, null, false, new HashMap<>()));

        // WHEN getAll elements in an empty page
        int size = 5;
        int page = 0;
        List<ElementEntity> elementEntities = elementService.
                getAll(size, page, UserRole.ADMIN);

        // THEN the List contains exactly one element
        assertEquals(1, elementEntities.size());
        assertThat(elementEntities).usingElementComparatorOnFields("key").contains(elementEntity);
    }

    @Test()
    public void checkGetAllInPageWithThreeElement() {
        // GIVEN The database contains one element
        ElementEntity elementEntity1 = enhancedDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), null, null, false, new HashMap<>()));
        ElementEntity elementEntity2 = enhancedDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), null, null, false, new HashMap<>()));
        ElementEntity elementEntity3 = enhancedDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), null, null, false, new HashMap<>()));

        // WHEN getAll elements in an empty page
        int size = 5;
        int page = 0;
        List<ElementEntity> elementEntities = elementService
                .getAll(size, page, UserRole.ADMIN);

        // THEN the List contains exactly the three created elements
        assertEquals(3, elementEntities.size());
        assertThat(elementEntities).usingElementComparatorOnFields("key").contains(elementEntity1);
        assertThat(elementEntities).usingElementComparatorOnFields("key").contains(elementEntity2);
        assertThat(elementEntities).usingElementComparatorOnFields("key").contains(elementEntity3);
    }

    @Test()
    public void checkStoreAsAdmin() {
        // GIVEN Valid Element Entity
        ElementEntity elementEntity1 = factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", "test", false, new HashMap<>());
        ElementEntity elementEntity2 = factory.createNewElement("name2", "type2", new Location(5, 10), new Date(), "zur@gmail.com", "test", false, new HashMap<>());
        elementEntity1.setKey("1#anotherTeam");
        elementEntity2.setKey("2#anotherTeam");
        ElementEntity[] elementEntities = {elementEntity1, elementEntity2};

        // WHEN we store the entity using ElementService Logic as admin
        elementService.
                store(elementEntities, UserRole.ADMIN);

        // THEN the entity is stored
        List<ElementEntity> entities = this.enhancedDao.readAll();
        assertEquals(2, entities.size());
        assertThat(entities).usingElementComparatorOnFields("key").contains(elementEntity1);
        assertThat(entities).usingElementComparatorOnFields("key").contains(elementEntity2);
    }

    @Test(expected = Throwable.class)
    public void checkValidateIllegalSmartSpace() {
        // GIVEN Entity with the current Smartspace
        ElementEntity elementEntity1 = factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", "test", false, new HashMap<>());
        ElementEntity elementEntity2 = factory.createNewElement("name2", "type2", new Location(5, 4), new Date(), "zur@gmail.com", "test", false, new HashMap<>());
        elementEntity1.setKey("1#anotherteam" + this.currentSmartSpace);
        elementEntity2.setKey("2#" + this.currentSmartSpace);
        ElementEntity[] elementEntities = {elementEntity1, elementEntity2};

        // WHEN we store the entity using ElementService Logic As Admin
        elementService.
                store(elementEntities, UserRole.ADMIN);

        // THEN we expect Exception to be thrown
    }

    @Test(expected = Throwable.class)
    public void checkValidateIllegalMoreAttributesAsAdmin() {
        // GIVEN Entity with null more attributes
        ElementEntity elementEntity = factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", "test", false, null);
        elementEntity.setKey("1#" + this.currentSmartSpace);
        ElementEntity[] elementEntities = {elementEntity};

        // WHEN we store the entity using ElementService Logic As Admin
        elementService.
                store(elementEntities, UserRole.ADMIN);

        // THEN we expect Exception to be thrown
    }

    @Test(expected = Throwable.class)
    public void checkValidateIllegalCreatorSmartSpaceAsAdmin() {
        // GIVEN Entity with null creatorSmartspace
        ElementEntity elementEntity = factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", null, false, new HashMap<>());
        elementEntity.setKey("1#" + this.currentSmartSpace);
        ElementEntity[] elementEntities = {elementEntity};


        // WHEN we store the entity using ElementService LogicAs Admin
        elementService.
                store(elementEntities, UserRole.ADMIN);

        // THEN we expect Exception to be thrown
    }

    @Test(expected = Throwable.class)
    public void checkValidateIllegalCreatorMailAsAdmin() {
        // GIVEN Entity with bad CreatorMail
        ElementEntity elementEntity = factory.createNewElement("name", "type", new Location(5, 4), new Date(), "      ", "test", false, new HashMap<>());
        elementEntity.setKey("1#" + this.currentSmartSpace);
        ElementEntity[] elementEntities = {elementEntity};


        // WHEN we store the entity using ElementService Logic as admin
        elementService.store(elementEntities, UserRole.ADMIN);

        // THEN we expect Exception to be thrown
    }

    @Test(expected = Throwable.class)
    public void checkValidateIllegalTypeAsAdmin() {
        // GIVEN Entity with Bad Type
        ElementEntity elementEntity = factory.createNewElement("name", "            ", new Location(5, 4), new Date(), "zur@gmail.com", "test", false, new HashMap<>());
        elementEntity.setKey("1#" + this.currentSmartSpace);
        ElementEntity[] elementEntities = {elementEntity};


        // WHEN we store the entity using ElementService Logic as admin
        elementService.
                store(elementEntities, UserRole.ADMIN);

        // THEN we expect Exception to be thrown
    }

    @Test(expected = Throwable.class)
    public void checkValidateIllegalNameAsAdmin() {
        // GIVEN Entity with Bad Name
        ElementEntity elementEntity = factory.createNewElement("              ", "type", new Location(5, 4), new Date(), "zur@gmail.com", "test", false, new HashMap<>());
        elementEntity.setKey("1#test");
        ElementEntity[] elementEntities = {elementEntity};


        // WHEN we store the entity using ElementService Logic As Admin
        elementService.
                store(elementEntities, UserRole.ADMIN);

        // THEN we expect Exception to be thrown
    }

    @Test(expected = Throwable.class)
    public void checkValidateIllegalLocationAsAdmin() {
        // GIVEN Entity without location
        ElementEntity elementEntity = factory.createNewElement("name", "type", null, new Date(), "zur@gmail.com", "test", false, new HashMap<>());
        elementEntity.setKey("1#test");
        ElementEntity[] elementEntities = {elementEntity};


        // WHEN we store the entity using ElementService Logic AsAdmin
        elementService.
                store(elementEntities, UserRole.ADMIN);

        // THEN we expect Exception to be thrown
    }

    @Test(expected = Throwable.class)
    public void getElementByIllegalElement() {
        // GIVEN nothing

        // WHEN we get element by id that does not exists as player
        elementService.getById("id", "smartspace", UserRole.PLAYER);

        // THEN we expect Exception to be thrown
    }

    @Test(expected = Throwable.class)
    public void getElementByIllegalSmartspace() {
        // GIVEN the database contains 2 elements
        ElementEntity elementEntity1 = enhancedDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), null, null, false, new HashMap<>()));

        // WHEN we get element by smartspace that does not exists as player
        elementService.getById(elementEntity1.getElementId(), "test", UserRole.PLAYER);

        // THEN we expect Exception to be thrown
    }

    @Test
    public void getElementByIDByManager() {
        // GIVEN the database contains 2 elements
        ElementEntity elementEntity1 = enhancedDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), null, null, true, new HashMap<>()));
        ElementEntity elementEntity2 = enhancedDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), null, null, false, new HashMap<>()));

        // WHEN we get element by smartspace and ID as manager
        ElementEntity entity = elementService.getById(elementEntity1.getElementId(), elementEntity2.getElementSmartspace(), UserRole.MANAGER);

        // THEN we expect to get the element
        assertEquals(elementEntity1.getKey(), entity.getKey());
    }

    @Test(expected = ElementNotFoundException.class)
    public void getExpiredElementByIDByPlayer() {
        // GIVEN the database contains 2 elements
        ElementEntity elementEntity1 = enhancedDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), null, null, true, new HashMap<>()));
        enhancedDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), null, null, false, new HashMap<>()));

        // WHEN we get element by smartspace and ID as player
        ElementEntity entity = elementService.getById(elementEntity1.getElementId(), elementEntity1.getElementSmartspace(), UserRole.PLAYER);

        // THEN we expect to get the element
        assertEquals(elementEntity1.getKey(), entity.getKey());
    }

    @Test
    public void getElementByIDByPlayer() {
        // GIVEN the database contains 2 elements
        enhancedDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), null, null, true, new HashMap<>()));
        ElementEntity elementEntity2 = enhancedDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), null, null, false, new HashMap<>()));

        // WHEN we get element by smartspace and ID as player
        ElementEntity entity = elementService.getById(elementEntity2.getElementId(), elementEntity2.getElementSmartspace(), UserRole.PLAYER);

        // THEN we expect to get the element
        assertEquals(elementEntity2.getKey(), entity.getKey());
    }

    @Test
    public void getElementByTypeUsingPaginationAsManager() {
        // GIVEN the database contains 2 elements
        String type = "type";
        int size = 5;
        int page = 0;
        ElementEntity elementEntity1 = enhancedDao.create(factory.createNewElement("name", type, new Location(5, 4), new Date(), null, null, true, new HashMap<>()));
        ElementEntity elementEntity2 = enhancedDao.create(factory.createNewElement("name", type, new Location(5, 4), new Date(), null, null, false, new HashMap<>()));

        // WHEN we get elements by type as manager
        List<ElementEntity> entities = elementService.getByType(size, page, type, UserRole.MANAGER);

        // THEN we expect to get 2 elements
        assertEquals(entities.size(), 2);
        assertThat(entities).usingElementComparatorOnFields("key").contains(elementEntity1);
        assertThat(entities).usingElementComparatorOnFields("key").contains(elementEntity2);
    }

    @Test
    public void getElementByTypeUsingPaginationAsPlayer() {
        // GIVEN the database contains 2 elements
        String type = "type";
        int size = 5;
        int page = 0;
        enhancedDao.create(factory.createNewElement("name", type, new Location(5, 4), new Date(), null, null, true, new HashMap<>()));
        ElementEntity elementEntity2 = enhancedDao.create(factory.createNewElement("name", type, new Location(5, 4), new Date(), null, null, false, new HashMap<>()));

        // WHEN we get elements by type as player
        List<ElementEntity> entities = elementService.getByType(size, page, type, UserRole.PLAYER);

        // THEN we expect to get 1 elements
        assertEquals(entities.size(), 1);
        assertThat(entities).usingElementComparatorOnFields("key").contains(elementEntity2);
    }

    @Test
    public void getElementByWrongTypeUsingPagination() {
        // GIVEN nothing
        String type = "type";
        int size = 5;
        int page = 0;

        // WHEN we get elements by type
        List<ElementEntity> entities = elementService.getByType(size, page, type, UserRole.MANAGER);

        // THEN we expect to get 0 elements
        assertEquals(entities.size(), 0);
    }

    @Test
    public void getElementByNameUsingPaginationAsManager() {
        // GIVEN the database contains 2 elements
        String name = "name";
        int size = 5;
        int page = 0;
        ElementEntity elementEntity1 = enhancedDao.create(factory.createNewElement(name, "type", new Location(5, 4), new Date(), null, null, true, new HashMap<>()));
        ElementEntity elementEntity2 = enhancedDao.create(factory.createNewElement(name, "type", new Location(5, 4), new Date(), null, null, false, new HashMap<>()));

        // WHEN we get elements by name as manager
        List<ElementEntity> entities = elementService.getByName(size, page, name, UserRole.MANAGER);

        // THEN we expect to get 2 elements
        assertEquals(entities.size(), 2);
        assertThat(entities).usingElementComparatorOnFields("key").contains(elementEntity1);
        assertThat(entities).usingElementComparatorOnFields("key").contains(elementEntity2);
    }

    @Test
    public void getElementByNameUsingPaginationAsPlayer() {
        // GIVEN the database contains 2 elements
        String name = "name";
        int size = 5;
        int page = 0;
        enhancedDao.create(factory.createNewElement(name, "type", new Location(5, 4), new Date(), null, null, true, new HashMap<>()));
        ElementEntity elementEntity2 = enhancedDao.create(factory.createNewElement(name, "type", new Location(5, 4), new Date(), null, null, false, new HashMap<>()));

        // WHEN we get elements by name as player
        List<ElementEntity> entities = elementService.getByName(size, page, name, UserRole.PLAYER);

        // THEN we expect to get 1 elements
        assertEquals(entities.size(), 1);
        assertThat(entities).usingElementComparatorOnFields("key").contains(elementEntity2);
    }

    @Test
    public void getElementByWrongNameUsingPagination() {
        // GIVEN nothing
        String name = "name";
        int size = 5;
        int page = 0;

        // WHEN we get elements by name as manager
        List<ElementEntity> entities = elementService.getByName(size, page, name, UserRole.MANAGER);

        // THEN we expect to get 0 elements
        assertEquals(entities.size(), 0);
    }

    @Test(expected = Throwable.class)
    public void createBadElementAsManager() {
        // GIVEN nothing

        // WHEN we get create new element without location as manager
        ElementEntity elmentEntity = factory.createNewElement("name", "type", null, new Date(), "zur@gmail.com", "test", false, new HashMap<>());
        elementService.create(elmentEntity, UserRole.MANAGER);

        // THEN we expect Exception to be thrown
    }

    @Test
    public void createElementAsManager() {
        // GIVEN nothing

        // WHEN we get create new element
        ElementEntity elementEntity1 = factory.createNewElement("name", "type", new Location(), new Date(), "zur@gmail.com", "test", false, new HashMap<>());
        elementService.create(elementEntity1, UserRole.MANAGER);

        // THEN we expect one element to be created
        int size = 5;
        int page = 0;
        List<ElementEntity> entities = enhancedDao.readAll(size, page);
        assertEquals(entities.size(), 1);
        assertThat(entities).usingElementComparatorOnFields("key").contains(elementEntity1);
    }

    @Test
    public void checkGetByLocationAsPlayer() {
        // GIVEN we have 3 elements, while one is expired and one is outside radius.
        enhancedDao.create(factory.createNewElement("name", "type", new Location(50, 50), new Date(), null, null, true, new HashMap<>()));
        ElementEntity elementEntity2 = enhancedDao.create(factory.createNewElement("name", "type", new Location(45, 45), new Date(), null, null, false, new HashMap<>()));
        enhancedDao.create(factory.createNewElement("name", "type", new Location(450, 45), new Date(), null, null, false, new HashMap<>()));

        // WHEN we try to get them as a user.
        List<ElementEntity> entities = elementService.getByLocation(3, 0, 50, 50, 10, UserRole.PLAYER);

        // THEN we expect to have only the second element returned.
        assertThat(entities).hasSize(1);
        assertThat(entities).usingElementComparatorOnFields("key").contains(elementEntity2);
    }

    @Test
    public void checkGetByLocationAsManager() {
        // GIVEN we have 3 elements, while one is expired and one is outside radius.
        ElementEntity elementEntity1 = enhancedDao.create(factory.createNewElement("name", "type", new Location(50, 50), new Date(), null, null, true, new HashMap<>()));
        ElementEntity elementEntity2 = enhancedDao.create(factory.createNewElement("name", "type", new Location(45, 45), new Date(), null, null, false, new HashMap<>()));
        enhancedDao.create(factory.createNewElement("name", "type", new Location(450, 45), new Date(), null, null, false, new HashMap<>()));

        // WHEN we try to get them as a user.
        List<ElementEntity> entities = elementService.getByLocation(3, 0, 50, 50, 10, UserRole.MANAGER);

        // THEN we expect to have element 1 and 2 returned.
        assertThat(entities).hasSize(2);
        assertThat(entities).usingElementComparatorOnFields("key").contains(elementEntity1);
        assertThat(entities).usingElementComparatorOnFields("key").contains(elementEntity2);
    }

    @Test(expected = Throwable.class)
    public void checkGetByLocationAsUnauthorized() {
        // GIVEN we have 3 elements, while one is expired and one is outside radius.
        enhancedDao.create(factory.createNewElement("name", "type", new Location(50, 50), new Date(), null, null, true, new HashMap<>()));
        enhancedDao.create(factory.createNewElement("name", "type", new Location(45, 45), new Date(), null, null, false, new HashMap<>()));
        enhancedDao.create(factory.createNewElement("name", "type", new Location(450, 45), new Date(), null, null, false, new HashMap<>()));

        // WHEN we try to get them as an unauthorized user.
        elementService.getByLocation(3, 0, 50, 50, 10, null);

        // THEN we expect an exception
    }

    @Test
    public void checkGetAllElementsAsPlayer() {
        // GIVEN we have 3 elements when 1 is expired
        enhancedDao.create(factory.createNewElement("name", "type", new Location(50, 50), new Date(), null, null, true, new HashMap<>()));
        ElementEntity elementEntity2 = enhancedDao.create(factory.createNewElement("name", "type", new Location(45, 45), new Date(), null, null, false, new HashMap<>()));
        ElementEntity elementEntity3 = enhancedDao.create(factory.createNewElement("name", "type", new Location(450, 45), new Date(), null, null, false, new HashMap<>()));

        // WHEN we try to get them as a user.
        List<ElementEntity> entities = elementService.getAllElements(3, 0, UserRole.PLAYER);

        // THEN we expect to have only the second and third elements.
        assertThat(entities).hasSize(2);
        assertThat(entities).usingElementComparatorOnFields("key").contains(elementEntity2);
        assertThat(entities).usingElementComparatorOnFields("key").contains(elementEntity3);
    }

    @Test
    public void checkGetAllElementsAsManager() {
        // GIVEN we have 3 elements when 1 is expired
        ElementEntity elementEntity1 = enhancedDao.create(factory.createNewElement("name", "type", new Location(50, 50), new Date(), null, null, true, new HashMap<>()));
        ElementEntity elementEntity2 = enhancedDao.create(factory.createNewElement("name", "type", new Location(45, 45), new Date(), null, null, false, new HashMap<>()));
        ElementEntity elementEntity3 = enhancedDao.create(factory.createNewElement("name", "type", new Location(450, 45), new Date(), null, null, false, new HashMap<>()));

        // WHEN we try to get them as a user.
        List<ElementEntity> entities = elementService.getAllElements(3, 0, UserRole.MANAGER);

        // THEN we expect to have all 3
        assertThat(entities).hasSize(3);
        assertThat(entities).usingElementComparatorOnFields("key").contains(elementEntity1);
        assertThat(entities).usingElementComparatorOnFields("key").contains(elementEntity2);
        assertThat(entities).usingElementComparatorOnFields("key").contains(elementEntity3);
    }

    @Test(expected = Throwable.class)
    public void checkGetAllElementsAsUnAuthorized() {
        // GIVEN we have 3 elements when 1 is expired
        ElementEntity elementEntity1 = enhancedDao.create(factory.createNewElement("name", "type", new Location(50, 50), new Date(), null, null, true, new HashMap<>()));
        ElementEntity elementEntity2 = enhancedDao.create(factory.createNewElement("name", "type", new Location(45, 45), new Date(), null, null, false, new HashMap<>()));
        ElementEntity elementEntity3 = enhancedDao.create(factory.createNewElement("name", "type", new Location(450, 45), new Date(), null, null, false, new HashMap<>()));

        // WHEN we try to get them as a user.
        List<ElementEntity> entities = elementService.getAllElements(3, 0, null);

        // THEN we expect to have all 3
        assertThat(entities).hasSize(3);
        assertThat(entities).usingElementComparatorOnFields("key").contains(elementEntity1);
        assertThat(entities).usingElementComparatorOnFields("key").contains(elementEntity2);
        assertThat(entities).usingElementComparatorOnFields("key").contains(elementEntity3);
    }

    @Test
    public void testUpdateElementAsManager() {
        // GIVEN we have an element in db
        ElementEntity elementEntity1 = factory.createNewElement("name", "type", new Location(), new Date(), "zur@gmail.com", "test", false, new HashMap<>());
        ElementEntity result = elementService.create(elementEntity1, UserRole.MANAGER);
        elementEntity1.setName("Omri");

        // WHEN we update that element
        elementService.update(elementEntity1, result.getElementId(), result.getElementSmartspace(), UserRole.MANAGER);

        // THEN we expect one element to be updated
        int size = 5;
        int page = 0;
        List<ElementEntity> entities = enhancedDao.readAll(size, page);
        assertEquals(entities.size(), 1);
        assertThat(entities).usingElementComparatorOnFields("key").contains(elementEntity1);
    }


    @Test(expected = Throwable.class)
    public void testUpdateElementAsPlayer() {
        // GIVEN we have an element in db
        ElementEntity elementEntity1 = factory.createNewElement("name", "type", new Location(), new Date(), "zur@gmail.com", "test", false, new HashMap<>());
        ElementEntity result = elementService.create(elementEntity1, UserRole.MANAGER);
        elementEntity1.setName("Omri");

        // WHEN we update that element
        elementService.update(elementEntity1, result.getElementId(), result.getElementSmartspace(), UserRole.PLAYER);

        // THEN we expect Exception to be thrown
    }

}
