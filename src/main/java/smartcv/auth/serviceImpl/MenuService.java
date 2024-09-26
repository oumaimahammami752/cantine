package smartcv.auth.serviceImpl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smartcv.auth.menu.Menu;
import smartcv.auth.menu.MenuRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MenuService {

    @Autowired
    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    // Method to get all menus
    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    public Menu addMenu(Menu menu) {
        return menuRepository.save(menu);
    }

    public Menu updateMenu(int id, Menu menuDetails) {
        return menuRepository.findById(id).map(menu -> {
            menu.setMenuDate(menuDetails.getMenuDate());
            menu.setEntree(menuDetails.getEntree());
            menu.setMainCourse(menuDetails.getMainCourse());
            menu.setGarnish(menuDetails.getGarnish());
            menu.setDessert(menuDetails.getDessert());
            menu.setSandwiches(menuDetails.getSandwiches());
            // If needed, you can update the user or other fields
            return menuRepository.save(menu);
        }).orElseThrow(() -> new NoSuchElementException("Menu not found with id " + id));
    }


    @Transactional
    public boolean deleteMenu(int menuId) {
        if (menuRepository.existsById(menuId)) {
            menuRepository.deleteById(menuId);
            return true;
        } else {
            return false;
        }
    }
}
