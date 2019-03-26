package dao.rdb;

import data.ActionEntity;
import org.springframework.data.repository.CrudRepository;



public interface ActionCrud extends CrudRepository<ActionEntity, String> {

}
