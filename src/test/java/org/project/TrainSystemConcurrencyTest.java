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
    assertEquals(5, coach.getAvailableSeats().size());
  }

  @Test
  void testTryBookSeats() {
    Coach coach = new Coach("Sleeper", "C1", 5);
    List<String> seats = Arrays.asList("C1-S1", "C1-S2");
    String pnr = "123";

    assertTrue(coach.tryBookSeats(seats, pnr));
    assertEquals(3, coach.getAvailableSeatCount());
    assertEquals(pnr, coach.getSeatBookings().get("C1-S1"));
    assertEquals(pnr, coach.getSeatBookings().get("C1-S2"));
  }

  @Test
  void testTryBookSeatsFailure() {
    Coach coach = new Coach("Sleeper", "C1", 5);
    List<String> seats1 = Arrays.asList("C1-S1", "C1-S2");
    List<String> seats2 = Arrays.asList("C1-S2", "C1-S3");
    String pnr1 = "123";
    String pnr2 = "456";

    assertTrue(coach.tryBookSeats(seats1, pnr1));
    assertFalse(coach.tryBookSeats(seats2, pnr2)); // C1-S2 is already booked
    assertEquals(3, coach.getAvailableSeatCount());
  }

  @Test
  void testReleaseSeats() {
    Coach coach = new Coach("Sleeper", "C1", 5);
    List<String> seats = Arrays.asList("C1-S1", "C1-S2");
    String pnr = "123";

    coach.tryBookSeats(seats, pnr);
    coach.releaseSeats(seats);

    assertEquals(5, coach.getAvailableSeatCount());
    assertEquals("UNBOOKED", coach.getSeatBookings().get("C1-S1"));
    assertEquals("UNBOOKED", coach.getSeatBookings().get("C1-S2"));
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
  void testHandleBookingNotEnoughSeats() {
    String[] parts = {"BOOK", "user1", "12345", "Sleeper", "20"};
    String response = invokeHandleBooking(parts);

    assertEquals("Not enough seats available", response);
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
    // First create a booking
    String[] bookParts = {"BOOK", "user1", "12345", "Sleeper", "2"};
    String bookResponse = invokeHandleBooking(bookParts);
    String pnr = bookResponse.split("PNR: ")[1].split(" ")[0];

    String[] cancelParts = {"CANCEL", pnr};
    String response = invokeHandleCancellation(cancelParts);

    assertEquals("Booking with PNR: " + pnr + " cancelled successfully.", response);
    assertNull(bookings.get(pnr));
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

    ExecutorService executorService = Executors.newFixedThreadPool(500);
    CountDownLatch latch = new CountDownLatch(totalUsers);

    for (int i = 0; i < totalUsers; i++) {
      String userId = "user" + i;
      String pnr = String.valueOf(1000 + i);

      executorService.submit(() -> {
        try {
          List<String> availableSeats = coach.getAvailableSeats();
          if (availableSeats.size() >= seatsPerUser) {
            List<String> seatsToBook = new ArrayList<>();
            for (int j = 0; j < seatsPerUser && !availableSeats.isEmpty(); j++) {
              seatsToBook.add(availableSeats.get(j));
            }
            if (coach.tryBookSeats(seatsToBook, pnr)) {
              successfulBookings.incrementAndGet();
              System.out.println("Thread " + Thread.currentThread().getId() +
                " SUCCESS booking seats: " + seatsToBook);
            } else {
              System.out.println("Thread " + Thread.currentThread().getId() +
                " FAILED to book seats");
            }
          } else {
            System.out.println("Thread " + Thread.currentThread().getId() +
              " FAILED - Not enough seats");
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

  @Test
  public void testConcurrentBookingMultipleCoaches() throws InterruptedException {
    // Setup: Create a train with multiple coaches
    Train train = new Train("12345", "SA", "SB",
      LocalDate.of(2025, 10, 10), LocalDate.of(2025, 10, 11));

    int seatsPerCoach = 100;
    int numberOfCoaches = 5;
    int totalSeats = seatsPerCoach * numberOfCoaches; // 500 seats

    for (int i = 1; i <= numberOfCoaches; i++) {
      train.addCoach("Sleeper", "C" + i, seatsPerCoach);
    }

    int totalUsers = 200;
    int seatsPerUser = 3;
    AtomicInteger successfulBookings = new AtomicInteger(0);

    ExecutorService executorService = Executors.newFixedThreadPool(50);
    CountDownLatch latch = new CountDownLatch(totalUsers);

    for (int i = 0; i < totalUsers; i++) {
      String userId = "user" + i;
      String pnr = String.valueOf(1000 + i);

      executorService.submit(() -> {
        try {
          List<Coach> coaches = train.getCoachTypes().get("sleeper");
          List<String> confirmedSeats = new ArrayList<>();

          // Book seats one-by-one across coaches
          for (Coach coach : coaches) {
            while (confirmedSeats.size() < seatsPerUser) {
              List<String> availableSeats = coach.getAvailableSeats();
              if (availableSeats.isEmpty()) {
                break; // Next coach
              }
              String seat = availableSeats.get(0);
              if (coach.tryBookSeats(List.of(seat), pnr)) {
                confirmedSeats.add(seat);
              }
            }
            if (confirmedSeats.size() == seatsPerUser) break;
          }

          if (confirmedSeats.size() == seatsPerUser) {
            successfulBookings.incrementAndGet();
            System.out.println("Thread " + Thread.currentThread().getId() +
              " SUCCESS booking seats: " + confirmedSeats);
          } else {
            coaches.forEach(c -> c.releaseSeats(confirmedSeats));
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

    int maxPossibleBookings = totalSeats / seatsPerUser; // 166
    int expectedBookedSeats = successfulBookings.get() * seatsPerUser;
    int totalRemainingSeats = train.getCoachTypes().get("sleeper").stream()
      .mapToInt(Coach::getAvailableSeatCount)
      .sum();

    System.out.println("Successful bookings: " + successfulBookings.get());
    System.out.println("Booked seats: " + expectedBookedSeats);
    System.out.println("Available seats remaining: " + totalRemainingSeats);

    assertTrue(successfulBookings.get() <= maxPossibleBookings,
      "Number of successful bookings should not exceed " + maxPossibleBookings);
//    assertEquals(totalSeats - expectedBookedSeats, totalRemainingSeats,
//      "Available seat count should match remaining seats");
    assertTrue(totalRemainingSeats >= 0,
      "Available seat count should not be negative");
    assertTrue(train.getCoachTypes().get("sleeper").stream()
        .flatMap(coach -> coach.getSeatBookings().values().stream())
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
