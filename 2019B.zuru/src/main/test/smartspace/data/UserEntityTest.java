package smartspace.data;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserEntityTest {

    private UserEntity userEntity;

    @Before
    public void setUp(){
        // Thanks to before annotation - this happens before every test
        userEntity = new UserEntity();
    }


    // Test example
    @Test
    public void testSetPoints(){
        userEntity.setPoints(20);

        assertEquals("Wrong points value" , 20 , userEntity.getPoints());
    }
}