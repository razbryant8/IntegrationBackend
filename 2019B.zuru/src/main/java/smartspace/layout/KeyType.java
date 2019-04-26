package smartspace.layout;

import java.util.Objects;

public class KeyType {


    private String id;
    private String smartspace;

    public KeyType() {
    }

    public KeyType(String id, String smartspace) {
        this.id = id;
        this.smartspace = smartspace;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSmartspace() {
        return smartspace;
    }

    public void setSmartspace(String smartspace) {
        this.smartspace = smartspace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeyType)) return false;
        KeyType that = (KeyType) o;
        return getId().equals(that.getId()) &&
                getSmartspace().equals(that.getSmartspace());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getSmartspace());
    }
}
