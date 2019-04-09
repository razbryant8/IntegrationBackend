package smartspace.data;

import smartspace.dao.rdb.MapToJsonConverter;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@Entity
@Table(name = "ACTIONS")
public class ActionEntity {

    private String actionSmartspace;
    private String actionId;
    private String elementSmartspace;
    private String ElementId;
    private String playerSmartspace;
    private String playerEmail;
    private String actionType;
    private Date creationTimestamp;
    private Map<String, Object> moreAttributes;

    public ActionEntity() {

    }

    public ActionEntity(String elementId, String elementSmartspace, String actionType, Date creationTimestamp, String playerEmail, String playerSmartspace, Map<String, Object> moreAttributes) {
        this.ElementId = elementId;
        this.elementSmartspace = elementSmartspace;
        this.actionType = actionType;
        this.creationTimestamp = creationTimestamp;
        this.playerEmail = playerEmail;
        this.playerSmartspace = playerSmartspace;
        this.moreAttributes = moreAttributes;
    }

    public String getActionSmartspace() {
        return actionSmartspace;
    }

    public void setActionSmartspace(String actionSmartspace) {
        this.actionSmartspace = actionSmartspace;
    }

    @Id
    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getElementSmartspace() {
        return elementSmartspace;
    }

    public void setElementSmartspace(String elementSmartspace) {
        this.elementSmartspace = elementSmartspace;
    }

    public String getElementId() {
        return ElementId;
    }

    public void setElementId(String elementId) {
        ElementId = elementId;
    }

    public String getPlayerSmartspace() {
        return playerSmartspace;
    }

    public void setPlayerSmartspace(String playerSmartspace) {
        this.playerSmartspace = playerSmartspace;
    }

    public String getPlayerEmail() {
        return playerEmail;
    }

    public void setPlayerEmail(String playerEmail) {
        this.playerEmail = playerEmail;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Date creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    @Lob
    @Convert(converter = MapToJsonConverter.class)
    public Map<String, Object> getMoreAttributes() {
        return moreAttributes;
    }

    public void setMoreAttributes(Map<String, Object> moreAttributes) {
        this.moreAttributes = moreAttributes;
    }


}
