package smartspace.dao.rdb;

import org.springframework.data.repository.CrudRepository;

import org.springframework.data.repository.PagingAndSortingRepository;
import smartspace.data.ElementEntity;

public interface EntityCrud extends PagingAndSortingRepository<ElementEntity, String> {

}
