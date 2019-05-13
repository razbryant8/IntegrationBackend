package smartspace.dao.rdb;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import smartspace.data.ActionEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RdbActionDaoTest {

    @Autowired
    private RdbActionDao rdbActionDao;
    private ActionEntity actionEntity;

    @Before
    public void setUp() {
        actionEntity = new ActionEntity();
    }

    @After
    public void tearDown() {
        rdbActionDao.deleteAll();
    }

    @Test
    public void testCreate() {
        ActionEntity actionEntity = rdbActionDao.create(this.actionEntity);
        assertNotNull("actionEntity ID is null", actionEntity.getActionId());
    }

    @Test
    public void testReadAllOneRow() {
        ActionEntity actionEntity = rdbActionDao.create(this.actionEntity);
        List<ActionEntity> actionEntities = this.rdbActionDao.readAll();
        assertThat(actionEntities).usingElementComparatorOnFields("actionId").contains(actionEntity);
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

        assertThat(actionEntities).usingElementComparatorOnFields("actionId").contains(actionEntity1, actionEntity2, actionEntity3);

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