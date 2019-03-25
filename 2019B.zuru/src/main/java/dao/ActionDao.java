package dao;

import java.util.List;

import data.*;

public interface ActionDao {

    public ActionEntity create(ActionEntity actionEntit);

    public List<ActionEntity> readAll();

    public void deleteAll();

}
