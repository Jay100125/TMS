package org.project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.server.RequestHandler;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class TrainManagementSystemTest {
  private ConcurrentHashMap<String, Train> trainMap;
  private ConcurrentHashMap<String, Map<String, String>> bookings;
  private RequestHandler requestHandler;

  @BeforeEach
  void setUp() {
    trainMap = new ConcurrentHashMap<>();
    bookings = new ConcurrentHashMap<>();

    // Create sample train data
    Train train = new Train("12345", "SA", "SB",
      LocalDate.of(2025, 10, 10), LocalDate.of(2025, 10, 11));
    train.addCoach("Sleeper", "C1", 5);
    train.addCoach("Sleeper", "C2", 5);
    train.addCoach("Sleeper", "C3", 5);
    train.addCoach("AC", "A1", 5);
    trainMap.put(train.getTrainId(), train);

    // Mock socket for RequestHandler
    Socket mockSocket = new Socket() {
      @Override
      public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream("".getBytes());
      }
      @Override
      public OutputStream getOutputStream() throws IOException {
        return new ByteArrayOutputStream();
      }
    };

    requestHandler = new RequestHandler(mockSocket, trainMap, bookings);
  }

  // Train Tests
  @Test
  void testTrainCreation() {
    Train train = trainMap.get("12345");
    assertEquals("12345", train.getTrainId());
    assertEquals("SA", train.getSource());
    assertEquals("SB", train.getDestination());
    assertEquals(LocalDate.of(2025, 10, 10), train.getDepartureDate());
    assertEquals(LocalDate.of(2025, 10, 11), train.getArrivalDate());
  }

  @Test
  void testAddCoach() {
    Train train = trainMap.get("12345");
    assertEquals(2, train.getCoachTypes().size());
    assertEquals(3, train.getCoachTypes().get("sleeper").size());
    assertEquals(1, train.getCoachTypes().get("ac").size());
  }

  // Coach Tests
  @Test
  void testCoachInitialization() {
    Coach coach = new Coach("Sleeper", "C1", 5);
    assertEquals("Sleeper", coach.getType());
    assertEquals("C1", coach.getCoachId());
    assertEquals(5, coach.getAvailableSeatCount());
  }

  @Test
  void testPollAndBookSeat() {
    Coach coach = new Coach("Sleeper", "C1", 5);
    String pnr = "123";

    String seat = coach.pollAndBookSeat(pnr);
    assertNotNull(seat);
    assertEquals(4, coach.getAvailableSeatCount());
    assertEquals(pnr, coach.getSeatBookings().get(seat));
  }

  @Test
  void testPollAndBookSeatNoSeats() {
    Coach coach = new Coach("Sleeper", "C1", 0);
    String pnr = "123";

    String seat = coach.pollAndBookSeat(pnr);
    assertNull(seat);
  }

  // RequestHandler Tests
  @Test
  void testHandleSearchValid() {
    String[] parts = {"SEARCH", "SA", "SB", "2025-10-10"};
    String response = invokeHandleSearch(parts);

    assertNotNull(response);
    assertTrue(response.contains("Available Trains"));
    assertTrue(response.contains("Train ID: 12345"));
  }

  @Test
  void testHandleSearchNoTrains() {
    String[] parts = {"SEARCH", "SA", "SC", "2025-10-10"};
    String response = invokeHandleSearch(parts);

    assertEquals("No trains available for the given criteria.", response);
  }

  @Test
  void testHandleSearchInvalidDate() {
    String[] parts = {"SEARCH", "SA", "SB", "invalid-date"};
    String response = invokeHandleSearch(parts);

    assertEquals("Invalid date format", response);
  }

  @Test
  void testHandleSearchPastDate() {
    String[] parts = {"SEARCH", "SA", "SB", "2023-01-01"};
    String response = invokeHandleSearch(parts);

    assertEquals("Date cannot be in the past", response);
  }

  @Test
  void testHandleBookingSuccess() throws IOException {
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    Socket mockSocket = new Socket() {
      @Override
      public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream("BOOK user1 12345 Sleeper 2\n".getBytes());
      }
      @Override
      public OutputStream getOutputStream() throws IOException {
        return outStream;
      }
    };

    RequestHandler handler = new RequestHandler(mockSocket, trainMap, bookings);
    handler.run();

    String response = outStream.toString();
    assertTrue(response.contains("Booking successful"));
    assertTrue(response.contains("PNR"));
    assertTrue(response.contains("Seats"));
  }


  @Test
  void testHandleBookingInvalidTrain() {
    String[] parts = {"BOOK", "user1", "99999", "Sleeper", "2"};
    String response = invokeHandleBooking(parts);

    assertEquals("Train not found", response);
  }

  @Test
  void testHandleBookingInvalidCoachType() {
    String[] parts = {"BOOK", "user1", "12345", "FirstClass", "2"};
    String response = invokeHandleBooking(parts);

    assertEquals("Coach not found", response);
  }

  @Test
  void testHandleBookingInvalidNumber() {
    String[] parts = {"BOOK", "user1", "12345", "Sleeper", "0"};
    String response = invokeHandleBooking(parts);

    assertEquals("Invalid number of seats", response);
  }

  @Test
  void testHandleCancellationSuccess() {
    String[] bookParts = {"BOOK", "user1", "12345", "Sleeper", "2"};
    String bookResponse = invokeHandleBooking(bookParts);
    String pnr = bookResponse.split("PNR: ")[1].split(" ")[0];
    // Step 2: Cancel the booking
    String[] cancelParts = {"CANCEL", pnr};
    String response = invokeHandleCancellation(cancelParts);

    // Step 3: Verify the cancellation
    assertEquals("Booking with PNR: " + pnr + " cancelled successfully.", response);
    assertNull(bookings.get(pnr)); // Booking should be removed from bookings map

    // Optional: Verify seats are released (if you want to test this explicitly)
    List<Coach> sleeperCoaches = trainMap.get("12345").getCoachTypes().get("sleeper");
    int totalAvailableSeats = sleeperCoaches.stream()
      .mapToInt(coach -> coach.getAvailableSeatCount())
      .sum();
    assertEquals(15, totalAvailableSeats);
  }

  @Test
  void testHandleCancellationNotFound() {
    String[] parts = {"CANCEL", "999"};
    String response = invokeHandleCancellation(parts);

    assertEquals("Booking not found", response);
  }

  @Test
  void testConcurrentBooking100Seats500Users() throws InterruptedException {
    Coach coach = new Coach("Sleeper", "C1", 1000);
    int totalUsers = 11500;
    int seatsPerUser = 3;
    AtomicInteger successfulBookings = new AtomicInteger(0);

    ExecutorService executorService = Executors.newFixedThreadPool(5);
    CountDownLatch latch = new CountDownLatch(totalUsers);

    long startTime = System.nanoTime();

    for (int i = 0; i < totalUsers; i++) {
      String userId = "user" + i;
      String pnr = String.valueOf(1000 + i);

      executorService.submit(() -> {
        try {
          List<String> confirmedSeats = new ArrayList<>();
          while (confirmedSeats.size() < seatsPerUser) {
            String seat = coach.pollAndBookSeat(pnr);
            if (seat != null) {
              confirmedSeats.add(seat);
            } else {
              break; // No more seats available
            }
          }

          if (confirmedSeats.size() == seatsPerUser) {
            successfulBookings.incrementAndGet();
            System.out.println("Thread " + Thread.currentThread().getId() +
              " SUCCESS booking seats: " + confirmedSeats);
          } else {
            confirmedSeats.forEach(seat -> coach.releaseSeats(Collections.singletonList(seat)));
            System.out.println("Thread " + Thread.currentThread().getId() +
              " FAILED to book " + seatsPerUser + " seats");
          }
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await(10, TimeUnit.SECONDS);
    executorService.shutdown();
    executorService.awaitTermination(5, TimeUnit.SECONDS);

    int totalSeats = 1000;
    int maxPossibleBookings = totalSeats / seatsPerUser;
    int expectedBookedSeats = successfulBookings.get() * seatsPerUser;
    long endTime = System.nanoTime(); // End timing
    double totalTimeSeconds = (endTime - startTime) / 1_000_000_000.0; // Convert to seconds
    double throughput = successfulBookings.get() / totalTimeSeconds;

    System.out.println("Total time (s): " + totalTimeSeconds);
    System.out.println("Successful bookings: " + successfulBookings.get());
    System.out.println("Throughput (bookings/s): " + throughput);
    System.out.println("Available seats remaining: " + coach.getAvailableSeatCount());

    System.out.println("Successful bookings: " + successfulBookings.get());
    System.out.println("Booked seats: " + expectedBookedSeats);
    System.out.println("Available seats remaining: " + coach.getAvailableSeatCount());

    assertTrue(successfulBookings.get() <= maxPossibleBookings,
      "Number of successful bookings should not exceed " + maxPossibleBookings);
    assertEquals(totalSeats - expectedBookedSeats, coach.getAvailableSeatCount(),
      "Available seat count should match remaining seats");
    assertTrue(coach.getAvailableSeatCount() >= 0,
      "Available seat count should not be negative");
    assertTrue(coach.getSeatBookings().values().stream()
        .filter(pnr -> !"UNBOOKED".equals(pnr))
        .count() <= totalSeats,
      "Total booked seats should not exceed total capacity");
  }


  // Helper methods to invoke private methods
  private String invokeHandleSearch(String[] parts) {
    return requestHandler.handleSearch(parts);
  }

  private String invokeHandleBooking(String[] parts) {
    return requestHandler.handleBooking(parts);
  }

  private String invokeHandleCancellation(String[] parts) {
    return requestHandler.handleCancellation(parts);
  }
}
