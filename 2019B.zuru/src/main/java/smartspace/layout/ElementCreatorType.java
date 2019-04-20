package smartspace.layout;

import org.springframework.stereotype.Component;

@Component
public class ElementCreatorType {

    private String email;
    private String smartspace;

    public ElementCreatorType() {
    }

    public ElementCreatorType(String email, String smartspace) {
        this.email = email;
        this.smartspace = smartspace;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSmartspace() {
        return smartspace;
    }

    public void setSmartspace(String smartspace) {
        this.smartspace = smartspace;
    }
}
