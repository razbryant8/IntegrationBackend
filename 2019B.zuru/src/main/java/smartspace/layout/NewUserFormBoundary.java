package smartspace.layout;

import smartspace.data.UserEntity;
import smartspace.data.UserRole;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewUserFormBoundary {

    private String email;
    private String username;
    private String role;
    private String avatar;



    public NewUserFormBoundary() {

    }

    public NewUserFormBoundary(String email, String username, String role, String avatar) {
        this.email = email;
        this.username = username;
        this.role = role;
        this.avatar = avatar;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(String role) {
        this.role = role.toUpperCase();
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getAvatar() {
        return avatar;
    }


    public UserEntity convertToEntity() {
        UserEntity entity = new UserEntity();

        if (this.email != null) {
            entity.setUserEmail(this.getEmail());
        }

        //role is enum
        if (this.role != null) {
            entity.setRole(UserRole.valueOf(this.getRole()));
        } else {
            entity.setRole(null);
        }

        entity.setUsername(this.getUsername());

        entity.setAvatar(this.getAvatar());

        switch (entity.getRole()){
            case ADMIN:
                entity.setPoints(-1);
                break;
            case MANAGER:
                entity.setPoints(-1);
                break;
            case PLAYER:
                entity.setPoints(0);
                break;
        }

        //smartspace hard-coded for now - need to fix
        entity.setUserSmartspace("2019b.zuru");

        entity.setKey(entity.getUserEmail()+"#"+entity.getUserSmartspace());


        return entity;
    }



}
