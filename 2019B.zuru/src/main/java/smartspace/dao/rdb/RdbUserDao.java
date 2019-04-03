package smartspace.dao.rdb;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import smartspace.dao.UserDao;
import smartspace.data.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;


@Repository
public class RdbUserDao implements UserDao<String> {

    private UserCrud userCrud;
    private AtomicLong nextUserId;

    @Autowired
    public RdbUserDao(UserCrud userCrud) {
        this.userCrud = userCrud;
        this.nextUserId = new AtomicLong(1L);
    }


    @Override
    @Transactional
    public UserEntity create(UserEntity userEntity) {
        userEntity.setUserEmail("2019b.zuru" + nextUserId.getAndIncrement() + userEntity.getUserSmartspace());
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
        if (this.userCrud.existsById(userEntity.getUserEmail())) {
            this.userCrud.save(userEntity);
        } else {
            throw new RuntimeException("No user with this email: " + userEntity.getUserEmail() + "exists!");
        }

    }

    @Override
    @Transactional
    public void deleteAll() {
        this.userCrud.deleteAll();

    }
}
