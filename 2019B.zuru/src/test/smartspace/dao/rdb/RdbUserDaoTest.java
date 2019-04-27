package smartspace.dao.rdb;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RdbUserDaoTest {

    @Autowired
    private RdbUserDao rdbUserDao;

    private UserEntity userEntity;

    private String smartspace;

    @Value("${spring.application.name}")
    public void setSmartspace(String smartspace) {
        this.smartspace = smartspace;
    }

    @Before
    public void setUp() {
        rdbUserDao.deleteAll(); userEntity = new UserEntity();
    }

    @Test
    public void testCreate() {
        UserEntity userEntity = rdbUserDao.create(this.userEntity);
        // Checking if username was set to the userEntity;
        assertNotNull("userEmail is null", userEntity.getUserEmail());
    }

    @Test
    public void testReadById() {
        String expectedId = "email#" + this.smartspace;
        this.userEntity.setUserEmail("email");
        this.userEntity.setRole(UserRole.ADMIN);
        this.userEntity.setPoints(15);
        this.userEntity.setAvatar("haha");
        this.userEntity.setUsername("zur");
        UserEntity userEntity = rdbUserDao.create(this.userEntity);
        Optional<UserEntity> userEntityOpt = rdbUserDao.readById(expectedId);
        assertTrue("Row was not created/found", userEntityOpt.isPresent());
    }

    @Test
    public void testReadAll() {
        //Clear db
        rdbUserDao.deleteAll();

        //Create 3 rows
        UserEntity userEntity1 = rdbUserDao.create(new UserEntity("mail1","smartspace1","user1","ava1", UserRole.ADMIN,100));
        UserEntity userEntity2 = rdbUserDao.create(new UserEntity("mail12","smartspace12","user1","ava1", UserRole.ADMIN,100));
        UserEntity userEntity3 = rdbUserDao.create(new UserEntity("mail123","smartspace123","user1","ava1", UserRole.ADMIN,100));

        // Read all the rows from db
        List<UserEntity> userEntities = rdbUserDao.readAll();

        assertEquals("Wrong row number", 3, userEntities.size());

        assertThat(userEntities).usingElementComparatorOnFields("userEmail").contains(userEntity1, userEntity2, userEntity3);
    }

    @Test
    public void testUpdate() {
        //Create a row
        UserEntity testEntity = new UserEntity("mail1","smartspace1","user1","ava1", UserRole.ADMIN,100);
        testEntity.setKey(testEntity.getUserEmail()+"#"+testEntity.getUserSmartspace());
        UserEntity userEntity = rdbUserDao.create(testEntity);
        userEntity.setPoints(2000);
        rdbUserDao.update(userEntity);

        Optional<UserEntity> userEntityOpt = rdbUserDao.readById(testEntity.getUserEmail());
        userEntityOpt.ifPresent(userEntity1 -> assertEquals("Points were not updated", 2000, userEntity1.getPoints()));
    }

    @Test(expected = Throwable.class)
    public void testUpdateIllegalRow() {
        //Create a row
        UserEntity userEntity = rdbUserDao.create(this.userEntity);
        // Change the id to a wrong id.
        userEntity.setUserEmail("blah blah");

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