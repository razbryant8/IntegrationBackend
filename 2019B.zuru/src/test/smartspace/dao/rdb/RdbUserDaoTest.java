package smartspace.dao.rdb;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import smartspace.data.UserEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RdbUserDaoTest {

    @Autowired
    private RdbUserDao rdbUserDao;

    private UserEntity userEntity;
    private ExpectedException thrown;

    @Before
    public void setUp() {
        userEntity = new UserEntity();
    }

    @Test
    public void testCreate() {
        UserEntity userEntity = rdbUserDao.create(this.userEntity);
        // Checking if username was set to the userEntity;
        assertNotNull("userEmail is null", userEntity.getUserEmail());
    }

    @Test
    public void testReadById() {
        UserEntity userEntity = rdbUserDao.create(this.userEntity);
        Optional<UserEntity> userEntityOpt = rdbUserDao.readById(userEntity.getUserEmail());
        assertTrue("Row was not created/found", userEntityOpt.isPresent());
    }

    @Test
    public void testReadAll() {
        //Clear db
        rdbUserDao.deleteAll();

        //Create 3 rows
        UserEntity userEntity1 = rdbUserDao.create(new UserEntity());
        UserEntity userEntity2 = rdbUserDao.create(new UserEntity());
        UserEntity userEntity3 = rdbUserDao.create(new UserEntity());

        // Read all the rows from db
        List<UserEntity> userEntities = rdbUserDao.readAll();

        assertEquals("Wrong row number", 3, userEntities.size());

        assertTrue("Row was not created/found.", userEntities.contains(userEntity1));
        assertTrue("Row was not created/found.", userEntities.contains(userEntity2));
        assertTrue("Row was not created/found.", userEntities.contains(userEntity3));
    }

    @Test
    public void testUpdate() {
        //Create a row
        UserEntity userEntity = rdbUserDao.create(this.userEntity);
        userEntity.setPoints(2000);
        rdbUserDao.update(userEntity);

        Optional<UserEntity> userEntityOpt = rdbUserDao.readById(userEntity.getUserEmail());
        userEntityOpt.ifPresent(userEntity1 -> assertEquals("Points were not updated", 2000, userEntity1.getPoints()));
    }

    @Test
    public void testUpdateIllegalRow() {
        //Create a row
        UserEntity userEntity = rdbUserDao.create(this.userEntity);
        // Change the id to a wrong id.
        userEntity.setUserEmail("blah blah");

        thrown.expectMessage("No user with this Email:");
        rdbUserDao.update(userEntity);
    }

    @Test
    public void testDeleteAll() {
        //Add initialized variables to db
        UserEntity userEntity1 = rdbUserDao.create(new UserEntity());
        UserEntity userEntity2 = rdbUserDao.create(new UserEntity());
        UserEntity userEntity3 = rdbUserDao.create(new UserEntity());

        rdbUserDao.deleteAll();

        assertEquals("Failed deleting all rows", 0, rdbUserDao.readAll().size());
    }
}