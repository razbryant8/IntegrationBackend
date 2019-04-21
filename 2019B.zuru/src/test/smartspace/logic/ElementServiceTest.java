package smartspace.logic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import smartspace.dao.EnhancedElementDao;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.util.EntityFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"spring.profiles.active=default"})
public class ElementServiceTest {
    private EnhancedElementDao<String> enhancedDao;

    private EntityFactory factory;

    private ElementServiceImpl elementService;

    private String currentSmartSpace;

    @Value("${spring.application.name}")
    public void setCurrentSmartSpace(String currentSmartSpace) {
        this.currentSmartSpace = currentSmartSpace;
    }

    @Autowired
    public void setElementService(ElementServiceImpl elementService) {
        this.elementService = elementService;
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
    public void checkGetAllInEmptyPage() {
        // GIVEN The database contains one element
        ElementEntity elementEntity = enhancedDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), null, null, false, new HashMap<>()));

        // WHEN getAll elements in an empty page
        int size = 5;
        int page = 1;
        List<ElementEntity> elementEntities = elementService.getAll(size, page);

        // THEN the List is empty
        assertEquals(0, elementEntities.size());
    }

    @Test()
    public void checkGetAllInPageWithOneElement() {
        // GIVEN The database contains one element
        ElementEntity elementEntity = enhancedDao.create(factory.createNewElement("name", "type", new Location(5, 4), new Date(), null, null, false, new HashMap<>()));

        // WHEN getAll elements in an empty page
        int size = 5;
        int page = 0;
        List<ElementEntity> elementEntities = elementService.getAll(size, page);

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
        List<ElementEntity> elementEntities = elementService.getAll(size, page);

        // THEN the List contains exactly the three created elements
        assertEquals(3, elementEntities.size());
        assertThat(elementEntities).usingElementComparatorOnFields("key").contains(elementEntity1);
        assertThat(elementEntities).usingElementComparatorOnFields("key").contains(elementEntity2);
        assertThat(elementEntities).usingElementComparatorOnFields("key").contains(elementEntity3);
    }

    @Test()
    public void checkStore() {
        // GIVEN Valid Element Entity
        ElementEntity elementEntity = factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", "test", false, new HashMap<>());
        elementEntity.setKey("1#anotherTeam");

        // WHEN we store the entity using ElementService Logic
        elementService.store(elementEntity);

        // THEN the entity is stored
        List<ElementEntity> entities = this.enhancedDao.readAll();
        assertEquals(1, entities.size());
        assertThat(entities).usingElementComparatorOnFields("key").contains(elementEntity);
    }

    @Test(expected = Throwable.class)
    public void checkValidateIlligalSmartSpace() {
        // GIVEN Entity with the current Smartspace
        ElementEntity elmentEntity = factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", "test", false, new HashMap<>());
        elmentEntity.setKey("1#" + this.currentSmartSpace);

        // WHEN we store the entity using ElementService Logic
        elementService.store(elmentEntity);

        // THEN we expect Exception to be thrown
    }

    @Test(expected = Throwable.class)
    public void checkValidateIlligalMoreAttributes() {
        // GIVEN Entity with null more attributes
        ElementEntity elmentEntity = factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", "test", false, null);
        elmentEntity.setKey("1#" + this.currentSmartSpace);

        // WHEN we store the entity using ElementService Logic
        elementService.store(elmentEntity);

        // THEN we expect Exception to be thrown
    }

    @Test(expected = Throwable.class)
    public void checkValidateIlligalCreatorSmartSpace() {
        // GIVEN Entity with null creatorSmartspace
        ElementEntity elmentEntity = factory.createNewElement("name", "type", new Location(5, 4), new Date(), "zur@gmail.com", null, false, new HashMap<>());
        elmentEntity.setKey("1#" + this.currentSmartSpace);

        // WHEN we store the entity using ElementService Logic
        elementService.store(elmentEntity);

        // THEN we expect Exception to be thrown
    }

    @Test(expected = Throwable.class)
    public void checkValidateIlligalCreatorMail() {
        // GIVEN Entity with bad CreatorMail
        ElementEntity elmentEntity = factory.createNewElement("name", "type", new Location(5, 4), new Date(), "      ", "test", false, new HashMap<>());
        elmentEntity.setKey("1#" + this.currentSmartSpace);

        // WHEN we store the entity using ElementService Logic
        elementService.store(elmentEntity);

        // THEN we expect Exception to be thrown
    }

    @Test(expected = Throwable.class)
    public void checkValidateIlligalType() {
        // GIVEN Entity with Bad Type
        ElementEntity elmentEntity = factory.createNewElement("name", "            ", new Location(5, 4), new Date(), "zur@gmail.com", "test", false, new HashMap<>());
        elmentEntity.setKey("1#" + this.currentSmartSpace);

        // WHEN we store the entity using ElementService Logic
        elementService.store(elmentEntity);

        // THEN we expect Exception to be thrown
    }

    @Test(expected = Throwable.class)
    public void checkValidateIlligalName() {
        // GIVEN Entity with Bad Name
        ElementEntity elmentEntity = factory.createNewElement("              ", "type", new Location(5, 4), new Date(), "zur@gmail.com", "test", false, new HashMap<>());
        elmentEntity.setKey("1#test");

        // WHEN we store the entity using ElementService Logic
        elementService.store(elmentEntity);

        // THEN we expect Exception to be thrown
    }

    @Test(expected = Throwable.class)
    public void checkValidateIlligalLocation() {
        // GIVEN Entity without location
        ElementEntity elmentEntity = factory.createNewElement("name", "type", null, new Date(), "zur@gmail.com", "test", false, new HashMap<>());
        elmentEntity.setKey("1#test");

        // WHEN we store the entity using ElementService Logic
        elementService.store(elmentEntity);

        // THEN we expect Exception to be thrown
    }
}
