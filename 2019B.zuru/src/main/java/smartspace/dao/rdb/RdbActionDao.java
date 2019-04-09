package smartspace.dao.rdb;

import smartspace.dao.ActionDao;
import smartspace.data.ActionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RdbActionDao implements ActionDao {
    private ActionCrud actionCrud;
    private IdGeneratorCrud idGeneratorCrud;

    @Autowired
    public RdbActionDao(ActionCrud actionCrud, IdGeneratorCrud idGeneratorCrud) {
        this.actionCrud = actionCrud;
        this.idGeneratorCrud = idGeneratorCrud;
    }

    @Override
    @Transactional
    public ActionEntity create(ActionEntity actionEntity) {
        IdGenerator nextId = this.idGeneratorCrud.save(new IdGenerator());
        actionEntity.setActionId("" + nextId.getNextId() + actionEntity.getElementSmartspace());
        this.idGeneratorCrud.delete(nextId);
        return this.actionCrud.save(actionEntity);
    }

    @Override
    @Transactional
    public List<ActionEntity> readAll() {
        List<ActionEntity> actionsList = new ArrayList<>();
        this.actionCrud.findAll().forEach(actionsList::add);
        return actionsList;
    }

    @Override
    @Transactional
    public void deleteAll() {
        this.actionCrud.deleteAll();
    }
}
