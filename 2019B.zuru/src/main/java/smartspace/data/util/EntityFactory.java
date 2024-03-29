package smartspace.data.util;

import smartspace.data.*;

import java.util.Date;
import java.util.Map;


public interface EntityFactory {
	public UserEntity createNewUser(String userEmail, String userSmartspace, String username, String avatar, UserRole role, long points);
	public ElementEntity createNewElement(String name, String type, Location location, Date creationTimestamp, String creatorEmail, String creatiorSmartspace, boolean expired, Map<String, Object> moreAttributes);
	public ActionEntity createNewAction(String elementId,String elementSmartspace,String actionType,Date creationTimestamp,String playerEmail,String playerSmartspace,Map<String,Object> moreAttributes);

}
