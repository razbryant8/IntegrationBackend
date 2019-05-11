package smartspace.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartspace.dao.ElementNotFoundException;
import smartspace.dao.EnhancedElementDao;
import smartspace.data.ElementEntity;
import smartspace.data.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ElementServiceImpl implements ElementService {

    private EnhancedElementDao<String> enhancedElementDao;
    private String smartspace;

    @Autowired
    public ElementServiceImpl(EnhancedElementDao<String> enhancedElementDao) {
        this.enhancedElementDao = enhancedElementDao;
    }

    @Override
    public List<ElementEntity> getAll(int size, int page, UserRole userRole) {
        if (userRole.equals(UserRole.ADMIN))
            return this.enhancedElementDao.readAll(size, page, "creationTimestamp");
        else
            throw new RuntimeException("Unauthorized action");
    }

    @Override
    @Transactional
    public ElementEntity store(ElementEntity elementEntity, UserRole userRole) {
        if (userRole.equals(UserRole.ADMIN)) {
            if (validate(elementEntity)) {
                return this.enhancedElementDao
                        .upsert(elementEntity);
            } else {
                throw new RuntimeException("Invalid element input");
            }
        } else
            throw new RuntimeException("Unauthorized Operation");
    }

    @Override
    @Transactional
    public ElementEntity create(ElementEntity elementEntity, UserRole userRole) {
        if (userRole.equals(UserRole.MANAGER)) {
            if (validateCreation(elementEntity)) {
                return this.enhancedElementDao.create(elementEntity);
            } else {
                throw new RuntimeException("Invalid element input");
            }
        } else
            throw new RuntimeException("Unauthorized operation");
    }

    @Override
    public List<ElementEntity> getByType(int size, int page, String type, UserRole userRole) {
        if (userRole.equals(UserRole.PLAYER))
            return this.enhancedElementDao.
                    getAllElementsByType(size, page, type, "creationTimestamp")
                    .stream()
                    .filter(elementEntity -> !elementEntity.isExpired())
                    .collect(Collectors.toList());
        else if (userRole.equals((UserRole.MANAGER)))
            return this.enhancedElementDao.getAllElementsByType(size, page, type, "creationTimestamp");
        else
            throw new ElementNotFoundException("Unauthorized request");
    }

    @Override
    public List<ElementEntity> getByName(int size, int page, String name, UserRole userRole) {
        if (userRole.equals(UserRole.PLAYER))
            return this.enhancedElementDao.
                    getAllElementsByName(size, page, name, "creationTimestamp")
                    .stream()
                    .filter(elementEntity -> !elementEntity.isExpired())
                    .collect(Collectors.toList());
        else if (userRole.equals((UserRole.MANAGER)))
            return this.enhancedElementDao.getAllElementsByName(size, page, name, "creationTimestamp");
        else
            throw new ElementNotFoundException("Unauthorized request");

    }

    @Override
    public ElementEntity getById(String elementId, String elementSmartspace, UserRole userRole) {
        if (userRole.equals(UserRole.PLAYER)) {
            ElementEntity elementEntity = new ElementEntity();
            elementEntity.setElementId(elementId);
            elementEntity.setElementSmartspace(elementSmartspace);
            Optional<ElementEntity> receivedEntity = this.enhancedElementDao.readById(elementEntity.getKey());
            if (receivedEntity.isPresent() && !receivedEntity.get().isExpired())
                return receivedEntity.get();
            else
                throw new ElementNotFoundException("No element with this ID: "
                        + elementEntity.getKey());
        } else if (userRole.equals(UserRole.MANAGER)) {
            ElementEntity elementEntity = new ElementEntity();
            elementEntity.setElementId(elementId);
            elementEntity.setElementSmartspace(elementSmartspace);
            Optional<ElementEntity> receivedEntity = this.enhancedElementDao.readById(elementEntity.getKey());
            if (receivedEntity.isPresent())
                return receivedEntity.get();
            else
                throw new ElementNotFoundException("No element with this ID: "
                        + elementEntity.getKey());
        } else
            throw new ElementNotFoundException("Unauthorized request");

    }

    @Override
    public void update(ElementEntity elementEntity, String elementId, String elementSmartspace, UserRole userRole) {
        if (userRole.equals(UserRole.MANAGER)) {
            elementEntity.setElementId(elementId);
            elementEntity.setElementSmartspace(elementSmartspace);
            if (validateCreation(elementEntity)) {
                this.enhancedElementDao.update(elementEntity);
            } else {
                throw new RuntimeException("Invalid element input");
            }
        } else
            throw new RuntimeException("Unauthorized operation");
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
