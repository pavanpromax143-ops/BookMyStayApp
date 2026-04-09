import java.util.*;

/* ------------------ BOOKING REQUEST ------------------ */
class BookingRequest {
    String guestName;
    String roomType;

    public BookingRequest(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

/* ------------------ SHARED HOTEL SYSTEM ------------------ */
class HotelSystem {
    private Map<String, Integer> inventory = new HashMap<>();
    private Queue<BookingRequest> requestQueue = new LinkedList<>();

    public void addInventory(String type, int count) {
        inventory.put(type, count);
    }

    public void submitRequest(BookingRequest request) {
        synchronized (requestQueue) {
            requestQueue.add(request);
        }
    }

    public BookingRequest getNextRequest() {
        synchronized (requestQueue) {
            return requestQueue.poll();
        }
    }

    /* Critical Section: Only one thread can allocate a room at a time */
    public synchronized void processBooking(BookingRequest request) {

        System.out.println(Thread.currentThread().getName()
                + " processing " + request.guestName
                + " for " + request.roomType);

        if (!inventory.containsKey(request.roomType)) {
            System.out.println("REJECTED: Invalid room type");
            return;
        }

        int available = inventory.get(request.roomType);

        if (available > 0) {
            inventory.put(request.roomType, available - 1);
            System.out.println("CONFIRMED: " + request.guestName
                    + " got " + request.roomType);
        } else {
            System.out.println("REJECTED: No rooms left for "
                    + request.guestName);
        }
    }

    public void printInventory() {
        System.out.println("\nFinal Inventory State:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " -> " + inventory.get(type));
        }
    }
}

/* ------------------ WORKER THREAD ------------------ */
class BookingProcessor extends Thread {
    private HotelSystem hotel;

    public BookingProcessor(HotelSystem hotel, String name) {
        super(name);
        this.hotel = hotel;
    }

    public void run() {
        while (true) {
            BookingRequest req = hotel.getNextRequest();
            if (req == null) break;
            hotel.processBooking(req);
        }
    }
}

/* ------------------ MAIN APPLICATION ------------------ */
 class BookMyStayApp {

    public static void main(String[] args) throws InterruptedException {

        HotelSystem hotel = new HotelSystem();

        hotel.addInventory("Suite", 1);
        hotel.addInventory("Single", 2);

        /* Simulating concurrent guest requests */
        hotel.submitRequest(new BookingRequest("Alice", "Suite"));
        hotel.submitRequest(new BookingRequest("Bob", "Suite"));   // competing
        hotel.submitRequest(new BookingRequest("Charlie", "Single"));
        hotel.submitRequest(new BookingRequest("David", "Single"));
        hotel.submitRequest(new BookingRequest("Eve", "Single"));  // overbook attempt

        /* Multiple worker threads */
        BookingProcessor t1 = new BookingProcessor(hotel, "Thread-1");
        BookingProcessor t2 = new BookingProcessor(hotel, "Thread-2");
        BookingProcessor t3 = new BookingProcessor(hotel, "Thread-3");

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        hotel.printInventory();
    }
}