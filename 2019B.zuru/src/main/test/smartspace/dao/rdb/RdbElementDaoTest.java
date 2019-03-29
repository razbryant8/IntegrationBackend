package smartspace.dao.rdb;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class RdbElementDaoTest {

    @Autowired
    private RdbElementDao rdbElementDao;

    private ElementEntity elementEntity;
    private ExpectedException thrown;


    @Before
    public void setUp() {
        elementEntity = new ElementEntity();
    }

    @Test
    public void testCreate() {
        ElementEntity elementEntity = rdbElementDao.create(this.elementEntity);
        // Checking if id was generated and set to the elementEntity.
        assertNotNull("Id is null", elementEntity.getElementId());
    }

    @Test
    public void testReadById() {
        ElementEntity elementEntity = rdbElementDao.create(this.elementEntity);
        // Checking if id was generated and set to the elementEntity.
        assertNotNull("Id is null", elementEntity.getElementId());
        // Read the row I just created
        Optional<ElementEntity> elementEntityOpt = rdbElementDao.readById(elementEntity.getElementId());
        assertTrue("Row was not created/found.", elementEntityOpt.isPresent());
    }


    @Test
    public void testReadAll() {
        ElementEntity elementEntity = rdbElementDao.create(this.elementEntity);
        // Read all the rows and look just for the one.
        List<ElementEntity> elementEntities = rdbElementDao.readAll();
        assertTrue("Row was not created/found.", elementEntities.contains(elementEntity));
    }


    @Test
    public void testReadAllThreeRows() {
        ElementEntity row1 = new ElementEntity();
        ElementEntity row2 = new ElementEntity();
        ElementEntity row3 = new ElementEntity();

        //Clear db
        rdbElementDao.deleteAll();

        //Create 3 rows
        ElementEntity elementEntity1 = rdbElementDao.create(row1);
        ElementEntity elementEntity2 = rdbElementDao.create(row2);
        ElementEntity elementEntity3 = rdbElementDao.create(row3);

        // Read all the rows from db
        List<ElementEntity> elementEntities = rdbElementDao.readAll();

        assertEquals("Wrong row number",3,elementEntities.size());
        assertTrue("elementEntity1 was not created/found.", elementEntities.contains(elementEntity1));
        assertTrue("elementEntity2 was not created/found.", elementEntities.contains(elementEntity2));
        assertTrue("elementEntity3 was not created/found.", elementEntities.contains(elementEntity3));
    }


    @Test
    public void testUpdateIllegalRow(){
        //Create a row
        ElementEntity elementEntity = rdbElementDao.create(this.elementEntity);
        // Change the id for a wrong id.
        elementEntity.setElementId(elementEntity.getName());
        // Ask DB to update the row for that entity.
        thrown.expectMessage("no element with id:");
        rdbElementDao.update(elementEntity);
    }

    @Test
    public void testUpdate(){
        //Create a row
        ElementEntity elementEntity = rdbElementDao.create(this.elementEntity);
        elementEntity.setName("Name");
        rdbElementDao.update(elementEntity);

        Optional<ElementEntity> elementEntity1 = rdbElementDao.readById(elementEntity.getElementId());
        elementEntity1.ifPresent(elementEntity2 -> assertEquals("Name field was not updated", "Name", elementEntity2.getName()));

    }

    @Test
    public void testDeleteByKey() {
       elementEntity = rdbElementDao.create(elementEntity);

       rdbElementDao.deleteByKey(elementEntity.getElementId());

       assertTrue("Failed deleting elementEntity by id", rdbElementDao.readById(elementEntity.getElementId()).isPresent());
    }

    @Test
    public void testDelete() {
        elementEntity = rdbElementDao.create(elementEntity);

        rdbElementDao.delete(elementEntity);

        assertTrue("Failed deleting elementEntity by object", rdbElementDao.readById(elementEntity.getElementId()).isPresent());
    }

    @Test
    public void testDeleteAll() {
        //Add initialized variables to db
        rdbElementDao.create(new ElementEntity());
        rdbElementDao.create(new ElementEntity());
        rdbElementDao.create(new ElementEntity());

        rdbElementDao.deleteAll();

        assertEquals("Failed deleting all", 0, rdbElementDao.readAll().size());
    }
}