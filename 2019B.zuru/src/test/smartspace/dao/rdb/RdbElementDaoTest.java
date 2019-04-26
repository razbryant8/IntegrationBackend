package smartspace.dao.rdb;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import smartspace.dao.ElementDao;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.util.EntityFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties= {"spring.profiles.active=default"})
public class RdbElementDaoTest {

    @Autowired
    private ElementDao<String> elementDao;

    @Autowired
    private EntityFactory factory;

    private ElementEntity elementEntity;


    @Before
    public void setUp() {
        elementEntity = factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>());
        elementDao.deleteAll();
    }

    @Test
    public void testCreate() {
        ElementEntity elementEntity = elementDao.create(this.elementEntity);
        // Checking if id was generated and set to the elementEntity.
        assertNotNull("Id is null", elementEntity.getElementId());
    }

    @Test
    public void testReadById() {
        ElementEntity elementEntity = elementDao.create(this.elementEntity);
        // Checking if id was generated and set to the elementEntity.
        assertNotNull("Id is null", elementEntity.getElementId());
        // Read the row I just created
        Optional<ElementEntity> elementEntityOpt = elementDao.readById(elementEntity.getElementId());
        assertTrue("Row was not created/found.", elementEntityOpt.isPresent());
    }


    @Test
    public void testReadAll() {
        ElementEntity elementEntity = elementDao.create(this.elementEntity);
        // Read all the rows and look just for the one.
        List<ElementEntity> elementEntities = elementDao.readAll();
        assertThat(elementEntities).usingElementComparatorOnFields("elementId").contains(elementEntity);
    }


    @Test
    public void testReadAllThreeRows() {
        ElementEntity row1 = new ElementEntity();
        ElementEntity row2 = new ElementEntity();
        ElementEntity row3 = new ElementEntity();

        //Clear db
        elementDao.deleteAll();

        //Create 3 rows
        ElementEntity elementEntity1 = elementDao.create(this.elementEntity);
        ElementEntity elementEntity2 = elementDao.create(this.elementEntity);
        ElementEntity elementEntity3 = elementDao.create(this.elementEntity);

        // Read all the rows from db
        List<ElementEntity> elementEntities = elementDao.readAll();

        assertEquals("Wrong row number", 3, elementEntities.size());
        assertThat(elementEntities).usingElementComparatorOnFields("elementId").contains(elementEntity1);
        assertThat(elementEntities).usingElementComparatorOnFields("elementId").contains(elementEntity2);
        assertThat(elementEntities).usingElementComparatorOnFields("elementId").contains(elementEntity3);
    }


    @Test(expected = Throwable.class)
    public void testUpdateIllegalRow() {
        //Create a row
        ElementEntity elementEntity = elementDao.create(this.elementEntity);
        // Change the id for a wrong id.
        elementEntity.setElementId(elementEntity.getName());
        // Ask DB to update the row for that entity.
        elementDao.update(elementEntity);
    }

    @Test
    public void testUpdate() {
        //Create a row
        ElementEntity elementEntity = elementDao.create(this.elementEntity);
        elementEntity.setName("Name");
        elementDao.update(elementEntity);

        Optional<ElementEntity> elementEntity1 = elementDao.readById(elementEntity.getElementId());
        elementEntity1.ifPresent(elementEntity2 -> assertEquals("Name field was not updated", "Name", elementEntity2.getName()));

    }

    @Test
    public void testDeleteByKey() {
        elementEntity = elementDao.create(elementEntity);

        elementDao.deleteByKey(elementEntity.getElementId());

        assertFalse("Failed deleting elementEntity by id", elementDao.readById(elementEntity.getElementId()).isPresent());
    }

    @Test
    public void testDelete() {
        elementEntity = elementDao.create(elementEntity);

        elementDao.delete(elementEntity);

        assertFalse("Failed deleting elementEntity by object", elementDao.readById(elementEntity.getElementId()).isPresent());
    }

    @Test
    public void testDeleteAll() {
        //Add initialized variables to db
        elementDao.create(this.elementEntity);
        elementDao.create(this.elementEntity);
        elementDao.create(this.elementEntity);

        elementDao.deleteAll();

        assertEquals("Failed deleting all", 0, elementDao.readAll().size());
    }
}