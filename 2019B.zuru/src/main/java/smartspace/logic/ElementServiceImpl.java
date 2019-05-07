package smartspace.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartspace.dao.ElementNotFoundException;
import smartspace.dao.EnhancedElementDao;
import smartspace.data.ElementEntity;

import java.util.List;
import java.util.Optional;

@Service
public class ElementServiceImpl implements ElementService {

    private EnhancedElementDao<String> enhancedElementDao;
    private String smartspace;

    @Autowired
    public ElementServiceImpl(EnhancedElementDao<String> enhancedElementDao) {
        this.enhancedElementDao = enhancedElementDao;
    }

    @Override
    public List<ElementEntity> getAll(int size, int page) {
        return this.enhancedElementDao.readAll(size, page, "creationTimestamp");
    }

    @Override
    @Transactional
    public ElementEntity store(ElementEntity elementEntity) {
        if (validate(elementEntity)) {
            return this.enhancedElementDao
                    .upsert(elementEntity);
        } else {
            throw new RuntimeException("Invalid element input");
        }
    }

    @Override
    @Transactional
    public ElementEntity create(ElementEntity elementEntity) {
        if (validateCreation(elementEntity)) {
            return this.enhancedElementDao.create(elementEntity);
        } else {
            throw new RuntimeException("Invalid element input");
        }
    }

    @Override
    public List<ElementEntity> getByType(int size, int page, String type) {
        return this.enhancedElementDao.getAllElementsByType(size, page, type);
    }

    @Override
    public List<ElementEntity> getByName(int size, int page, String name) {
        return this.enhancedElementDao.getAllElementsByName(size, page, name);
    }

    @Override
    public ElementEntity getById(String elementId, String elementSmartspace) {
        ElementEntity elementEntity = new ElementEntity();
        elementEntity.setElementId(elementId);
        elementEntity.setElementSmartspace(elementSmartspace);
        Optional<ElementEntity> receivedEntity = this.enhancedElementDao.readById(elementEntity.getKey());
        if (receivedEntity.isPresent() && !receivedEntity.get().isExpired())
            return receivedEntity.get();
        else
            throw new ElementNotFoundException("No element with this ID: "
                + elementEntity.getKey());
    }

    private boolean validate(ElementEntity elementEntity) {
        return elementEntity.getLocation() != null &&
                elementEntity.getMoreAttributes() != null &&
                elementEntity.getType() != null &&
                !elementEntity.getType().trim().isEmpty() &&
                elementEntity.getName() != null &&
                !elementEntity.getName().trim().isEmpty() &&
                elementEntity.getCreatorEmail() != null &&
                !elementEntity.getCreatorEmail().trim().isEmpty() &&
                elementEntity.getCreatorSmartspace() != null &&
                !elementEntity.getCreatorSmartspace().trim().isEmpty() &&
                elementEntity.getElementSmartspace() != null &&
                !elementEntity.getElementSmartspace().trim().isEmpty() &&
                !elementEntity.getElementSmartspace().equals(this.smartspace) &&
                elementEntity.getElementId() != null &&
                !elementEntity.getElementId().trim().isEmpty();

    }

    private boolean validateCreation(ElementEntity elementEntity) {
        return elementEntity.getLocation() != null &&
                elementEntity.getMoreAttributes() != null &&
                elementEntity.getType() != null &&
                !elementEntity.getType().trim().isEmpty() &&
                elementEntity.getName() != null &&
                !elementEntity.getName().trim().isEmpty() &&
                elementEntity.getCreatorEmail() != null &&
                !elementEntity.getCreatorEmail().trim().isEmpty() &&
                elementEntity.getCreatorSmartspace() != null &&
                !elementEntity.getCreatorSmartspace().trim().isEmpty();
    }

    @Value("${spring.application.name}")
    public void setSmartspace(String smartspace) {
        this.smartspace = smartspace;
    }

}
