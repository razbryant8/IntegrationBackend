package smartspace.data.util;

import org.springframework.stereotype.Component;
import smartspace.data.*;

import java.util.Date;
import java.util.Map;

@Component
public class EntityFactoryImpl implements EntityFactory {
    @Override
    public UserEntity createNewUser(String userEmail, String userSmartspace, String username, String avatar, UserRole role, long points) {
        return new UserEntity(userEmail, userSmartspace, username, avatar, role, points);
    }

    @Override
    public ElementEntity createNewElement(String name, String type, Location location, Date creationTimestamp, String creatorEmail, String creatiorSmartspace, boolean expired, Map<String, Object> moreAttributes) {
        return new ElementEntity(name, type, location, creationTimestamp, creatorEmail, creatiorSmartspace, expired, moreAttributes);
    }

    @Override
    public ActionEntity createNewAction(String elementId, String elementSmartspace, String actionType, Date creationTimestamp, String playerEmail, String playerSmartspace, Map<String, Object> moreAttributes) {
        return new ActionEntity(elementId, elementSmartspace, actionType, creationTimestamp, playerEmail, playerSmartspace, moreAttributes);
    }
}
