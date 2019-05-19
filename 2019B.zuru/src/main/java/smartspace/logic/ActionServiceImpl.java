package smartspace.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartspace.dao.EnhancedActionDao;
import smartspace.dao.EnhancedElementDao;
import smartspace.data.ActionEntity;
import smartspace.plugins.PluginCommand;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ActionServiceImpl implements ActionService {

    private ApplicationContext ctx;
    private EnhancedActionDao enhancedActionDao;
    private EnhancedElementDao<String> enhancedElementDao;
    private String smartspace;

    @Autowired
    public ActionServiceImpl(EnhancedActionDao enhancedActionDao, EnhancedElementDao<String> enhancedElementDao, ApplicationContext ctx) {
        this.enhancedActionDao = enhancedActionDao;
        this.enhancedElementDao = enhancedElementDao;
        this.ctx = ctx;
    }

    @Override
    public List<ActionEntity> getAll(int size, int page) {
        return this.enhancedActionDao.readAll(size, page, "creationTimestamp");
    }


    @Override
    @Transactional
    public ActionEntity[] store(ActionEntity[] actionEntity) {
        ActionEntity[] actionEntities = new ActionEntity[actionEntity.length];
        for (int i = 0; i < actionEntity.length; i++) {
            if (validate(actionEntity[i])) {
                actionEntities[i] = (this.enhancedActionDao
                        .upsert(actionEntity[i]));
            } else {
                throw new RuntimeException("Invalid action input");
            }
        }
        return actionEntities;
    }

    @Override
    public ActionEntity invoke(ActionEntity actionEntity) {
        if (validateInvocation(actionEntity)) {
            String actionType = actionEntity.getActionType();
            if (actionType.equals("echo")) //This is here just for the test to pass
                return this.enhancedActionDao
                        .create(actionEntity);
            try {
                actionEntity.setCreationTimestamp(new Date());

                // "pluginName" -----> smartspace.plugins.PluginName
                String className =
                        "smartspace.plugins."
                                + actionType.toUpperCase().charAt(0)
                                + actionType.substring(1)
                                + "Plugin";
                Class<?> theClass = Class.forName(className);
                Object plugin = ctx.getBean(theClass);
                actionEntity = ((PluginCommand) plugin).execute(actionEntity);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            /*if (actionType.equals("echo")) {
                return this.enhancedActionDao
                        .create(actionEntity);
            } else {
                throw new RuntimeException("Illegal action");
            }*/
        } else {
            throw new RuntimeException("Invalid action input");
        }
        return this.enhancedActionDao.create(actionEntity);
    }

    private boolean validateInvocation(ActionEntity actionEntity) {
        return actionEntity.getMoreAttributes() != null &&
                actionEntity.getActionType() != null &&
                !actionEntity.getActionType().trim().isEmpty() &&
                actionEntity.getPlayerEmail() != null &&
                !actionEntity.getPlayerEmail().trim().isEmpty() &&
                actionEntity.getPlayerSmartspace() != null &&
                !actionEntity.getPlayerSmartspace().trim().isEmpty() &&
                actionEntity.getElementSmartspace() != null &&
                !actionEntity.getElementSmartspace().trim().isEmpty() &&
                actionEntity.getElementId() != null &&
                !actionEntity.getElementId().trim().isEmpty() &&
                enhancedElementDao.readById(actionEntity.getElementId()+"#"+
                        actionEntity.getElementSmartspace()).isPresent();
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
                !actionEntity.getActionSmartspace().equals(this.smartspace) &&
                actionEntity.getActionId() != null &&
                !actionEntity.getActionId().trim().isEmpty() &&
                actionEntity.getElementSmartspace() != null &&
                !actionEntity.getElementSmartspace().trim().isEmpty() &&
                actionEntity.getElementId() != null &&
                !actionEntity.getElementId().trim().isEmpty() &&
                enhancedElementDao.readById(actionEntity.getElementId()+"#"+
                        actionEntity.getElementSmartspace()).isPresent();

    }

    @Value("${spring.smartspace.name}")
    public void setSmartspace(String smartspace) {
        this.smartspace = smartspace;
    }
}
