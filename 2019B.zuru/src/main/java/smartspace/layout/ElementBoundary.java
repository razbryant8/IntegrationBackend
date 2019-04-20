package smartspace.layout;

import smartspace.data.ElementEntity;
import smartspace.data.Location;

import java.util.Date;
import java.util.Map;

public class ElementBoundary {
    private ElementKeyType key;
    private String elementType;
    private String name;
    private boolean expired;
    private Date created;
    private ElementCreatorType creator;
    private ElementLatLngType latlng;
    private Map<String, Object> elementProperties;

    public ElementBoundary() {
    }

    public ElementBoundary(ElementEntity elementEntity) {
        this.key = new ElementKeyType(elementEntity.getElementId(),
                elementEntity.getElementSmartspace());
        this.elementType = elementEntity.getType();
        this.name = elementEntity.getName();
        this.expired = elementEntity.isExpired();
        this.created = elementEntity.getCreationTimestamp();
        this.creator = new ElementCreatorType(elementEntity.getCreatorEmail(),
                elementEntity.getCreatorSmartspace());
        if (elementEntity.getLocation() != null) {
            latlng = new ElementLatLngType(180.0 - elementEntity.getLocation().getY(),
                    elementEntity.getLocation().getX() - 180.0);
        } else
            this.latlng = null;
        this.elementProperties = elementEntity.getMoreAttributes();
    }

    public ElementKeyType getKey() {
        return key;
    }

    public void setKey(ElementKeyType key) {
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

    public ElementCreatorType getCreator() {
        return creator;
    }

    public void setCreator(ElementCreatorType creator) {
        this.creator = creator;
    }

    public ElementLatLngType getLatlng() {
        return latlng;
    }

    public void setLatlng(ElementLatLngType latlng) {
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

        if (this.key != null && this.key.getId() != null && this.key.getSmartspace() != null)
            entity.setKey(this.key.getId() + "#" + this.key.getSmartspace());
        else
            entity.setKey(null);
        entity.setLocation(null);
        if (this.latlng != null) {
            Location location = new Location();
            location.setX(this.latlng.getLng() + 180);
            location.setY(180 - this.latlng.getLat());
            entity.setLocation(location);
        }
        entity.setName(this.name);
        entity.setType(this.elementType);
        entity.setCreationTimestamp(this.created);
        entity.setExpired(this.expired);
        entity.setCreatorSmartspace(null);
        entity.setCreatorEmail(null);
        if (this.creator != null && this.creator.getEmail() != null &&
                this.creator.getSmartspace() != null) {
            entity.setCreatorEmail(this.creator.getEmail());
            entity.setCreatorSmartspace(this.creator.getSmartspace());
        }
        entity.setMoreAttributes(this.elementProperties);
        return entity;
    }

}
