package smartcv.auth.menu;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import smartcv.auth.feedback.Feedback;
import smartcv.auth.model.User;
import smartcv.auth.reservation.Reservation;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Setter
@Getter
@Table(name = "menu")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")

public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Temporal(TemporalType.DATE)
    private Date menuDate;

    private String entree;
    private String mainCourse;  // plat principal
    private String garnish;     // garniture
    private String dessert;

    @ElementCollection
    @CollectionTable(name = "sandwiches", joinColumns = @JoinColumn(name = "menu_id"))
    @Column(name = "sandwich")
    private List<String> sandwiches;

    @CollectionTable
    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Feedback> feedbacks;


    @ManyToOne
    private User user;  // The user who created the menu

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;
}