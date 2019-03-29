package smartspace.dao;
import java.util.List;

import smartspace.data.*;

public interface ActionDao {
	
	public ActionEntity create(ActionEntity actionEntit);
	public List<ActionEntity> readAll();
	public void deleteAll();

}
