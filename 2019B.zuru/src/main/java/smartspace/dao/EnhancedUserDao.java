package smartspace.dao;

import smartspace.data.UserEntity;

import java.util.List;

public interface EnhancedUserDao<k> extends UserDao<k> {
    public List<UserEntity> readAll(int size, int page);

    public List<UserEntity> readAll(int size, int page, String sortBy);

    public List<UserEntity> getUserByRole(
            String role, int size, int page);

}
