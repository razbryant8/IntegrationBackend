package smartspace.dao.rdb;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import smartspace.dao.EnhancedUserDao;
import smartspace.data.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
public class RdbUserDao implements EnhancedUserDao<String> {

    private UserCrud userCrud;
    private String smartspace;


    @Autowired
    public RdbUserDao(UserCrud userCrud) {
        this.userCrud = userCrud;


    }

    @Value("${spring.application.name}")
    public void setSmartspace(String smartspace) {
        this.smartspace = smartspace;
    }


    @Override
    @Transactional
    public UserEntity create(UserEntity userEntity) {
        userEntity.setKey("" + userEntity.getUserEmail() + "#" + this.smartspace);
        return this.userCrud.save(userEntity);

    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserEntity> readById(String userKey) {
        return this.userCrud.findById(userKey);
    }


    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> readAll() {
        List<UserEntity> userEntityList = new ArrayList<>();
        this.userCrud.findAll().forEach(userEntityList::add);
        return userEntityList;
    }

    @Override
    @Transactional()
    public void update(UserEntity userEntity) {

        UserEntity existing = this.readById(userEntity.getKey())
                .orElseThrow(() -> new RuntimeException("No user with this key " + userEntity.getKey() + " is exists!"));

        // Patching
        if (userEntity.getUsername() != null) {
            existing.setUsername(userEntity.getUsername());
        }
        if (userEntity.getAvatar() != null) {
            existing.setAvatar(userEntity.getAvatar());
        }

        if (userEntity.getRole() != null) {
            existing.setRole(userEntity.getRole());
        }

        if (userEntity.getPoints() >= 0) {
            existing.setPoints(userEntity.getPoints());
        }

        this.userCrud.save(existing);


    }

    @Override
    @Transactional
    public void deleteAll() {
        this.userCrud.deleteAll();

    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> readAll(int size, int page) {

        return this.userCrud
                .findAll(PageRequest.of(page, size))
                .getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> readAll(int size, int page, String sortBy) {
        return this.userCrud
                .findAll(PageRequest.of(page, size, Direction.ASC, sortBy))
                .getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> getUserByRole(String role, int size, int page) {

        return this.userCrud
                .findAllByRoleLike(
                        "%" + role + "%",
                        PageRequest.of(page, size));
    }

    @Override
    public UserEntity upsert(UserEntity userEntity) {
        return userCrud.save(userEntity);
    }

}
