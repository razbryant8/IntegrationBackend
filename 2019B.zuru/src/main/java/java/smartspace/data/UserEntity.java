package smartspace.data;

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

    public String getUserSmartspace() {
        return userSmartspace;
    }

    public void setUserSmartspace(String userSmartspace) {
        this.userSmartspace = userSmartspace;
    }

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
