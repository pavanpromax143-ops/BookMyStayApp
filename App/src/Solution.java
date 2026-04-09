import java.util.*;

/* ------------------ CUSTOM EXCEPTIONS ------------------ */
class InvalidRoomTypeException extends Exception {
    public InvalidRoomTypeException(String message) { super(message); }
}

class InsufficientInventoryException extends Exception {
    public InsufficientInventoryException(String message) { super(message); }
}

/* ------------------ VALIDATOR ------------------ */
class BookingValidator {
    public static void validateRequest(String type, Map<String, Integer> inventory)
            throws InvalidRoomTypeException, InsufficientInventoryException {

        if (!inventory.containsKey(type))
            throw new InvalidRoomTypeException("Error: Room type '" + type + "' does not exist.");

        if (inventory.get(type) <= 0)
            throw new InsufficientInventoryException("Error: No vacancy for '" + type + "'.");
    }
}

/* ------------------ RESERVATION MODEL ------------------ */
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

/* ------------------ BOOKING HISTORY ------------------ */
class BookingHistory {
    private List<Reservation> history = new ArrayList<>();

    public void addReservation(Reservation r) {
        history.add(r); // insertion order preserved
    }

    public List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(history);
    }
}

/* ------------------ REPORT SERVICE ------------------ */
class BookingReportService {

    public static void printAllBookings(BookingHistory history) {
        System.out.println("\n--- Booking History Report ---");

        for (Reservation r : history.getAllReservations()) {
            System.out.println("Guest: " + r.getGuestName()
                    + " | Room: " + r.getRoomType());
        }
    }

    public static void printSummary(BookingHistory history) {
        Map<String, Integer> summary = new HashMap<>();

        for (Reservation r : history.getAllReservations()) {
            summary.put(r.getRoomType(),
                    summary.getOrDefault(r.getRoomType(), 0) + 1);
        }

        System.out.println("\n--- Booking Summary ---");
        for (String type : summary.keySet()) {
            System.out.println(type + " booked: " + summary.get(type));
        }
    }
}

/* ------------------ CORE SYSTEM ------------------ */
class HotelSystem {
    private Map<String, Integer> inventory = new HashMap<>();
    private BookingHistory history = new BookingHistory();

    public void addInventory(String type, int count) {
        inventory.put(type, count);
    }

    public BookingHistory getHistory() {
        return history;
    }

    public void processBooking(String guest, String type) {
        System.out.println("[PROCESSING] " + guest + " requesting " + type + "...");

        try {
            BookingValidator.validateRequest(type, inventory);

            inventory.put(type, inventory.get(type) - 1);
            System.out.println("SUCCESS: Booking confirmed for " + guest);

            // Store in history
            history.addReservation(new Reservation(guest, type));

        } catch (InvalidRoomTypeException | InsufficientInventoryException e) {
            System.err.println("REJECTED: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("CRITICAL: An unexpected error occurred.");
        }

        System.out.println("-------------------------------------------");
    }
}

/* ------------------ MAIN APPLICATION ------------------ */
 class BookMyStayApp {
    public static void main(String[] args) {

        HotelSystem hotel = new HotelSystem();
        hotel.addInventory("Suite", 1);
        hotel.addInventory("Single", 5);

        System.out.println("Hotel Booking System with History Tracking\n");

        hotel.processBooking("Alice", "Suite");
        hotel.processBooking("Bob", "Suite");       // rejected
        hotel.processBooking("Charlie", "Single");
        hotel.processBooking("David", "Penthouse"); // rejected

        // Admin viewing reports
        BookingReportService.printAllBookings(hotel.getHistory());
        BookingReportService.printSummary(hotel.getHistory());
    }
}