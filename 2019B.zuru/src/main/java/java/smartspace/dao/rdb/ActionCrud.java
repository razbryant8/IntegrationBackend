package smartspace.dao.rdb;

import smartspace.data.ActionEntity;
import org.springframework.data.repository.CrudRepository;



public interface ActionCrud extends CrudRepository<ActionEntity, String> {

}
