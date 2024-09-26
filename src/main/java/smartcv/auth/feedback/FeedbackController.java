package smartcv.auth.feedback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smartcv.auth.menu.Menu;
import smartcv.auth.menu.MenuRepository;
import smartcv.auth.serviceImpl.FeedbackService;
import smartcv.auth.serviceImpl.MenuService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private MenuRepository menuRepo;
    @PostMapping("/add")
    public ResponseEntity<Feedback> addFeedback(
            @RequestParam Feedback.FeedbackType feedbackType,
            @RequestParam int menuId
    ) {
        // Retrieve Menu entity using the provided ID
        Menu menu = menuRepo.findById(menuId)
                .orElseThrow(() -> new NoSuchElementException("Menu not found"));

        // Create Feedback entity
        Feedback feedback = new Feedback();
        feedback.setFeedbackType(feedbackType);
        feedback.setMenu(menu);

        // Save Feedback
        Feedback savedFeedback = feedbackService.addFeedback(feedback);
        return ResponseEntity.ok(savedFeedback);
    }


    @GetMapping("/all")
    public ResponseEntity<List<Feedback>> getAllFeedbacks() {
        List<Feedback> feedbacks = feedbackService.getAllFeedbacks();
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/menu/{menuId}")
    public ResponseEntity<List<Feedback>> getFeedbacksByMenuId(@PathVariable int menuId) {
        List<Feedback> feedbacks = feedbackService.getFeedbacksByMenuId(menuId);
        return ResponseEntity.ok(feedbacks);
    }
}
