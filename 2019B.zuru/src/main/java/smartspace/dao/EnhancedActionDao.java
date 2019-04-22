package smartspace.dao;

import smartspace.data.ActionEntity;

import java.util.List;

public interface EnhancedActionDao extends ActionDao {

    List<ActionEntity> readAll(int size, int page);

    List<ActionEntity> readAll(int size, int page, String sortBy);

    ActionEntity upsert(ActionEntity actionEntity);

}
