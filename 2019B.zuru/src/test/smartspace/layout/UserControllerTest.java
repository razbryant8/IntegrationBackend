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
    private String baseUrl;
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

    @Value("${spring.application.name}")
    public void setMySmartspace(String currentSmartspace) {
        this.currentSmartspace = currentSmartspace;
    }

    @PostConstruct
    public void init() {
        this.baseUrl = "http://localhost:" + port + "/smartspace/admin/users/";
        this.restTemplate = new RestTemplate();
    }

    @Before
    public void setUp() {
        String mail = "admin_store_test@mail";
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
        ArrayList<UserBoundary> UserBoundaryArr = new ArrayList<UserBoundary>();

        UserBoundary newUserBoundary = new UserBoundary();
        newUserBoundary.setUserKey(new UserKeyType("test_import@mail", "2019b.test"));
        newUserBoundary.setUsername("user2");
        newUserBoundary.setAvatar(":-{");
        newUserBoundary.setRole("PLAYER");
        newUserBoundary.setPoints(200);

        UserBoundaryArr.add(newUserBoundary);

        UserBoundary[] actualResult = this.restTemplate
                .postForObject(
                        this.baseUrl + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail(),
                        UserBoundaryArr,
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
        ArrayList<UserBoundary> UserBoundaryArr = new ArrayList<UserBoundary>();

        UserBoundary newUserBoundary1 = new UserBoundary();
        newUserBoundary1.setUserKey(new UserKeyType("test_import1@mail", "2019b.test"));
        newUserBoundary1.setUsername("user2");
        newUserBoundary1.setAvatar(":-{");
        newUserBoundary1.setRole("PLAYER");
        newUserBoundary1.setPoints(200);

        UserBoundaryArr.add(newUserBoundary1);

        UserBoundary newUserBoundary2 = new UserBoundary();
        newUserBoundary2.setUserKey(new UserKeyType("test_import2@mail", "2019b.test"));
        newUserBoundary2.setUsername("user3");
        newUserBoundary2.setAvatar(" ^ ^ ");
        newUserBoundary2.setRole("MANAGER");
        newUserBoundary2.setPoints(1000);

        UserBoundaryArr.add(newUserBoundary2);

        UserBoundary[] actualResult = this.restTemplate
                .postForObject(
                        this.baseUrl + adminUser.getUserSmartspace() + "/" + adminUser.getUserEmail(),
                        UserBoundaryArr,
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
                ("crash@mail","2019b.zuru","needToCrash","Q",UserRole.ADMIN,400);


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
        enhancedUserDao.create(new UserEntity
                ("user1@mail","2019b.nba","user1","Q1",UserRole.PLAYER,40));
        UserEntity userEntity2 = enhancedUserDao.create(new UserEntity
                ("user2@mail","2019b.nba","user2","Q2",UserRole.MANAGER,4000));
        UserEntity userEntity3 = enhancedUserDao.create(new UserEntity
                ("user3@mail","2019b.nba","user3","Q3",UserRole.MANAGER,200));
        enhancedUserDao.create(new UserEntity
                ("user4@mail","2019b.nba","user4","Q4",UserRole.PLAYER,100));


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
                ("user1@mail","2019b.nba","user1","Q1",UserRole.PLAYER,40));
        UserEntity userEntity2 = enhancedUserDao.create(new UserEntity
                ("user2@mail","2019b.nba","user2","Q2",UserRole.MANAGER,4000));
        UserEntity userEntity3 = enhancedUserDao.create(new UserEntity
                ("user3@mail","2019b.nba","user3","Q3",UserRole.MANAGER,200));
        UserEntity userEntity4 = enhancedUserDao.create(new UserEntity
                ("user4@mail","2019b.nba","user4","Q4",UserRole.PLAYER,100));


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
}
