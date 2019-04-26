package smartspace.dao;
import java.util.List;

import smartspace.data.*;

public interface ActionDao {
	
	ActionEntity create(ActionEntity actionEntity);
	List<ActionEntity> readAll();
	void deleteAll();

}
