import java.util.*;

/**
 * ============================================================
 * MAIN CLASS - UseCase10BookingCancellation
 * Use Case 10: Booking Cancellation & Inventory Rollback
 * ============================================================
 */

 class UseCase10BookingCancellation {

    // ------------------- RoomInventory -------------------
    static class RoomInventory {
        private Map<String, Integer> inventory;

        public RoomInventory() {
            inventory = new HashMap<>();
            inventory.put("Single", 5); // initial rooms
            inventory.put("Double", 3);
        }

        public void increaseRoom(String roomType) {
            inventory.put(roomType, inventory.getOrDefault(roomType, 0) + 1);
        }

        public int getAvailability(String roomType) {
            return inventory.getOrDefault(roomType, 0);
        }
    }

    // ------------------- CancellationService -------------------
    static class CancellationService {

        // Stack for rollback history
        private Stack<String> releasedRoomIds;

        // Map reservation -> room type
        private Map<String, String> reservationRoomTypeMap;

        public CancellationService() {
            releasedRoomIds = new Stack<>();
            reservationRoomTypeMap = new HashMap<>();
        }

        // Register booking
        public void registerBooking(String reservationId, String roomType) {
            reservationRoomTypeMap.put(reservationId, roomType);
        }

        // Cancel booking
        public void cancelBooking(String reservationId, RoomInventory inventory) {

            if (!reservationRoomTypeMap.containsKey(reservationId)) {
                System.out.println("Invalid Reservation ID!");
                return;
            }

            String roomType = reservationRoomTypeMap.get(reservationId);

            System.out.println("Booking Cancellation");

            // Restore inventory
            inventory.increaseRoom(roomType);

            // Push into stack (rollback)
            releasedRoomIds.push(reservationId);

            // Remove booking
            reservationRoomTypeMap.remove(reservationId);

            System.out.println("Booking cancelled successfully. Inventory restored for room type: " + roomType);
        }

        // Show rollback history
        public void showRollbackHistory() {
            System.out.println("\nRollback History (Most Recent First):");

            ListIterator<String> it = releasedRoomIds.listIterator(releasedRoomIds.size());
            while (it.hasPrevious()) {
                System.out.println("Released Reservation ID: " + it.previous());
            }
        }
    }

    // ------------------- MAIN METHOD -------------------
    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        CancellationService service = new CancellationService();

        // Step 1: Register booking
        service.registerBooking("Single-1", "Single");

        // Step 2: Cancel booking
        service.cancelBooking("Single-1", inventory);

        // Step 3: Show rollback history
        service.showRollbackHistory();

        // Step 4: Show updated availability
        System.out.println("\nUpdated Single Room Availability: " +
                inventory.getAvailability("Single"));
    }
}