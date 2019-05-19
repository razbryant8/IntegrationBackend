package smartspace.dao;

import smartspace.data.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserDao<String> {

    public UserEntity create(UserEntity userEntity);

    public Optional<UserEntity> readById(String id);

    public List<UserEntity> readAll();

    public void update(UserEntity userEntity);

    public void deleteAll();

}
