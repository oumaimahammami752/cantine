package smartcv.auth.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smartcv.auth.menu.Menu;
import smartcv.auth.menu.MenuRepository;
import smartcv.auth.model.User;
import smartcv.auth.repository.UserRepository;
import smartcv.auth.reservation.Reservation;
import smartcv.auth.reservation.ReservationRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuRepository menuRepository;

    public Reservation makeReservation(long userId, Date reservationDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        Date oneMonthFromNow = calendar.getTime();

        // Check if the reservation date is within one month
        if (reservationDate.after(oneMonthFromNow)) {
            System.out.println("Reservation date cannot be more than a month from now.");
            return null;  // Or throw an exception if that's more appropriate for your use case
        }
        Optional<User> userOpt = userRepository.findById(userId);
       // Optional<Menu> menuOpt = menuRepository.findById(menuId);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            Reservation reservation = new Reservation();
            reservation.setUser(user);
            reservation.setReservationDate(reservationDate);

            // Set cancellation deadline to one day before the reservation date
            Date cancellationDeadline = new Date(reservationDate.getTime() - (24 * 60 * 60 * 1000));
            reservation.setCancellationDeadline(cancellationDeadline);

            reservation.setCancelled(false);

            return reservationRepository.save(reservation);
        }

        return null;  // Return null if user or menu is not found
    }

    public boolean cancelReservation(int reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);

        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();

            // Check if reservation is not already canceled and if current date is before the cancellation deadline
            if (!reservation.isCancelled() && new Date().before(reservation.getCancellationDeadline())) {
                reservation.setCancelled(true);
                reservationRepository.save(reservation);
                return true;
            }
        }

        return false; // Reservation not found or cannot be canceled
    }


    public List<Reservation> getReservationsByUserId(long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    //public List<Reservation> getReservationsByEmail(String email) {
      //  return reservationRepository.findByUserEmail(email);
    //}


}
