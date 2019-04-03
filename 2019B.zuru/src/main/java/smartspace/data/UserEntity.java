package smartspace.data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "USERS")
public class UserEntity {
    private String userSmartspace;
    private String userEmail;
    private String username;
    private String avatar;
    private UserRole role;
    private long points;

    public UserEntity() {

    }

    public UserEntity(String userEmail, String userSmartspace, String username, String avatar, UserRole role, long points) {
        this.userEmail = userEmail;
        this.userSmartspace = userSmartspace;
        this.username = username;
        this.avatar = avatar;
        this.role = role;
        this.points = points;
    }

    @Transient
    public String getUserSmartspace() {
        return userSmartspace;
    }

    public void setUserSmartspace(String userSmartspace) {
        this.userSmartspace = userSmartspace;
    }

    @Id
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Transient
    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }


}
