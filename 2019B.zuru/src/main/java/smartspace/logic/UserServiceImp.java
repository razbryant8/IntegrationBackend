package smartspace.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartspace.dao.EnhancedUserDao;
import smartspace.dao.UserNotFoundException;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    public UserEntity[] store(UserEntity[] users) {
        UserEntity[] usersEntities = new UserEntity[users.length];
        for(int i=0; i<usersEntities.length;i++) {
            if (validate(users[i])) {
                usersEntities[i] =  this.userDao.upsert(users[i]);
            } else {
                throw new RuntimeException("Invalid user input");
            }
        }
        return usersEntities;
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
        if (userDao.readById(user.getKey()) != null) {
            return this.userDao.readById(user.getKey());
        } else {
            throw new UserNotFoundException("No user with this key: "
                    + user.getKey());

        }


    }


    private boolean validate(UserEntity user) {
        return user.getRole() != null &&
                !user.getUsername().trim().isEmpty() &&
                !user.getUserSmartspace().equals(this.currentSmartspace) &&
                !user.getAvatar().trim().isEmpty() &&
                !user.getUserEmail().trim().isEmpty() &&
                user.getPoints() >= 0.0;

    }

    @Value("${spring.smartspace.name}")
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
    public void update(String userSmartspace, String userEmail, UserEntity updateDetails) {
        Optional<UserEntity> ifExcistUser = getUserByMailAndSmartSpace(userEmail, userSmartspace);
        Optional<UserEntity> userToUpdate = getUserByMailAndSmartSpace(updateDetails.getUserEmail(), updateDetails.getUserSmartspace());
        if (ifExcistUser.isPresent() && userToUpdate.isPresent()) {
            UserEntity needToUpdateUserEntity = new UserEntity();

            needToUpdateUserEntity.setKey(updateDetails.getKey());
            needToUpdateUserEntity.setUsername(updateDetails.getUsername());
            needToUpdateUserEntity.setAvatar(updateDetails.getAvatar());
            needToUpdateUserEntity.setRole(updateDetails.getRole());
            needToUpdateUserEntity.setPoints(userToUpdate.get().getPoints());

            this.userDao.update(needToUpdateUserEntity);
        } else throw new UserNotFoundException();

    }


    private boolean CheckingEmailAndRole(UserEntity user) {

        if ((user.getRole().equals(UserRole.ADMIN) ||
                user.getRole().equals(UserRole.MANAGER) ||
                user.getRole().equals(UserRole.PLAYER)) &&
                (user.getUserSmartspace().equals(this.currentSmartspace))
                && validateEmailAddress(user.getUserEmail())) {
            return true;
        }
        return false;


    }

    //check if emil is validate email with regular expression
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmailAddress(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
}
