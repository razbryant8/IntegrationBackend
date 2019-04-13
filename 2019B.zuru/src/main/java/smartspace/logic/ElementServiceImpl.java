package smartspace.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartspace.dao.EnhancedElementDao;
import smartspace.data.ElementEntity;

import java.util.Date;
import java.util.List;

@Service
public class ElementServiceImpl implements ElementService {

    private EnhancedElementDao<String> enhancedElementDao;

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
            elementEntity.setCreationTimestamp(new Date());
            return this.enhancedElementDao
                    .create(elementEntity);
        } else {
            throw new RuntimeException("Invalid element input");
        }
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
                !elementEntity.getCreatorSmartspace().trim().isEmpty(); //To be completed &&
        //!elementEntity.getElementSmartspace().equals();

    }
}
