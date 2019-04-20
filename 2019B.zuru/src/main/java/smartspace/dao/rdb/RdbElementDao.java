package smartspace.dao.rdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import smartspace.dao.EnhancedElementDao;
import smartspace.data.ElementEntity;


@Repository
public class RdbElementDao implements EnhancedElementDao<String> {

    private EntityCrud entityCrud;
    private IdGeneratorCrud idGeneratorCrud;

    @Autowired
    public RdbElementDao(EntityCrud entityCrud, IdGeneratorCrud idGeneratorCrud) {
        this.entityCrud = entityCrud;
        this.idGeneratorCrud = idGeneratorCrud;
    }

    @Override
    @Transactional
    public ElementEntity create(ElementEntity elementEntity) {
        IdGenerator nextId = this.idGeneratorCrud.save(new IdGenerator());
        elementEntity.setKey("" + nextId.getNextId() + "#" + elementEntity.getElementSmartspace());
        this.idGeneratorCrud.delete(nextId);
        return this.entityCrud.save(elementEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ElementEntity> readById(String elementKey) {
        return this.entityCrud.findById(elementKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ElementEntity> readAll() {
        List<ElementEntity> entityList = new ArrayList<>();
        this.entityCrud.findAll().forEach(entityList::add);
        return entityList;
    }

    @Override
    @Transactional
    public void update(ElementEntity elementEntity) {
        ElementEntity existing = this.readById(elementEntity.getKey())
                .orElseThrow(() -> new RuntimeException("No element with this ID: "
                        + elementEntity.getElementId()));

        if (elementEntity.getLocation() != null) {
            existing.setLocation(elementEntity.getLocation());
        }

        if (elementEntity.getName() != null) {
            existing.setName(elementEntity.getName());
        }

        if (elementEntity.getMoreAttributes() != null) {
            existing.setMoreAttributes(elementEntity.getMoreAttributes());
        }

        if (elementEntity.getType() != null) {
            existing.setType(elementEntity.getType());
        }

        existing.setExpired(elementEntity.isExpired());


        this.entityCrud.save(existing);
    }

    @Override
    @Transactional
    public void deleteByKey(String elementKey) {
        this.entityCrud.deleteById(elementKey);

    }

    @Override
    @Transactional
    public void delete(ElementEntity elementEntity) {
        this.entityCrud.delete(elementEntity);

    }

    @Override
    @Transactional
    public void deleteAll() {
        this.entityCrud.deleteAll();

    }

    @Override
    @Transactional(readOnly = true)
    public List<ElementEntity> readAll(int size, int page) {
        return this.entityCrud.findAll(PageRequest.of(page, size)).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ElementEntity> readAll(int size, int page, String sortBy) {
        return this.entityCrud.findAll(PageRequest.of(page, size, Sort.Direction.ASC, sortBy)).getContent();
    }

    @Override
    public ElementEntity upsert(ElementEntity elementEntity) {
        return entityCrud.save(elementEntity);
    }
}
