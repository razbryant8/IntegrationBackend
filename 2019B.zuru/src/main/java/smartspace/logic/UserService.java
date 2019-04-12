package smartspace.logic;

import smartspace.data.UserEntity;

import java.util.List;

public interface UserService {

    public List<UserEntity> getAll(int size, int page);

    public UserEntity store(UserEntity user);
}
