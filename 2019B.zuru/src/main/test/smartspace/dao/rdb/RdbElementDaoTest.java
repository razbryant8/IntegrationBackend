package smartspace.dao.rdb;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
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
        assertTrue("Row was not created/found.", elementEntities.contains(elementEntity1));
        assertTrue("Row was not created/found.", elementEntities.contains(elementEntity2));
        assertTrue("Row was not created/found.", elementEntities.contains(elementEntity3));
    }


    @Test
    public void testUpdateIllegalRow(){
        //Create a row
        ElementEntity elementEntity = rdbElementDao.create(this.elementEntity);

        rdbElementDao.update(elementEntity);


    }
}