package dao.rdb;

import data.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserCrud extends CrudRepository<UserEntity, String> {


}
