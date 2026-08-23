package in.pranay.billingsoftware.service;

import in.pranay.billingsoftware.io.UserRequest;
import in.pranay.billingsoftware.io.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest request);

    String getUserRole(String email);

    List<UserResponse> readUsers();

    void deleteUser(String id);
}
