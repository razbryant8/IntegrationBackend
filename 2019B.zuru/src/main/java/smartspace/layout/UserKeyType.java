package smartspace.layout;

import java.util.Objects;

public class UserKeyType {
    private String email;
    private String smartspace;

    public UserKeyType() {
    }

    public UserKeyType(String email, String smartspace) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserKeyType)) return false;
        UserKeyType that = (UserKeyType) o;
        return getEmail().equals(that.getEmail()) &&
                getSmartspace().equals(that.getSmartspace());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail(), getSmartspace());
    }
}
