package smartspace.layout;

import smartspace.data.ElementEntity;
import smartspace.data.Location;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ElementBoundary {
    private Map<String, String> key;
    private String elementType;
    private String name;
    private boolean expired;
    private Date created;
    private Map<String, String> creator;
    private Map<String, Double> latlng;
    private Map<String, Object> elementProperties;

    public ElementBoundary() {
    }

    public ElementBoundary(ElementEntity elementEntity) {
        this.key = new HashMap<String, String>();
        this.key.put("id", elementEntity.getElementId().
                replace(elementEntity.getElementSmartspace(), ""));
        this.key.put("smartspace", elementEntity.getElementSmartspace());
        this.elementType = elementEntity.getType();
        this.name = elementEntity.getName();
        this.expired = elementEntity.isExpired();
        this.created = elementEntity.getCreationTimestamp();
        this.creator = new HashMap<String, String>();
        this.creator.put("email", elementEntity.getCreatorEmail());
        this.creator.put("smartspace", elementEntity.getCreatorSmartspace());
        if (elementEntity.getLocation() != null) {
            this.latlng = new HashMap<String, Double>();
            this.latlng.put("lat", elementEntity.getLocation().getX() - 180.0);
            this.latlng.put("lng", 180.0 - elementEntity.getLocation().getY());
        } else
            this.latlng = null;
        this.elementProperties = elementEntity.getMoreAttributes();
    }

    public Map<String, String> getKey() {
        return key;
    }

    public void setKey(Map<String, String> key) {
        this.key = key;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Map<String, String> getCreator() {
        return creator;
    }

    public void setCreator(Map<String, String> creator) {
        this.creator = creator;
    }

    public Map<String, Double> getLatlng() {
        return latlng;
    }

    public void setLatlng(Map<String, Double> latlng) {
        this.latlng = latlng;
    }

    public Map<String, Object> getElementProperties() {
        return elementProperties;
    }

    public void setElementProperties(Map<String, Object> elementProperties) {
        this.elementProperties = elementProperties;
    }

    public ElementEntity convertToEntity() {
        ElementEntity entity = new ElementEntity();

        entity.setElementId(this.key.get("id"));
        entity.setElementSmartspace(this.key.get("smartspace"));
        entity.setLocation(null);
        if (this.latlng != null && !this.latlng.isEmpty() && this.latlng.get("lat") != null
                && this.latlng.get("lng") != null) {
            Location location = new Location();
            location.setX(this.latlng.get("lat") + 180);
            location.setY(180 - this.latlng.get("lng"));
            entity.setLocation(location);
        }
        entity.setName(this.name);
        entity.setType(this.elementType);
        entity.setCreationTimestamp(this.created);
        entity.setExpired(this.expired);
        entity.setCreatorSmartspace(null);
        entity.setCreatorEmail(null);
        if (this.creator != null && !this.creator.isEmpty() &&
                this.creator.get("email") != null && this.creator.get("smartspace") != null) {
            entity.setCreatorEmail(this.creator.get("email"));
            entity.setCreatorSmartspace(this.creator.get("smartspace"));
        }
        entity.setMoreAttributes(this.elementProperties);
        return entity;
    }

}
