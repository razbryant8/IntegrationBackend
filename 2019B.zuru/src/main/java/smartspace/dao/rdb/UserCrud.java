package smartspace.dao.rdb;

import org.springframework.data.repository.CrudRepository;
import smartspace.data.UserEntity;

public interface UserCrud extends CrudRepository<UserEntity, String> {


}
