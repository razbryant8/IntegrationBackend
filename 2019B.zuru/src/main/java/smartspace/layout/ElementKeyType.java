package smartspace.layout;

import org.springframework.stereotype.Component;

@Component
public class ElementKeyType {

    private String id;
    private String smartspace;

    public ElementKeyType() {
    }

    public ElementKeyType(String id, String smartspace) {
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
}
