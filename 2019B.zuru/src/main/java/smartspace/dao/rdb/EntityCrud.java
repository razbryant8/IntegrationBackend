package smartspace.dao.rdb;

import org.springframework.data.repository.CrudRepository;

import smartspace.data.ElementEntity;

public interface EntityCrud extends CrudRepository<ElementEntity, String> {

}
