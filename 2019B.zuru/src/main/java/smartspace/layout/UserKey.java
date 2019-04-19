package smartspace.layout;

import java.util.Objects;

public class UserKey {

    private String email;

    private String smartspce;


    public UserKey() {

    }

    public UserKey(String email, String smartspce) {
        this.email = email;
        this.smartspce = smartspce;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSmartspce(String smartspce) {
        this.smartspce = smartspce;
    }

    public String getEmail() {
        return email;
    }

    public String getSmartspce() {
        return smartspce;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserKey)) return false;
        UserKey userKey = (UserKey) o;
        return getEmail().equals(userKey.getEmail()) &&
                getSmartspce().equals(userKey.getSmartspce());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail(), getSmartspce());
    }
}
