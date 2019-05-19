package smartspace.layout;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import smartspace.dao.EnhancedUserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.data.util.EntityFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.profiles.active=default"})
public class UserControllerTest {

    private EnhancedUserDao enhancedUserDao;
    private EntityFactory entityFactory;
    private int port;
    private String currentSmartspace;
    private RestTemplate restTemplate;
    private String baseUrl, userRestAPIurl, loginUrlAddition;
    private UserEntity adminUser;


    @Autowired
    public void setEnhancedUserDao(EnhancedUserDao enhancedUserDao) {
        this.enhancedUserDao = enhancedUserDao;
    }

    @Autowired
    public void setEntityFactory(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    @LocalServerPort
    public void setPort(int port) {
        this.port = port;
    }

    @Value("${spring.smartspace.name}")
    public void setMySmartspace(String currentSmartspace) {
        this.currentSmartspace = currentSmartspace;
    }

    @PostConstruct
    public void init() {
        this.baseUrl = "http://localhost:" + port + "/smartspace/admin/users/";
        this.userRestAPIurl = "http://localhost:" + port + "/smartspace/users";
        this.loginUrlAddition = this.userRestAPIurl + "/login/";
        this.restTemplate = new RestTemplate();
    }

    @Before
    public void setUp() {
        String mail = "admin_store_test@mail.com";
        String smartspace = "2019b.NotOurSmartspace";
        adminUser = enhancedUserDao.upsert(entityFactory.createNewUser
                (mail, smartspace, "test", ":-}", UserRole.ADMIN, 100));
    }

    @After
    public void tearDown() {
        this.enhancedUserDao.deleteAll();
        adminUser = null;
    }


    @Test(expected = Throwable.class)
    public void testIllegalImportPermissions() {
        // GIVEN the database contain one Admin user


        // WHEN a user with illegal permissions try to Import using REST API
        UserBoundary[] userBoundaries = new UserBoundary[1];
        userBoundaries[0] = new UserBoundary();
        userBoundaries[0].setUserKey(new UserKeyType("test_import@mail", "2019b.test"));
        userBoundaries[0].setUsername("user2");
        userBoundaries[0].setAvatar(":-{");
        userBoundaries[0].setRole("PLAYER");
        userBoundaries[0].setPoints(200);

        this.restTemplate.postForEntity(this.baseUrl + "badSmartSpace/badEmail",
                userBoundaries,
                UserBoundary[].class);

        // THEN exception is thrown
    }


    @Test(expected = Throwable.class)
    public void testIllegalExportPermissions() {
        // GIVEN the database contain one Admin user

        // WHEN someone that is not admin Export using REST API
        int page = 0;
        int size = 5;
        this.restTemplate
                .getForObject(
                        this.baseUrl + "badSmartSpace/BadEmail?size={size}&page={page}",
                        ElementBoundary[].class, size, page);

        // THEN exception is thrown
    }


    @Test
    public void validOneUserImport() {
        // GIVEN the database contain one Admin user

        // WHEN someone that is Import using REST API
        ArrayList<UserBoundary> UserBoundryArr = new ArrayList<UserBoundary>();

        UserBoundary newUserBoundary = new UserBoundary();
        newUserBoundary.setUserKey(new UserKeyType("test_import@mail", "2019b.test"));
        newUserBoundary.setUsername("user2");
        newUserBoundary.setAvatar(":-{");
        newUserBoundary.setRole("PLAYER");
        newUserBoundary.setPoints(200);

        UserBoundryArr.add(newUserBoundary);

        UserBoundary[] actualResult = this.restTemplate
                .postForObject(
                        this.baseUrl + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail(),
                        UserBoundryArr,
                        UserBoundary[].class);

        // THEN the database contain those two users (admin + new user)
        List<UserEntity> actualInDB = this.enhancedUserDao.readAll();
        assertThat(actualInDB).hasSize(2);
        assertThat(actualInDB).usingElementComparatorOnFields("key").contains(actualResult[0].convertToEntity());
    }


    @Test
    public void validTwoUserImport() {
        // GIVEN the database contain one Admin user

        // WHEN someone that is Import using REST API
        ArrayList<UserBoundary> UserBoundryArr = new ArrayList<UserBoundary>();

        UserBoundary newUserBoundary1 = new UserBoundary();
        newUserBoundary1.setUserKey(new UserKeyType("test_import1@mail", "2019b.test"));
        newUserBoundary1.setUsername("user2");
        newUserBoundary1.setAvatar(":-{");
        newUserBoundary1.setRole("PLAYER");
        newUserBoundary1.setPoints(200);

        UserBoundryArr.add(newUserBoundary1);

        UserBoundary newUserBoundary2 = new UserBoundary();
        newUserBoundary2.setUserKey(new UserKeyType("test_import2@mail", "2019b.test"));
        newUserBoundary2.setUsername("user3");
        newUserBoundary2.setAvatar(" ^ ^ ");
        newUserBoundary2.setRole("MANAGER");
        newUserBoundary2.setPoints(1000);

        UserBoundryArr.add(newUserBoundary2);

        UserBoundary[] actualResult = this.restTemplate
                .postForObject(
                        this.baseUrl + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail(),
                        UserBoundryArr,
                        UserBoundary[].class);

        // THEN the database contain those two users (admin + new user)
        List<UserEntity> actualInDB = this.enhancedUserDao.readAll();
        assertThat(actualInDB).hasSize(3);
        assertThat(actualInDB).usingElementComparatorOnFields("key").contains(actualResult[0].convertToEntity());
    }


    @Test(expected = Throwable.class)
    public void testImportUsersFromCurrentSmartSpace() {
        // GIVEN the database contain one Admin user and we create one more user from our project smartspace
        UserEntity importUser = new UserEntity
                ("crash@mail", "2019b.zuru", "needToCrash", "Q", UserRole.ADMIN, 400);


        // WHEN we try to import a user from the same smartspace of our project.
        UserBoundary[] userBoundaries = new UserBoundary[1];
        userBoundaries[0] = new UserBoundary();
        userBoundaries[0].setUserKey(new UserKeyType());
        userBoundaries[0].setUsername("userrr");
        userBoundaries[0].setRole("PLAYER");
        userBoundaries[0].setAvatar(":P");
        userBoundaries[0].setPoints(500);

        this.restTemplate.postForObject(
                this.baseUrl + importUser.getUserSmartspace() + "/" + importUser.getUserEmail(),
                userBoundaries,
                UserBoundary[].class);

        // THEN Exception is thrown
    }


    @Test
    public void testExportUsingPagination() {
        // GIVEN the database contain one Admin user and two elements
        UserEntity userEntity1 = enhancedUserDao.create(new UserEntity
                ("user1@mail", "2019b.nba", "user1", "Q1", UserRole.PLAYER, 40));
        UserEntity userEntity2 = enhancedUserDao.create(new UserEntity
                ("user2@mail", "2019b.nba", "user2", "Q2", UserRole.MANAGER, 4000));
        UserEntity userEntity3 = enhancedUserDao.create(new UserEntity
                ("user3@mail", "2019b.nba", "user3", "Q3", UserRole.MANAGER, 200));
        UserEntity userEntity4 = enhancedUserDao.create(new UserEntity
                ("user4@mail", "2019b.nba", "user4", "Q4", UserRole.PLAYER, 100));


        // WHEN I get all messages using page 1 and size 2
        int page = 1;
        int size = 2;
        UserBoundary[] result =
                this.restTemplate
                        .getForObject(
                                this.baseUrl + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail() + "?size={size}&page={page}",
                                UserBoundary[].class,
                                size, page);

        // THEN the result contains 2 messages of the messages inserted to the database
        assertThat(result).hasSize(2);
        assertThat(result).usingElementComparatorOnFields("key").contains(new UserBoundary(userEntity2));
        assertThat(result).usingElementComparatorOnFields("key").contains(new UserBoundary(userEntity3));
    }

    @Test
    public void testExportingWithDefaultPagination() {
        // GIVEN the database contain one Admin user and two elements
        UserEntity userEntity1 = enhancedUserDao.create(new UserEntity
                ("user1@mail", "2019b.nba", "user1", "Q1", UserRole.PLAYER, 40));
        UserEntity userEntity2 = enhancedUserDao.create(new UserEntity
                ("user2@mail", "2019b.nba", "user2", "Q2", UserRole.MANAGER, 4000));
        UserEntity userEntity3 = enhancedUserDao.create(new UserEntity
                ("user3@mail", "2019b.nba", "user3", "Q3", UserRole.MANAGER, 200));
        UserEntity userEntity4 = enhancedUserDao.create(new UserEntity
                ("user4@mail", "2019b.nba", "user4", "Q4", UserRole.PLAYER, 100));


        // WHEN I get all messages using page 1 and size 2
        UserBoundary[] result =
                this.restTemplate
                        .getForObject(
                                this.baseUrl + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail(),
                                UserBoundary[].class);

        // THEN the result contains 2 messages of the messages inserted to the database
        assertThat(result).hasSize(5);
        assertThat(result).usingElementComparatorOnFields("key").contains(new UserBoundary(adminUser));
        assertThat(result).usingElementComparatorOnFields("key").contains(new UserBoundary(userEntity1));
        assertThat(result).usingElementComparatorOnFields("key").contains(new UserBoundary(userEntity2));
        assertThat(result).usingElementComparatorOnFields("key").contains(new UserBoundary(userEntity3));
        assertThat(result).usingElementComparatorOnFields("key").contains(new UserBoundary(userEntity4));
    }


    /*
     * Test For User REST API
     */

    @Test
    public void testValidCreateNewUserForm() {
        // GIVEN the database empty
        this.enhancedUserDao.deleteAll();

        // WHEN create new user with newUserFormBoundary
        NewUserFormBoundary newUserForm = new NewUserFormBoundary("test@this.now", "test1", "PLAYER", ":-)");
        UserBoundary createdUserNewForm = this.restTemplate
                .postForObject(this.userRestAPIurl,
                        newUserForm
                        , UserBoundary.class);

        //THEN the only new user will be in the DB
        Optional<UserEntity> rv = this.enhancedUserDao.readById(newUserForm.getEmail() + "#2019b.zuru");

        if (rv.isPresent()) {
            UserEntity rvEntity = rv.get();
            assertThat(rvEntity).isEqualToComparingOnlyGivenFields(newUserForm.convertToEntity(),
                    "key", "username", "avatar", "role", "points");
        }
    }

    @Test(expected = Throwable.class)
    public void testInvalidCreateNewUserFormWithInvalidEmail() {
        // GIVEN the database empty
        this.enhancedUserDao.deleteAll();

        // WHEN create new user with newUserFormBoundary with invalid email address
        NewUserFormBoundary newUserForm = new NewUserFormBoundary("test_this.now", "test1", "PLAYER", ":-)");
        this.restTemplate
                .postForObject(this.userRestAPIurl,
                        newUserForm
                        , UserBoundary.class);

        //THEN Exception will be thrown
    }


    @Test
    public void testGetUserByExcistUser() {
        // GIVEN the database with one user (admin user by default)


        // WHEN we perform GET by the default user to himself
        String mail = "admin_store_test@mail.com";
        String smartspace = "2019b.NotOurSmartspace";
        UserBoundary expectedStoredAdminUser = this.restTemplate
                .getForObject(this.loginUrlAddition + smartspace + "/" + mail, UserBoundary.class);

        //THEN we get the stored admin user
        UserEntity expectedStoredAdminUserEntity = expectedStoredAdminUser.convertToEntity();
        assertThat(expectedStoredAdminUserEntity).isEqualToComparingOnlyGivenFields(this.adminUser,
                "key", "username", "avatar", "role", "points");
    }

    @Test
    public void testGetNotExcistUser() {
        // GIVEN the database with one user (admin user by default)


        // WHEN we perform GET by the default user to wrong user details
        String mail = "not@excist.mail";
        String smartspace = "2019b.NotOurSmartspace";
        UserBoundary expectedStoredAdminUser = this.restTemplate
                .getForObject(this.loginUrlAddition + smartspace + "/" + mail, UserBoundary.class);

        //THEN the returned value is null
        assertThat(expectedStoredAdminUser).isEqualTo(null);
    }

    @Test
    public void testCreateUserAndThenGetThisUserAllWithUserRestApi() {
        // GIVEN the default user DB


        // WHEN create new user with newUserFormBoundary and get with user REST API
        String mail = "test@this.now";
        String ourSmartspace = "2019b.zuru";
        NewUserFormBoundary newUserForm = new NewUserFormBoundary(mail, "test1", "PLAYER", ":-)");
        UserEntity cu = newUserForm.convertToEntity();
        UserBoundary createdUserNewForm = this.restTemplate
                .postForObject(this.userRestAPIurl,
                        newUserForm
                        , UserBoundary.class);

        UserBoundary expectedStoredAdminUser = this.restTemplate
                .getForObject(this.loginUrlAddition + ourSmartspace + "/" + mail, UserBoundary.class);

        //THEN
        assertThat(expectedStoredAdminUser.convertToEntity()).isEqualToComparingOnlyGivenFields(cu,
                "key", "username", "avatar", "role", "points");
    }

    @Test
    public void testPutNewRoleWithUserRestApi() {
        //GIVEN the default db with the admin user

        //WHEN we change his role to PLAYER with user rest api
        String mail = "admin_store_test@mail.com";
        String smartspace = "2019b.NotOurSmartspace";
        String newRole = "PLAYER";
        UserBoundary updateRoleBoundary = new UserBoundary();
        updateRoleBoundary.setRole(newRole);
        updateRoleBoundary.setUserKey(new UserKeyType(mail, smartspace));

        this.restTemplate.put(this.loginUrlAddition + smartspace + "/" + mail, updateRoleBoundary);

        //THAN his role will changed to PLAYER
        UserBoundary rv = this.restTemplate.getForObject(this.loginUrlAddition + smartspace + "/" + mail, UserBoundary.class);

        assertThat(rv.convertToEntity().getRole()).isEqualByComparingTo(UserRole.PLAYER);

    }

    @Test(expected = Throwable.class)
    public void testPutNewRoleWithUserRestApiToInvalidUserInDB() {
        //GIVEN the default db with the admin user

        //WHEN we change his role to PLAYER with user rest api
        String mail = "admin_store_test@mail.com";
        String smartspace = "2019b.NotOurSmartspace";
        String newRole = "PLAYER";
        UserBoundary updateRoleBoundary = new UserBoundary();
        updateRoleBoundary.setRole(newRole);
        updateRoleBoundary.setUserKey(new UserKeyType(mail, "2019b.notExcistSmartspace"));

        this.restTemplate.put(this.loginUrlAddition + smartspace + "/" + mail, updateRoleBoundary);

        // THEN Exception is thrown

    }

    @Test
    public void testPutNewUserNameWithUserRestApi() {
        //GIVEN the default db with the admin user

        //WHEN we change his username to abrakadbra with user rest api

        String mail = this.adminUser.getUserEmail();
        String smartspace = this.adminUser.getUserSmartspace();
        String newUserName = "abrakadbra";
        UserBoundary updateUsernameBoundary = new UserBoundary(this.adminUser);
        updateUsernameBoundary.setUsername(newUserName);

        this.restTemplate.put(this.loginUrlAddition + smartspace + "/" + mail, updateUsernameBoundary);


        //THAN his username will changed to abrakadbra
        UserBoundary rv = this.restTemplate.getForObject(this.loginUrlAddition + smartspace + "/" + mail, UserBoundary.class);

        assertThat(rv.convertToEntity()).isEqualToComparingOnlyGivenFields(updateUsernameBoundary, "username");
        assertThat(rv.getUsername()).isEqualTo(newUserName);

    }

    @Test
    public void testPutNewAvatarWithUserRestApi() {
        //GIVEN the default db with the admin user

        //WHEN we change his avatar to (8) with user rest api
        String mail = this.adminUser.getUserEmail();
        String smartspace = this.adminUser.getUserSmartspace();
        String newAvatar = "(8)";
        UserBoundary updateAvatarBoundary = new UserBoundary(this.adminUser);
        updateAvatarBoundary.setAvatar(newAvatar);

        this.restTemplate.put(this.loginUrlAddition + smartspace + "/" + mail, updateAvatarBoundary);

        //THAN his avatar will changed to (8)
        UserBoundary rv = this.restTemplate.getForObject(this.loginUrlAddition + smartspace + "/" + mail, UserBoundary.class);

        assertThat(rv.convertToEntity()).isEqualToComparingOnlyGivenFields(updateAvatarBoundary, "avatar");
        assertThat(rv.getAvatar()).isEqualTo(newAvatar);
    }

    @Test
    public void testTryPutNewAmountOfPointsWithUserRestApiAndStayTheSamePointsThatHave() {
        //GIVEN the default db with the admin user

        //WHEN we change his points to 500 with user rest api (he has 100 points)
        String mail = this.adminUser.getUserEmail();
        String smartspace = this.adminUser.getUserSmartspace();
        long points = 500;
        UserBoundary updatePointsBoundary = new UserBoundary(this.adminUser);
        updatePointsBoundary.setPoints(points);

        this.restTemplate.put(this.loginUrlAddition + smartspace + "/" + mail, updatePointsBoundary);

        //THAN his points will stay 100
        UserBoundary rv = this.restTemplate.getForObject(this.loginUrlAddition + smartspace + "/" + mail, UserBoundary.class);

        assertThat(rv.convertToEntity().getPoints()).isEqualTo(100);
    }

    @Test(expected = Throwable.class)
    public void testPutNewEmailWithUserRestApi() {
        //GIVEN the default db with the admin user

        //WHEN we change his email to change@this.email user rest api
        String mail = this.adminUser.getUserEmail();
        String smartspace = this.adminUser.getUserSmartspace();
        String newEmail = "change@this.email";
        UserBoundary updateEmailBoundary = new UserBoundary(this.adminUser);
        updateEmailBoundary.setUserKey(new UserKeyType(newEmail, this.adminUser.getUserSmartspace()));

        this.restTemplate.put(this.loginUrlAddition + smartspace + "/" + mail, updateEmailBoundary);

        //THAN will throw exception
    }

}
