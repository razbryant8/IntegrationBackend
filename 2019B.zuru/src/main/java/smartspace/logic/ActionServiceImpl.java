package smartspace.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartspace.dao.EnhancedActionDao;
import smartspace.data.ActionEntity;

import java.util.List;

@Service
public class ActionServiceImpl implements ActionService {

    private EnhancedActionDao enhancedActionDao;
    private String smartspace;

    @Autowired
    public ActionServiceImpl(EnhancedActionDao enhancedActionDao) {
        this.enhancedActionDao = enhancedActionDao;
    }

    @Override
    public List<ActionEntity> getAll(int size, int page) {
        return this.enhancedActionDao.readAll(size, page, "creationTimestamp");
    }


    @Override
    @Transactional
    public ActionEntity store(ActionEntity actionEntity) {
        if (validate(actionEntity)) {
            return this.enhancedActionDao
                    .upsert(actionEntity);
        } else {
            throw new RuntimeException("Invalid element input");
        }
    }

    private boolean validate(ActionEntity actionEntity) {
        return actionEntity.getMoreAttributes() != null &&
                actionEntity.getActionType() != null &&
                !actionEntity.getActionType().trim().isEmpty() &&
                actionEntity.getPlayerEmail() != null &&
                !actionEntity.getPlayerEmail().trim().isEmpty() &&
                actionEntity.getPlayerSmartspace() != null &&
                !actionEntity.getPlayerSmartspace().trim().isEmpty() &&
                actionEntity.getActionSmartspace() != null &&
                !actionEntity.getActionSmartspace().trim().isEmpty() &&
                actionEntity.getActionId()!=null &&
                !actionEntity.getActionId().trim().isEmpty()&&
                actionEntity.getElementSmartspace() != null &&
                !actionEntity.getElementSmartspace().trim().isEmpty() &&
                !actionEntity.getElementSmartspace().equals(this.smartspace) &&
//                !actionEntity.getElementSmartspace().equals(actionEntity.getActionSmartspace()) &&
//                !actionEntity.getElementSmartspace().equals(actionEntity.getPlayerSmartspace()) &&
                actionEntity.getElementId() != null &&
                !actionEntity.getElementId().trim().isEmpty();

    }

    @Value("${spring.application.name}")
    public void setSmartspace(String smartspace) {
        this.smartspace = smartspace;
    }
}
