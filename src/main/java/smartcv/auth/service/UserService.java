package smartcv.auth.service;


import org.springframework.security.core.userdetails.UserDetailsService;
import smartcv.auth.model.User;

public interface UserService extends UserDetailsService {
    User createUser(User user);
    User getUserById(Long id);
    User getUserByEmailAndPassword(String email, String password);
    User findByEmail(String email);
}
