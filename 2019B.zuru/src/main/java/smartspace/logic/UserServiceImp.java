package smartspace.logic;

import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartspace.dao.EnhancedUserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;

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

    @Transactional(readOnly = true)
    public Optional<UserEntity> getUserByMailAndSmartSpace(String email, String smartSpace) {
        UserEntity user = new UserEntity();
        user.setUserSmartspace(smartSpace);
        user.setUserEmail(email);
        return this.userDao.readById(user.getKey());

    }


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

    @Override
    public UserEntity create(UserEntity user) {

        if (CheckingEmailAndRole(user)) {
            return this.userDao.create(user);

        }
        return null;
    }




    @Override
    // maybe we need to add AOP annotation to this code
    public void update(String userSmartspace, String userEmail, UserEntity user) {
        user.setUserSmartspace(userSmartspace);
        user.setUserEmail(userEmail);
        this.userDao.update(user);

    }

    //need to complete how to checking if user mail contain "@." with EmailValidator
    // Eyal says that he gives some jar that does it -> I thing that is the jar.

    private boolean CheckingEmailAndRole(UserEntity user) {

        EmailValidator validator = new EmailValidator();

        if ((user.getRole().equals(UserRole.ADMIN) ||
                user.getRole().equals(UserRole.MANAGER) ||
                user.getRole().equals(UserRole.PLAYER)) &&
               (user.getUserSmartspace().equals(this.currentSmartspace))){
               // &&(validator.isValid("@.",user.getUserEmail()))) {
            return true;
        }
        return false;


    }
}
