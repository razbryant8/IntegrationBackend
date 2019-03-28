package smartspace.dao;

import java.util.List;
import java.util.Optional;

import smartspace.data.*;

public interface UserDao<UserKey> {
	
	public UserEntity create(UserEntity userEntity);
	public Optional<UserEntity> readById(UserKey userKey);
	public List<UserEntity> readAll();
	public void update(UserEntity userEntity);
	public void deleteAll();

}
