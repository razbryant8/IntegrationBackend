package smartspace.dao.rdb;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import smartspace.data.ElementEntity;


import java.util.List;

public interface ElementCrud extends PagingAndSortingRepository<ElementEntity, String> {

    public List<ElementEntity>
    findAllByName(
            @Param("name") String name,
            Pageable pageable);

    public List<ElementEntity>
    findAllByType(
            @Param("type") String type,
            Pageable pageable);

}
