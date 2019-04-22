package smartspace.layout;

import smartspace.data.ActionEntity;

import java.util.Date;
import java.util.Map;

public class ActionBoundary {

    private KeyType actionKey;
    private String type;
    private Date created;
    private KeyType element;
    private ElementCreatorType player;
    private Map<String, Object> actionProperties;

    public ActionBoundary() { }

    public ActionBoundary(ActionEntity actionEntity) {
        this.actionKey = new KeyType(actionEntity.getActionId(), actionEntity.getActionSmartspace());
        this.type = actionEntity.getActionType();
        this.created = actionEntity.getCreationTimestamp();
        this.element = new KeyType(actionEntity.getElementId(), actionEntity.getElementSmartspace());
        this.player = new ElementCreaterType(actionEntity.getPlayerEmail(), actionEntity.getPlayerSmartspace());
        this.actionProperties = actionEntity.getMoreAttributes();
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

    public ElementCreatorType getPlayer() {
        return player;
    }

    public void setPlayer(ElementCreatorType player) {
        this.player = player;
    }

    public Map<String, Object> getActionProperties() {
        return actionProperties;
    }

    public void setActionProperties(Map<String, Object> actionProperties) {
        this.actionProperties = actionProperties;
    }

    public ActionEntity convertToEntity() {
        ActionEntity entity = new ActionEntity();

        if (this.actionKey != null && this.actionKey.getId() != null
                && this.actionKey.getSmartspace() != null) {
            entity.setKey(this.actionKey.getId() + "#" + this.actionKey.getSmartspace());
        } else {
            entity.setKey(null);
        }
    }
}
