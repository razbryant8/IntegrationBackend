package smartspace.logic;


import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import smartspace.dao.EnhancedUserDao;
import smartspace.data.UserEntity;

import java.util.List;


@Service
public class UserServiceImp implements UserService {

    private EnhancedUserDao<String> userDao;


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
            user.setRole(user.getRole());          // not sure about that (line 33)
            return this.userDao
                    .create(user);
        } else {
            throw new RuntimeException("Invalid user input");
        }
    }

    // maybe we need to check only the smart space ->  user.getUserSmartspace(); ?? ;
    private boolean validate(UserEntity user) {
        return user.getRole() != null &&
                !user.getUsername().trim().isEmpty() &&
                !user.getAvatar().trim().isEmpty() &&
                !user.getUserEmail().trim().isEmpty() &&
                user.getPoints() >= 0.0;

    }
}