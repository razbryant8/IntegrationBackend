package smartspace.dao.rdb;


import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import smartspace.data.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserCrud extends PagingAndSortingRepository<UserEntity, String> {

    public List<UserEntity>
    findAllByRoleLike(
            @Param("role") String role,
            Pageable pageable);

    public List<UserEntity>
    findUserByEmailAndSmartspaceLike(
            @Param("email") String email,
            @Param("smartspace") String smartspace,
            @Param("sortBy") String sortBy,
            Pageable pageable);




}

