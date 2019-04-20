package smartspace.layout;

import smartspace.data.UserEntity;
import smartspace.data.UserRole;

import java.util.HashMap;
import java.util.Map;

public class UserBoundary {
    private UserKey key;
    private String userSmartspace;
    private String userEmail;
    private String username;
    private String avatar;
    private String role;
    private long points;


    public UserBoundary() {

    }

    public UserBoundary(UserEntity userEntity) {


        this.userSmartspace = userEntity.getUserSmartspace();
        this.userEmail = userEntity.getUserEmail();
        this.key = new UserKey(this.userEmail, this.userSmartspace);
        this.username = userEntity.getUsername();
        this.avatar = userEntity.getAvatar();
        this.role = userEntity.getRole().name();
        this.points = userEntity.getPoints();
    }

    public UserKey getKey() { return this.key; }

    public String getUserSmartspace() {
        return userSmartspace;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getRole() {
        return role;
    }

    public long getPoints() {
        return points;
    }

    private void UserKey(UserKey key) { this.key = key; }

    public void setUserSmartspace(String userSmartspace) {
        this.userSmartspace = userSmartspace;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setRole(String role) { this.role = role; }

    public void setPoints(long points) {
        this.points = points;
    }


    public UserEntity convertToEntity() {
        UserEntity entity = new UserEntity();

        if (this.key != null) {
            entity.setUserEmail(this.key.getEmail());
            entity.setUserSmartspace(this.key.getSmartspace());
            entity.setKey(this.key.getEmail()+"#"+this.key.getSmartspace());
        }

        //role is enum
        if (this.role != null) {
            entity.setRole(UserRole.valueOf(this.role));
        } else {
            entity.setRole(null);
        }

        entity.setUsername(this.username);

        entity.setAvatar(this.avatar);

        entity.setPoints(this.points);

        return entity;
    }


}
