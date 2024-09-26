package smartcv.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smartcv.auth.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmailAndPassword(String email, String password);
    User findByEmail(String email);
    User findByMatricule(String matricule);



}

