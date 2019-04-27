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
    private String currentSmartspace;


    @Autowired
    public UserServiceImp(EnhancedUserDao<String> userDao) {
        this.userDao = userDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> getAll(int size, int page) {
        return this.userDao
                .readAll(size, page, "Role");
    }

    @Override
    public UserEntity store(UserEntity user) {
        if (validate(user)) {
            return this.userDao
                    .upsert(user);
        } else {
            throw new RuntimeException("Invalid user input");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserEntity> getUserByKey(String key) {
        return this.userDao
                .readById(key);
    }

    public Optional<UserEntity> getUserByMailAndSmartSpace(String email, String smartSpace) {
        UserEntity user = new UserEntity();
        user.setUserSmartspace(smartSpace);
        user.setUserEmail(email);
        return this.userDao.readById(user.getKey());

    }

    // maybe we need to check only the smartspace ->  user.getUserSmartspace(); ?? ;
    private boolean validate(UserEntity user) {
        return user.getRole() != null &&
                !user.getUsername().trim().isEmpty() &&
                !user.getUserSmartspace().equals(this.currentSmartspace) &&
                !user.getAvatar().trim().isEmpty() &&
                !user.getUserEmail().trim().isEmpty() &&
                user.getPoints() >= 0.0;

    }

    @Value("${spring.application.name}")
    public void setSmartspace(String currentSmartspace) {
        this.currentSmartspace = currentSmartspace;
    }

    @Override
    public String getCurrentSmartspace() {
        return currentSmartspace;
    }
}
