package smartspace.layout;

import smartspace.data.UserEntity;
import smartspace.data.UserRole;


public class UserBoundary {
    private UserKeyType key;
    private String username;
    private String avatar;
    private String role;
    private long points;


    public UserBoundary() {

    }

    public UserBoundary(UserEntity userEntity) {


        this.key = new UserKeyType(userEntity.getUserEmail(), userEntity.getUserSmartspace());
        this.username = userEntity.getUsername();
        this.avatar = userEntity.getAvatar();
        this.role = userEntity.getRole().name();
        this.points = userEntity.getPoints();
    }

    public UserKeyType getKey() {
        return this.key;
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

    public void setUserKey(UserKeyType key) {
        this.key = key;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setRole(String role) {
        this.role = role.toUpperCase();
    }

    public void setPoints(long points) {
        this.points = points;
    }


    public UserEntity convertToEntity() {
        UserEntity entity = new UserEntity();

        if (this.key != null) {
            entity.setUserEmail(this.key.getEmail());
            entity.setUserSmartspace(this.key.getSmartspace());
            entity.setKey(this.key.getEmail() + "#" + this.key.getSmartspace());
        }

        //role is enum
        if (this.role != null) {
            entity.setRole(UserRole.valueOf(this.getRole()));
        } else {
            entity.setRole(null);
        }

        entity.setUsername(this.getUsername());

        entity.setAvatar(this.getAvatar());

        entity.setPoints(this.getPoints());

        return entity;
    }


}
