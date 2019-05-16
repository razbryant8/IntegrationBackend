package smartspace.layout;

import smartspace.data.ActionEntity;

import java.util.Date;
import java.util.Map;

public class ActionBoundary {

    private KeyType actionKey;
    private String type;
    private Date created;
    private KeyType element;
    private UserKeyType player;
    private Map<String, Object> properties;

    public ActionBoundary() { }

    public ActionBoundary(ActionEntity actionEntity) {
        this.actionKey = new KeyType(actionEntity.getActionId(), actionEntity.getActionSmartspace());
        this.type = actionEntity.getActionType();
        this.created = actionEntity.getCreationTimestamp();
        this.element = new KeyType(actionEntity.getElementId(), actionEntity.getElementSmartspace());
        this.player = new UserKeyType(actionEntity.getPlayerEmail(), actionEntity.getPlayerSmartspace());
        this.properties = actionEntity.getMoreAttributes();
    }

    public KeyType getActionKey() {
        return actionKey;
    }

    public void setActionKey(KeyType actionKey) {
        this.actionKey = actionKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public KeyType getElement() {
        return element;
    }

    public void setElement(KeyType element) {
        this.element = element;
    }

    public UserKeyType getPlayer() {
        return player;
    }

    public void setPlayer(UserKeyType player) {
        this.player = player;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public ActionEntity convertToEntity() {
        ActionEntity entity = new ActionEntity();

        if (this.actionKey != null && this.actionKey.getId() != null
                && this.actionKey.getSmartspace() != null) {
            entity.setKey(this.actionKey.getId() + "#" + this.actionKey.getSmartspace());
        } else {
            entity.setKey(null);
        }

        if (this.type != null) {
            entity.setActionType(this.type);
        }

        entity.setCreationTimestamp(this.created);

        if (this.element != null && this.element.getId() != null
                && this.element.getSmartspace() != null) {
            entity.setElementSmartspace(this.element.getSmartspace());
            entity.setElementId(this.element.getId() + "#" + this.element.getSmartspace());
        } else {
            entity.setElementId(null);
        }

        if (this.player != null) {
            entity.setPlayerSmartspace(this.player.getSmartspace());
            entity.setPlayerEmail(this.player.getEmail());
        }

        entity.setMoreAttributes(this.properties);
        
        return entity;
    }
}
