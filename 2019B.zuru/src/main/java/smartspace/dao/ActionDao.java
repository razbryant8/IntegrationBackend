package smartspace.dao;

import smartspace.data.ActionEntity;

import java.util.List;

public interface ActionDao {

    ActionEntity create(ActionEntity actionEntity);

    List<ActionEntity> readAll();

    void deleteAll();

}
