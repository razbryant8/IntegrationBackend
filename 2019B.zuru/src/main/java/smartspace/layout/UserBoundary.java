package smartspace.layout;

import smartspace.data.UserEntity;
import smartspace.data.UserRole;

public class UserBoundary {

    private String userEmail;
    private String username;
    private String avatar;
    private UserRole role;
    private long points;


    public UserBoundary() {

    }

    public UserBoundary(UserEntity userEntity) {
        this.userEmail = userEntity.getUserEmail();
        this.username = userEntity.getUsername();
        this.avatar = userEntity.getAvatar();
        if (userEntity.getRole() != null) {
            this.role = userEntity.getRole();
        } else {
            this.role = null;
        }
        this.points = userEntity.getPoints();
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

    public UserRole getRole() {
        return role;
    }

    public long getPoints() {
        return points;
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

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    /*
need to complete
    public UserEntity convertToEntity() {
        UserEntity entity = new UserEntity();

        entity.setRole(null);
        if (this.role != null && this.author.contains("#")) {
            String[] args = this.author.split("#");
            if (args.length == 2) {
                entity.setAuthor(new Name(args[0], args[1]));
            }
        }

        entity.setDetails(this.details);

        entity.setKey(this.key);

        if (this.type != null) {
            entity.setMessageType(MessageType.valueOf(this.type));
        } else {
            entity.setMessageType(null);
        }

        entity.setText(this.text);

        entity.setTimestamp(this.timestamp);

        return entity;
    }
*/


}
