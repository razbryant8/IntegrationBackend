package smartspace.data;

import javax.persistence.*;

@Entity
@Table(name = "USERS")
public class UserEntity implements SmartspaceEntity<String> {
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


    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public UserRole getRole() {
        return role;
    }


    @Transient
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUsername() {
        return username;
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

    @Column(name = "ID")
    @Id
    @Override
    public String getKey() {
        return getUserEmail() + "#" + getUserSmartspace();
    }

    @Override
    public void setKey(String key) {
        if(key != null) {
            String[] args = key.split("#");
            if (args.length == 2) {
                this.setUserEmail(args[0]);
                this.setUserSmartspace(args[1]);
            }
        }

    }
}
