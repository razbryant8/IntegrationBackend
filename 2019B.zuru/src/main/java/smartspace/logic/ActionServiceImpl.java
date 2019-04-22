package smartspace.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import smartspace.dao.EnhancedActionDao;
import smartspace.data.ActionEntity;

import java.util.List;

@Service
public class ActionServiceImpl implements ActionService {

    private EnhancedActionDao enhancedActionDao;
    private String smartspace;

//    @Autowired
//    public ActionServiceImpl(EnhancedActionDao enhancedActionDao) {
//        this.enhancedActionDao = enhancedActionDao;
//    }

    @Override
    public List<ActionEntity> getAll(int size, int page) {
        return null;
    }

    @Override
    public ActionEntity store(ActionEntity actionEntity) {
        return null;
    }

    @Value("${spring.application.name}")
    public void setSmartspace(String smartspace) {
        this.smartspace = smartspace;
    }
}
