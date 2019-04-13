package smartspace.logic;

import smartspace.data.ElementEntity;

import java.util.List;

public interface ElementService {
    public List<ElementEntity> getAll(int size, int page);

    public ElementEntity store(ElementEntity elementEntity);
}
