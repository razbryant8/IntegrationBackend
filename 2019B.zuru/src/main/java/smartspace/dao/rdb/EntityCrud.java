package smartspace.dao.rdb;

import org.springframework.data.repository.CrudRepository;

import data.ElementEntity;

public interface EntityCrud extends CrudRepository<ElementEntity, String> {

}
