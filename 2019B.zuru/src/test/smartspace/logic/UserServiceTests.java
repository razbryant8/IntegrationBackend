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
import smartspace.dao.EnhancedUserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.data.util.EntityFactory;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"spring.profiles.active=default"})
public class UserServiceTests {
    private EnhancedUserDao<String> enhancedUserDao;
    private EntityFactory entityFactory;
    private UserServiceImp userService;
    private String currentSmartSpace;

    @Value("${spring.smartspace.name}")
    public void setCurrentSmartSpace(String currentSmartSpace) {
        this.currentSmartSpace = currentSmartSpace;
    }

    @Autowired
    public void setUserService(UserServiceImp userService) {
        this.userService = userService;
    }

    @Autowired
    public void setFactory(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    @Autowired
    public void setEnhancedDao(EnhancedUserDao<String> enhancedUserDao) {
        this.enhancedUserDao = enhancedUserDao;
    }

    @Before
    public void setUp() {
    }


    @After
    public void deleteDb() {
        this.enhancedUserDao.deleteAll();
    }


    @Test()
    public void checkGetAllUsersInEmptyPage() {
        // GIVEN empty DB

        // WHEN getAll users from empty page
        int size = 5;
        int page = 0;
        List<UserEntity> usersEntities = userService.getAll(size, page);

        // THEN the List is empty
        assertEquals(0, usersEntities.size());
    }


    @Test
    public void checkGetAllInPageWithOneUser() {
        // GIVEN The database contains one user
        UserEntity userEntity = enhancedUserDao.create(entityFactory.createNewUser("mail1", "smart1", "user1", "ava1", UserRole.ADMIN, 100));

        // WHEN getAll elements in an empty page
        int size = 5;
        int page = 0;
        List<UserEntity> userEntities = userService.getAll(size, page);

        // THEN the List contains exactly one element
        assertEquals(1, userEntities.size());
        assertThat(userEntities).usingElementComparatorOnFields("userSmartspace", "userEmail").contains(userEntity);
    }

    @Test
    public void checkGetUserByKey() {
        //GIVEN one user to insert db
        String mail = "mail1";
        String smartspace = "smart1";
        String key = mail + "#" + smartspace;
        UserEntity expectedEntity = enhancedUserDao.upsert(entityFactory.createNewUser(mail, smartspace, "user1", "ava1", UserRole.ADMIN, 100));

        //WHEN getUserByKey
        Optional<UserEntity> returnedOptEntity = enhancedUserDao.readById(key);
        UserEntity returnedEntity = returnedOptEntity.get();

        //THEN the user will be the user that inserted
        assertThat(returnedEntity).isEqualToComparingOnlyGivenFields(expectedEntity,
                "userSmartspace", "userEmail", "username", "avatar", "role", "points");


    }


    @Test()
    public void checkUserServiceStore() {

        // GIVEN Valid User Entity
        String mail = "mail1";
        String smartspace = "smart1";
        UserEntity[] userEntities = new UserEntity[1];
        UserEntity userEntity = entityFactory.createNewUser(mail, smartspace,
                "user1", "ava1", UserRole.ADMIN, 100);
        String userEntityId = userEntity.getKey();
        userEntities[0] = userEntity;
        // WHEN we store the user using UserService Logic
        userService.store(userEntities);

        // THEN the user entity is stored
        Optional<UserEntity> expectedUserEntity = this.enhancedUserDao.readById(userEntityId);
        assertThat(expectedUserEntity.get()).isEqualToComparingOnlyGivenFields(userEntity,
                "userSmartspace", "userEmail", "username", "avatar", "role", "points");
    }

    @Test()
    public void checkMultipyUsersStoreByAmount() {

        // GIVEN Valid User
        UserEntity[] userEntities = new UserEntity[3];
        userEntities[0] = entityFactory.createNewUser("mail1", "smart1",
                "user1", "ava1", UserRole.ADMIN, 100);
        userEntities[1] = entityFactory.createNewUser("mail2", "smart2",
                "user2", "ava2", UserRole.ADMIN, 200);
        userEntities[2] = entityFactory.createNewUser("mail3", "smart3",
                "user3", "ava3", UserRole.ADMIN, 300);

        // WHEN we store the users using UserService Logic
        userService.store(userEntities);

        // THEN the amount od user entities is stored

        int size = 5;
        int page = 0;
        List<UserEntity> usersEntities = userService.getAll(size, page);

        assertEquals(3, usersEntities.size());
    }


    @Test(expected = Throwable.class)
    public void checkValidateIlligalUserSmartspace() {
        // GIVEN Valid User Entity from this project
        UserEntity[] userEntities = new UserEntity[1];
        userEntities[0] = entityFactory.createNewUser("mail1", "2019b.zuru",
                "user1", "ava1", UserRole.ADMIN, 100);

        // WHEN we store the user entity using UserService Logic
        userService.store(userEntities);

        // THEN we expect Exception to be thrown because is the same smartspace that in our project

    }

    @Test(expected = Throwable.class)
    public void checkValidateIlligalNotAdminUserRole() {
        // GIVEN Valid User Entity from this project
        UserEntity[] userEntities = new UserEntity[1];

        userEntities[0] = entityFactory.createNewUser("mail1", "2019b.zuru",
                "user1", "ava1", UserRole.MANAGER, 100);

        // WHEN we store the user entity using UserService Logic
        userService.store(userEntities);

        // THEN we expect Exception to be thrown because is the not a admin

    }

    @Test(expected = Throwable.class)
    public void checkValidateIlligalNullserRole() {
        // GIVEN Valid User Entity from this project
        UserEntity[] userEntities = new UserEntity[1];
        userEntities[0] = entityFactory.createNewUser("mail1", "2019b.zuru",
                "user1", "ava1", null, 100);

        // WHEN we store the user entity using UserService Logic
        userService.store(userEntities);

        // THEN we expect Exception to be thrown because is the not a admin

    }

    @Test(expected = Throwable.class)
    public void checkValidateIlligalEmptyUsername() {
        // GIVEN Valid User Entity from this project
        UserEntity[] userEntities = new UserEntity[1];
        userEntities[0] = entityFactory.createNewUser("mail1", "2019b.zuru",
                "", "ava1", UserRole.ADMIN, 100);

        // WHEN we store the user entity using UserService Logic
        userService.store(userEntities);

        // THEN we expect Exception to be thrown because is the not a admin

    }

    @Test(expected = Throwable.class)
    public void checkValidateIlligalSpacesAvatar() {
        // GIVEN Valid User Entity from this project
        UserEntity[] userEntities = new UserEntity[1];

        userEntities[0] = entityFactory.createNewUser("mail1", "2019b.zuru",
                "user1", "      ", UserRole.ADMIN, 100);

        // WHEN we store the user entity using UserService Logic
        userService.store(userEntities);

        // THEN we expect Exception to be thrown because is the not a admin

    }

    @Test(expected = Throwable.class)
    public void checkValidateIlligalNegetivePoints() {
        // GIVEN Valid User Entity from this project
        UserEntity[] userEntities = new UserEntity[1];

        userEntities[0] = entityFactory.createNewUser("mail1", "2019b.zuru",
                "user1", ":-}", UserRole.ADMIN, -550);

        // WHEN we store the user entity using UserService Logic
        userService.store(userEntities);

        // THEN we expect Exception to be thrown because is the not a admin

    }
}
