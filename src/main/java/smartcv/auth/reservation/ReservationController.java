package smartcv.auth.reservation;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smartcv.auth.model.User;
import smartcv.auth.repository.UserRepository;
import smartcv.auth.serviceImpl.ReservationService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Optional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationService reservationService;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @PostMapping("/make")
    public ResponseEntity<Reservation> makeReservation(
            @RequestParam long userId,
            @RequestParam Date reservationDate) {

        // Convert the reservationDate string to Date
        // Date date = dateFormat.parse(reservationDate);

        Reservation reservation = reservationService.makeReservation(userId, reservationDate);

        if (reservation != null) {
            return ResponseEntity.ok(reservation);
        } else {
            return ResponseEntity.badRequest().build();  // Return 400 if user or menu not found
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<Void> cancelReservation(@RequestParam int reservationId) {
        boolean success = reservationService.cancelReservation(reservationId);

        if (success) {
            return ResponseEntity.ok().build(); // Return 200 OK if cancellation was successful
        } else {
            return ResponseEntity.badRequest().build(); // Return 400 Bad Request if reservation cannot be canceled
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getReservationsByUserId(@PathVariable long userId) {
        Optional<List<Reservation>> reservationsOpt = Optional.ofNullable(reservationService.getReservationsByUserId(userId));

        if (reservationsOpt.isPresent()) {
            List<Reservation> reservations = reservationsOpt.get();
            List<Map<String, Object>> reservationList = new ArrayList<>();

            for (Reservation reservation : reservations) {
                Map<String, Object> reservationData = new HashMap<>();
                reservationData.put("id", reservation.getId());
                reservationData.put("reservationDate", reservation.getReservationDate());
                reservationData.put("cancellationDeadline", reservation.getCancellationDeadline());
                reservationData.put("isCancelled", reservation.isCancelled());
       //         reservationData.put("menu", reservation.getMenu().getId());  // Return only menu ID or other necessary fields

                reservationList.add(reservationData);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("reservations", reservationList);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();

        List<Map<String, Object>> response = reservations.stream().map(reservation -> {
            Map<String, Object> reservationData = new HashMap<>();
            reservationData.put("id", reservation.getId());
            reservationData.put("reservationDate", reservation.getReservationDate());
            reservationData.put("cancellationDeadline", reservation.getCancellationDeadline());
            reservationData.put("isCancelled", reservation.isCancelled());
            reservationData.put("user", reservation.getUser().getFirstName()); // Return only user ID
            reservationData.put("email",reservation.getUser().getEmail());
            return reservationData;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/checkReservation")
    public String checkReservation(@RequestParam String matricule) {
        User user = userRepository.findByMatricule(matricule);

        if (user != null) {
            Date today = new Date();
            boolean hasReservationToday = user.getReservations().stream()
                    .anyMatch(reservation -> isSameDay(reservation.getReservationDate(), today) && !reservation.isCancelled());

            if (hasReservationToday) {
                return "Welcome!";
            } else {
                return "You don't have a reservation.";
            }
        } else {
            return "User not found.";
        }
    }
    private boolean isSameDay(Date date1, Date date2) {
        return date1.getYear() == date2.getYear() &&
                date1.getMonth() == date2.getMonth() &&
                date1.getDate() == date2.getDate();
    }

    @GetMapping("/generate-pdf")
    public ResponseEntity<byte[]> generatePDF(@RequestParam String matricule) {
        // Fetch user based on matricule
        User user = userRepository.findByMatricule(matricule);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Fetch user's name, today's date, and set price
        String userName = user.getFirstName() + " " + user.getLastName();
        Date today = new Date();
        String price = "1,200 TND";

        // Generate PDF
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(out);
            com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            Document document = new Document(pdf);

            // Add content to the PDF
            document.add(new Paragraph("Reservation Details")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Name: " + userName)
                    .setFontSize(12));
            document.add(new Paragraph("Date: " + today.toString())
                    .setFontSize(12));
            document.add(new Paragraph("Price: " + price)
                    .setFontSize(12));

            document.close();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }

        // Prepare PDF for download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "reservation.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(out.toByteArray());
    }

}
