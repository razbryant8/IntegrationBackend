package smartspace.logic;

import smartspace.data.ElementEntity;

import java.util.List;
import java.util.Optional;

public interface ElementService {
    public List<ElementEntity> getAll(int size, int page);

    public ElementEntity store(ElementEntity elementEntity);

    public ElementEntity create(ElementEntity elementEntity);

    public List<ElementEntity> getByType(int size, int page, String type);

    public List<ElementEntity> getByName(int size, int page, String name);

    public ElementEntity getById (String elementId, String elementSmartspace);
}
