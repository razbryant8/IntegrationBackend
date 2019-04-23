package smartspace.logic;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import smartspace.dao.EnhancedUserDao;
import smartspace.data.UserEntity;

import java.util.List;
import java.util.Optional;


@Service
public class UserServiceImp implements UserService {

    private EnhancedUserDao<String> userDao;
    private String smartspace;


    @Autowired
    public UserServiceImp(EnhancedUserDao<String> userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<UserEntity> getAll(int size, int page) {
        return this.userDao
                .readAll(size, page, "Role");
    }

    @Override
    @Transactional
    public UserEntity store(UserEntity user) {
        if (validate(user)) {
            user.setRole(user.getRole());          // not sure about that
            return this.userDao
                    .create(user);
        } else {
            throw new RuntimeException("Invalid user input");
        }
    }

    public Optional<UserEntity> getUserByKey(String key) {
        return this.userDao
                .readById(key);
    }

    @Override
    // for import users from another db
    public List<UserEntity> getUsersByEmailAndSmartspace(String email, String smartspace, int size, int page) {
        if(this.smartspace != smartspace){
            return this.userDao.getUsersByEmailAndSmartspace(email, smartspace, size, page, "");
        }
        else{
            throw  new RuntimeException("Invalid smartspace value!");
        }

    }

    // maybe we need to check only the smartspace ->  user.getUserSmartspace(); ?? ;
    private boolean validate(UserEntity user) {
        return user.getRole() != null &&
                !user.getUsername().trim().isEmpty() &&
                !user.getAvatar().trim().isEmpty() &&
                !user.getUserEmail().trim().isEmpty() &&
                user.getPoints() >= 0.0;

    }

    @Value("${spring.application.name}")
    public void setSmartspace(String smartspace) {
        this.smartspace = smartspace;
    }
}
