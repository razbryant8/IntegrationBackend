package smartspace.layout;

import java.util.Objects;

public class UserKey {

    private String email;

    private String smartspace;


    public UserKey() {

    }

    public UserKey(String email, String smartspace) {
        this.email = email;
        this.smartspace = smartspace;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSmartspace(String smartspace) {
        this.smartspace = smartspace;
    }

    public String getEmail() {
        return email;
    }

    public String getSmartspace() {
        return smartspace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserKey)) return false;
        UserKey userKey = (UserKey) o;
        return getEmail().equals(userKey.getEmail()) &&
                getSmartspace().equals(userKey.getSmartspace());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail(), getSmartspace());
    }
}
