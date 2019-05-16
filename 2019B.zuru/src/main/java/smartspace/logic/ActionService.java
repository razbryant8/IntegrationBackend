package smartspace.logic;

import smartspace.data.ActionEntity;

import java.util.List;

public interface ActionService {

    List<ActionEntity> getAll(int size, int page);

    ActionEntity store(ActionEntity actionEntity);

    ActionEntity invoke(ActionEntity actionEntity);

}
