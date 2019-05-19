package smartspace.logic;

import smartspace.data.ElementEntity;
import smartspace.data.UserRole;

import java.util.List;

public interface ElementService {
    public List<ElementEntity> getAll(int size, int page);

    public ElementEntity[] store(ElementEntity[] elementEntity);

    public ElementEntity create(ElementEntity elementEntity);

    public List<ElementEntity> getByType(int size, int page, String type, UserRole userRole);

    public List<ElementEntity> getByName(int size, int page, String name, UserRole userRole);

    public ElementEntity getById(String elementId, String elementSmartspace, UserRole userRole);

    public List<ElementEntity> getByLocation(int size, int page, double x, double y, int distance, UserRole userRole);

    void update(ElementEntity elementEntity, String elementId, String elementSmartspace);

    List<ElementEntity> getAllElements(int size, int page, UserRole userRole);


}
