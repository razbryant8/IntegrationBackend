package smartspace.dao;

import smartspace.data.ElementEntity;

import java.util.List;

public interface EnhancedElementDao<ElementKey> extends ElementDao<ElementKey> {
    public List<ElementEntity> readAll(int size, int page);

    public List<ElementEntity> readAll(int size, int page, String sortBy);
    /*public List<ElementEntity> getMessagesContainigText (
            String pattern, int size, int page);
    public List<ElementEntity> getMessagesWithTimestampRange(
            Date fromDate,
            Date toDate,
            int size, int page);*/
}
