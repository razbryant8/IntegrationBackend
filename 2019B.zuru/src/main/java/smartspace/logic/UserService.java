package smartspace.logic;

import smartspace.data.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {

    public List<UserEntity> getAll(int size, int page);

    public UserEntity store(UserEntity user);

    public Optional<UserEntity> getUserByKey(String key);

    public Optional<UserEntity> getUserByMailAndSmartSpace(String email, String smartSpace);

    public String getCurrentSmartspace();

    public UserEntity create(UserEntity user);

    public void update(String userSmartspace,String userEmail, UserEntity convertToEntity);

}
