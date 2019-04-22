package smartspace.dao.rdb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import smartspace.dao.EnhancedActionDao;
import smartspace.data.ActionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RdbActionDao implements EnhancedActionDao {
    private ActionCrud actionCrud;
    private IdGeneratorCrud idGeneratorCrud;
    private String smartspace;

    @Autowired
    public RdbActionDao(ActionCrud actionCrud, IdGeneratorCrud idGeneratorCrud) {
        this.actionCrud = actionCrud;
        this.idGeneratorCrud = idGeneratorCrud;
    }

    @Override
    @Transactional
    public ActionEntity create(ActionEntity actionEntity) {
        IdGenerator nextId = this.idGeneratorCrud.save(new IdGenerator());
        actionEntity.setActionId("" + nextId.getNextId() + "#" + this.smartspace);
        this.idGeneratorCrud.delete(nextId);
        return this.actionCrud.save(actionEntity);
    }

    @Override
    @Transactional(readOnly = true)
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

    @Value("${spring.application.name}")
    public void setSmartspace(String smartspace) {

    }

    @Override
    @Transactional(readOnly = true)
    public List<ActionEntity> readAll(int size, int page) {
        return actionCrud.findAll(PageRequest.of(page, size)).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActionEntity> readAll(int size, int page, String sortBy) {
        return actionCrud.findAll(PageRequest.of(page, size, Sort.Direction.ASC, sortBy)).getContent();
    }

    @Override
    public ActionEntity upsert(ActionEntity actionEntity) {
        return actionCrud.save(actionEntity);
    }
}
