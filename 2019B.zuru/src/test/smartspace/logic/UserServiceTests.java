package smartspace.logic;


import org.junit.After;
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
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"spring.profiles.active=default"})
public class UserServiceTests {
    private EnhancedUserDao<String> enhancedUserDao;
    private EntityFactory entityFactory;
    private UserServiceImp userService;
    private String currentSmartSpace;

    @Value("${spring.application.name}")
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

    @After
    public void deleteDb(){
        this.enhancedUserDao.deleteAll();
    }

    @Test
    public void checkGetAllInPageWithOneUser(){
        // GIVEN The database contains one user
        UserEntity userEntity = enhancedUserDao.create(entityFactory.createNewUser("mail1","smart1","user1","ava1", UserRole.ADMIN,100));

        // WHEN getAll elements in an empty page
        int size = 5;
        int page = 0;
        List<UserEntity> userEntities = userService.getAll(size,page);

        // THEN the List contains exactly one element
        assertEquals(1,userEntities.size());
        assertThat(userEntities).usingElementComparatorOnFields("userSmartspace","userEmail").contains(userEntity);
    }

    @Test
    public void checkGetUserByKey(){
        //GIVEN one user to insert db
        String mail = "mail1";
        String smartspace = "smart1";
        UserEntity expectedEntity = enhancedUserDao.create(entityFactory.createNewUser(mail,currentSmartSpace,"user1","ava1", UserRole.ADMIN,100));

        //WHEN getUserByKey
        Optional<UserEntity> returnedOptEntity = enhancedUserDao.readById(expectedEntity.getKey());
        UserEntity returnedEntity = returnedOptEntity.get();

        //THEN the user will be the user that inserted
        assertThat(returnedEntity).isEqualToComparingOnlyGivenFields(expectedEntity,
                "userSmartspace","userEmail","username","avatar","role","points");


    }



}
