package smartcv.auth.service;

import smartcv.auth.reservation.Reservation;

import java.util.Date;

public interface ReservationService {
     Reservation makeReservation(int userId, int menuId, Date reservationDate);
}
