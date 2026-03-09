import java.util.*;

abstract class Room {

    protected String roomType;
    protected int beds;
    protected int size;
    protected double price;

    public Room(String roomType, int beds, int size, double price) {
        this.roomType = roomType;
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    public String getRoomType() {
        return roomType;
    }

    public void displayRoomDetails() {
        System.out.println("Room Type: " + roomType);
        System.out.println("Beds: " + beds);
        System.out.println("Size: " + size + " sq ft");
        System.out.println("Price per night: $" + price);
    }
}

class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 200, 80.0);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 350, 150.0);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 600, 300.0);
    }
}

class RoomInventory {

    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 1);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void decrementRoom(String roomType) {
        int count = inventory.get(roomType);
        if (count > 0) {
            inventory.put(roomType, count - 1);
        }
    }

    public void displayInventory() {
        for (String type : inventory.keySet()) {
            System.out.println(type + " Remaining: " + inventory.get(type));
        }
    }
}

class Reservation {

    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

class BookingRequestQueue {

    private Queue<Reservation> queue;

    public BookingRequestQueue() {
        queue = new LinkedList<>();
    }

    public void addRequest(Reservation r) {
        queue.add(r);
    }

    public Reservation getNextRequest() {
        return queue.poll();
    }

    public boolean hasRequests() {
        return !queue.isEmpty();
    }
}

class BookingService {

    private RoomInventory inventory;
    private HashMap<String, Set<String>> allocatedRooms;
    private int roomCounter = 1;

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
        allocatedRooms = new HashMap<>();
    }

    public void processReservation(Reservation reservation) {

        String roomType = reservation.getRoomType();

        if (inventory.getAvailability(roomType) <= 0) {
            System.out.println("Reservation failed for " + reservation.getGuestName() + " - No rooms available.");
            return;
        }

        String roomId = roomType.replace(" ", "") + "-" + roomCounter++;
        allocatedRooms.putIfAbsent(roomType, new HashSet<>());
        Set<String> assigned = allocatedRooms.get(roomType);

        if (!assigned.contains(roomId)) {
            assigned.add(roomId);
            inventory.decrementRoom(roomType);
            System.out.println("Reservation confirmed for " + reservation.getGuestName() + " | Room ID: " + roomId);
        }
    }
}

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("==================================");
        System.out.println("     Book My Stay Application     ");
        System.out.println("     Hotel Booking System v6.0    ");
        System.out.println("==================================");

        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue queue = new BookingRequestQueue();
        BookingService bookingService = new BookingService(inventory);

        queue.addRequest(new Reservation("Alice", "Single Room"));
        queue.addRequest(new Reservation("Bob", "Double Room"));
        queue.addRequest(new Reservation("Charlie", "Suite Room"));
        queue.addRequest(new Reservation("David", "Single Room"));
        queue.addRequest(new Reservation("Eva", "Suite Room"));

        System.out.println("\nProcessing Reservations\n");

        while (queue.hasRequests()) {
            Reservation r = queue.getNextRequest();
            bookingService.processReservation(r);
        }

        System.out.println("\nRemaining Inventory\n");
        inventory.displayInventory();
    }
}