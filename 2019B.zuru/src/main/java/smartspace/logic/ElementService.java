package smartspace.logic;

import smartspace.data.ElementEntity;
import smartspace.data.UserRole;

import java.util.List;
import java.util.Optional;

public interface ElementService {
    public List<ElementEntity> getAll(int size, int page, UserRole userRole);

    public ElementEntity store(ElementEntity elementEntity, UserRole userRole);

    public ElementEntity create(ElementEntity elementEntity, UserRole userRole);

    public List<ElementEntity> getByType(int size, int page, String type, UserRole userRole);

    public List<ElementEntity> getByName(int size, int page, String name, UserRole userRole);

    public ElementEntity getById(String elementId, String elementSmartspace, UserRole userRole);

    void update(ElementEntity elementEntity , String elementId, String elementSmartspace, UserRole userRole);

    List<ElementEntity> getAllElements(int size, int page, UserRole userRole);


}
