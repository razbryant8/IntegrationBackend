package smartspace.dao.rdb;

import org.springframework.data.repository.CrudRepository;

import org.springframework.data.repository.PagingAndSortingRepository;
import smartspace.data.ElementEntity;

public interface ElementCrud extends PagingAndSortingRepository<ElementEntity, String> {

}
