package smartspace.dao.rdb;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import smartspace.Application;
import smartspace.data.ActionEntity;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Application.class})
public class RdbActionDaoTest {

    @Autowired
    private RdbActionDao rdbActionDao;
    private ActionEntity actionEntity;
    private String marktest;

    @Before
    public void setUp() {
        actionEntity = new ActionEntity();
    }

    @Test
    public void testCreate() {
        ActionEntity actionEntity = rdbActionDao.create(this.actionEntity);
        assertNotNull("actionEntity ID is null", actionEntity.getActionId());
    }

    @Test
    public void testReadAllOneRow() {
        ActionEntity actionEntity = rdbActionDao.create(this.actionEntity);
        assertTrue("Row was not created", this.rdbActionDao.readAll().contains(actionEntity));
    }

    @Test
    public void testReadAllThreeRow() {
        //Initialize variables to add
        ActionEntity actionEntity1 = new ActionEntity();
        ActionEntity actionEntity2 = new ActionEntity();
        ActionEntity actionEntity3 = new ActionEntity();

        //Clear db for test
        rdbActionDao.deleteAll();

        //Add initialized variables to db
        actionEntity1 = rdbActionDao.create(actionEntity1);
        actionEntity2 = rdbActionDao.create(actionEntity2);
        actionEntity3 = rdbActionDao.create(actionEntity3);

        List<ActionEntity> actionEntities = rdbActionDao.readAll();

        assertTrue("actionEntity1 not created/found", actionEntities.contains(actionEntity1));
        assertTrue("actionEntity2 not created/found", actionEntities.contains(actionEntity2));
        assertTrue("actionEntity3 not created/found", actionEntities.contains(actionEntity3));

        assertEquals("Wrong number of rows", 3, actionEntities.size());
    }

    @Test
    public void testDeleteAll() {
        //Add initialized variables to db
        rdbActionDao.create(new ActionEntity());
        rdbActionDao.create(new ActionEntity());
        rdbActionDao.create(new ActionEntity());

        rdbActionDao.deleteAll();

        assertEquals("Failed deleting all rows", 0, rdbActionDao.readAll().size());
    }


}