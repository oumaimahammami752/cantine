package smartcv.auth.menu;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Integer> {
    // Custom query methods can be added here if needed
}
