package smartspace.data;

import smartspace.dao.rdb.MapToJsonConverter;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@Entity
@Table(name = "ELEMENTS")
public class ElementEntity implements SmartspaceEntity<String> {

    private String elementSmartspace;
    private String elementId;
    private Location location;
    private String name;
    private String type;
    private Date creationTimestamp;
    private boolean expired;
    private String creatorSmartspace;
    private String creatorEmail;
    private Map<String, Object> moreAttributes;

    public ElementEntity() {

    }

    public ElementEntity(String name, String type, Location location, Date creationTimestamp, String creatorEmail, String creatiorSmartspace, boolean expired, Map<String, Object> moreAttributes) {
        super();
        this.name = name;
        this.type = type;
        this.creationTimestamp = creationTimestamp;
        this.creatorEmail = creatorEmail;
        this.creatorSmartspace = creatiorSmartspace;
        this.expired = expired;
        this.moreAttributes = moreAttributes;
        this.location = location;
    }

    @Transient
    public String getElementSmartspace() {
        return elementSmartspace;
    }

    public void setElementSmartspace(String elementSmartspace) {
        this.elementSmartspace = elementSmartspace;
    }

    @Transient
    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    @Embedded
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Date creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public String getCreatorSmartspace() {
        return creatorSmartspace;
    }

    public void setCreatorSmartspace(String creatorSmartspace) {
        this.creatorSmartspace = creatorSmartspace;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    @Lob
    @Convert(converter = MapToJsonConverter.class)
    public Map<String, Object> getMoreAttributes() {
        return moreAttributes;
    }

    public void setMoreAttributes(Map<String, Object> moreAttributes) {
        this.moreAttributes = moreAttributes;
    }

    @Id
    @Override
    public String getKey() {
        return this.elementId + "#" + this.elementSmartspace;
    }

    @Override
    public void setKey(String key) {
        if(key != null) {
            String[] args = key.split("#");
            if (args.length == 2) {
                this.elementId = args[0];
                this.elementSmartspace = args[1];
            }
        }
    }
}
