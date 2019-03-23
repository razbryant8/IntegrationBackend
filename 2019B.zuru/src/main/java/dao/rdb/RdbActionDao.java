package dao.rdb;

import dao.ActionDao;
import data.ActionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class RdbActionDao implements ActionDao {
    private ActionCrud actionCrud;
    private AtomicLong nextActionId;

    @Autowired
    public RdbActionDao(ActionCrud actionCrud){
        this.actionCrud = actionCrud;
        this.nextActionId=new AtomicLong(1L);
    }

    @Override
    @Transactional
    public ActionEntity create(ActionEntity actionEntit) {
        actionEntit.setActionId(""+nextActionId.getAndIncrement()+actionEntit.getElementSmartspace());
        return this.actionCrud.save(actionEntit);
    }

    @Override
    @Transactional
    public List<ActionEntity> readAll() {
        List<ActionEntity> actionsList = new ArrayList<>();
        this.actionCrud.findAll().forEach(entity->actionsList.add(entity));
        return actionsList;
    }

    @Override
    public void deleteAll() {
        this.actionCrud.deleteAll();
    }
}
