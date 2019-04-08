package smartspace.dao.rdb;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import smartspace.dao.UserDao;
import smartspace.data.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
public class RdbUserDao implements UserDao<String> {

    private UserCrud userCrud;
    private IdGeneratorCrud idGeneratorCrud;


    @Autowired
    public RdbUserDao(UserCrud userCrud, IdGeneratorCrud idGeneratorCrud) {
        this.userCrud = userCrud;
        this.idGeneratorCrud = idGeneratorCrud;


    }


    @Override
    @Transactional
    public UserEntity create(UserEntity userEntity) {
        IdGenerator nextId = this.idGeneratorCrud.save(new IdGenerator());
        userEntity.setUserEmail("" + nextId.getNextId() + userEntity.getUserSmartspace());
        this.idGeneratorCrud.delete(nextId);

        return this.userCrud.save(userEntity);

    }

    @Override
    @Transactional
    public Optional<UserEntity> readById(String userKey) {
        return this.userCrud.findById(userKey);
    }


    @Override
    @Transactional
    public List<UserEntity> readAll() {
        List<UserEntity> userEntityList = new ArrayList<>();
        this.userCrud.findAll().forEach(userEntityList::add);
        return userEntityList;
    }

    @Override
    @Transactional
    public void update(UserEntity userEntity) {

        UserEntity existing = this.readById(userEntity.getUserEmail())
                .orElseThrow(() -> new RuntimeException("No user with this email: " + userEntity.getUserEmail() + "exists!"));

        // Patching
        if (userEntity.getAvatar() != null) {
            existing.setAvatar(userEntity.getAvatar());
        }

        if (userEntity.getRole() != null) {
            existing.setRole(userEntity.getRole());
        }
        // need to ask Eyal about that
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
}
