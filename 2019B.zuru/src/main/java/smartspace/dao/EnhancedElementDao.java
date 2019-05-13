package smartspace.dao;

import smartspace.data.ElementEntity;

import java.util.List;

public interface EnhancedElementDao<ElementKey> extends ElementDao<ElementKey> {
    public List<ElementEntity> readAll(int size, int page);

    public List<ElementEntity> readAll(int size, int page, String sortBy);

    public ElementEntity upsert(ElementEntity elementEntity);

    public List<ElementEntity> getAllElementsByType(int size, int page, String type, String sortBy);

    public List<ElementEntity> getAllElementsByName(int size, int page, String name, String sortBy);

    public List<ElementEntity> getAllElementsByLocation(int size, int page, double x, double y, int distance, String sortBy);

    /*public List<ElementEntity> getMessagesContainigText (
            String pattern, int size, int page);
    public List<ElementEntity> getMessagesWithTimestampRange(
            Date fromDate,
            Date toDate,
            int size, int page);*/
}
