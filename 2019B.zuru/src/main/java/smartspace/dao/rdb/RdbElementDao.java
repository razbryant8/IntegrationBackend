package smartspace.dao.rdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import smartspace.dao.ElementNotFoundException;
import smartspace.dao.EnhancedElementDao;
import smartspace.data.ElementEntity;


@Repository
public class RdbElementDao implements EnhancedElementDao<String> {

    private ElementCrud elementCrud;
    private IdGeneratorCrud idGeneratorCrud;
    private String smartspace;

    @Autowired
    public RdbElementDao(ElementCrud elementCrud, IdGeneratorCrud idGeneratorCrud) {
        this.elementCrud = elementCrud;
        this.idGeneratorCrud = idGeneratorCrud;
    }

    @Override
    @Transactional
    public ElementEntity create(ElementEntity elementEntity) {
        IdGenerator nextId = this.idGeneratorCrud.save(new IdGenerator());
        elementEntity.setKey("" + nextId.getNextId() + "#" + this.smartspace);
        this.idGeneratorCrud.delete(nextId);
        return this.elementCrud.save(elementEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ElementEntity> readById(String elementKey) {
        return this.elementCrud.findById(elementKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ElementEntity> readAll() {
        List<ElementEntity> entityList = new ArrayList<>();
        this.elementCrud.findAll().forEach(entityList::add);
        return entityList;
    }

    @Override
    @Transactional
    public void update(ElementEntity elementEntity) {
        ElementEntity existing = this.readById(elementEntity.getKey())
                .orElseThrow(() -> new ElementNotFoundException("No element with this ID: "
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


        this.elementCrud.save(existing);
    }

    @Override
    @Transactional
    public void deleteByKey(String elementKey) {
        this.elementCrud.deleteById(elementKey);

    }

    @Override
    @Transactional
    public void delete(ElementEntity elementEntity) {
        this.elementCrud.delete(elementEntity);

    }

    @Override
    @Transactional
    public void deleteAll() {
        this.elementCrud.deleteAll();

    }

    @Override
    @Transactional(readOnly = true)
    public List<ElementEntity> readAll(int size, int page) {
        return this.elementCrud.findAll(PageRequest.of(page, size)).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ElementEntity> readAll(int size, int page, String sortBy) {
        return this.elementCrud.findAll(PageRequest.of(page, size, Sort.Direction.ASC, sortBy)).getContent();
    }

    @Override
    @Transactional
    public ElementEntity upsert(ElementEntity elementEntity) {
        return elementCrud.save(elementEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ElementEntity> getAllElementsByType(int size, int page, String type, String sortBy) {
        return this.elementCrud.findAllByType(type, PageRequest.of(page, size, Sort.Direction.ASC, sortBy));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ElementEntity> getAllElementsByName(int size, int page, String name, String sortBy) {
        return this.elementCrud.findAllByName(name, PageRequest.of(page, size, Sort.Direction.ASC, sortBy));
    }


    @Value("${spring.application.name}")
    public void setSmartspace(String smartspace) {
        this.smartspace = smartspace;
    }
}
