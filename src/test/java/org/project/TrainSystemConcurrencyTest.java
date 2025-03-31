//package org.project;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.project.database.TrainDatabase;
//import org.project.server.RequestHandler;
//
//import java.io.*;
//import java.net.Socket;
//import java.time.LocalDate;
//import java.util.*;
//import java.util.concurrent.*;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.logging.Logger;
//import java.util.stream.Collectors;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class TrainManagementSystemTest {
//  private ConcurrentHashMap<String, Train> trainMap;
//  private ConcurrentHashMap<String, Map<String, String>> bookings;
//  private RequestHandler requestHandler;
//  private static final Logger logger = Logger.getLogger(TrainManagementSystemTest.class.getName());
//
////  @BeforeEach
////  void setUp() {
////    trainMap = new ConcurrentHashMap<>();
////    bookings = new ConcurrentHashMap<>();
////
////    // Create sample train data
////    Train train = new Train("12345", "SA", "SB",
////      LocalDate.of(2025, 10, 10), LocalDate.of(2025, 10, 11));
////    train.addCoach("Sleeper", "C1", 5);
////    train.addCoach("Sleeper", "C2", 5);
////    train.addCoach("Sleeper", "C3", 5);
////    train.addCoach("AC", "A1", 5);
////    trainMap.put(train.getTrainId(), train);
////
////    // Mock socket for RequestHandler
////    Socket mockSocket = new Socket() {
////      @Override
////      public InputStream getInputStream() {
////        return new ByteArrayInputStream("".getBytes());
////      }
////
////      @Override
////      public OutputStream getOutputStream() {
////        return new ByteArrayOutputStream();
////      }
////    };
////
////    requestHandler = new RequestHandler(mockSocket, trainMap, bookings);
////  }
//
//  @BeforeEach
//  void setUp() {
//    TrainDatabase.INSTANCE.reset();
//
//    Train train = new Train("12345", "SA", "SB",
//      LocalDate.of(2025, 10, 10), LocalDate.of(2025, 10, 11));
//    train.addCoach("Sleeper", "C1", 5);
//    train.addCoach("Sleeper", "C2", 5);
//    train.addCoach("Sleeper", "C3", 5);
//    train.addCoach("AC", "A1", 5);
//    TrainDatabase.INSTANCE.addTrain(train);
//
//    // Mock socket and create RequestHandler
//    Socket mockSocket = new Socket() { /* ... */ };
//    requestHandler = new RequestHandler(mockSocket);
//  }
//
//
//  // ### Train Tests
//  @Test
//  void testTrainCreation() {
//    Train train = trainMap.get("12345");
//    assertEquals("12345", train.getTrainId());
//    assertEquals("SA", train.getSource());
//    assertEquals("SB", train.getDestination());
//    assertEquals(LocalDate.of(2025, 10, 10), train.getDepartureDate());
//    assertEquals(LocalDate.of(2025, 10, 11), train.getArrivalDate());
//  }
//
//  @Test
//  void testAddCoach() {
//    Train train = trainMap.get("12345");
//    assertEquals(2, train.getCoachTypes().size());
//    assertEquals(3, train.getCoachTypes().get("sleeper").size());
//    assertEquals(1, train.getCoachTypes().get("ac").size());
//  }
//
//  // ### Coach Tests
//  @Test
//  void testCoachInitialization() {
//    Coach coach = new Coach("Sleeper", "C1", 5);
//    assertEquals("Sleeper", coach.getType());
//    assertEquals("C1", coach.getCoachId());
//    assertEquals(5, coach.getAvailableSeatCount());
//  }
//
//  @Test
//  void testPollAndBookSeat() {
//    Coach coach = new Coach("Sleeper", "C1", 5);
//    String pnr = "123";
//    String userId = "testUser";
//
//    String seat = coach.pollAndBookSeat(pnr);
//    assertNotNull(seat);
//    assertEquals(4, coach.getAvailableSeatCount());
//    assertEquals(pnr, coach.getSeatBookings().get(seat));
//    logger.info("User " + userId + " booked seat " + seat + " with PNR " + pnr);
//  }
//
//  @Test
//  void testPollAndBookSeatNoSeats() {
//    Coach coach = new Coach("Sleeper", "C1", 0);
//    String pnr = "123";
//
//    String seat = coach.pollAndBookSeat(pnr);
//    assertNull(seat);
//  }
//
//  // ### RequestHandler Tests
//  @Test
//  void testHandleSearchValid() {
//    Map<String, String> request = new HashMap<>();
//    request.put("command", "SEARCH");
//    request.put("source", "SA");
//    request.put("destination", "SB");
//    request.put("date", "2025-10-10");
//
//    String response = invokeHandleSearch(request);
//
//    assertNotNull(response);
//    assertTrue(response.contains("Available Trains"));
//    assertTrue(response.contains("Train ID: 12345"));
//  }
//
//  @Test
//  void testHandleSearchNoTrains() {
//    Map<String, String> request = new HashMap<>();
//    request.put("command", "SEARCH");
//    request.put("source", "SA");
//    request.put("destination", "SC");
//    request.put("date", "2025-10-10");
//
//    String response = invokeHandleSearch(request);
//
//    assertEquals("404 No trains available for the given criteria.", response);
//  }
//
//  @Test
//  void testHandleSearchInvalidDate() {
//    Map<String, String> request = new HashMap<>();
//    request.put("command", "SEARCH");
//    request.put("source", "SA");
//    request.put("destination", "SB");
//    request.put("date", "invalid-date");
//
//    String response = invokeHandleSearch(request);
//
//    assertEquals("400 Invalid date format", response);
//  }
//
//  @Test
//  void testHandleSearchPastDate() {
//    Map<String, String> request = new HashMap<>();
//    request.put("command", "SEARCH");
//    request.put("source", "SA");
//    request.put("destination", "SB");
//    request.put("date", "2023-01-01");
//
//    String response = invokeHandleSearch(request);
//
//    assertEquals("400 Date cannot be in the past", response);
//  }
//
//  @Test
//  void testHandleBookingSuccess() {
//    Map<String, String> request = new HashMap<>();
//    request.put("command", "BOOK");
//    request.put("userId", "user1");
//    request.put("trainId", "12345");
//    request.put("coachType", "Sleeper");
//    request.put("numberOfSeats", "2");
//
//    String response = invokeHandleBooking(request);
//
//    assertTrue(response.contains("Booking successful"));
//    assertTrue(response.contains("PNR"));
//    assertTrue(response.contains("Seats"));
//
//    String pnr = response.split("PNR: ")[1].split(" ")[0];
//    String seats = response.split("Seats: ")[1];
//    logger.info("User user1 booked seats " + seats + " on train 12345 with PNR " + pnr);
//  }
//
//  @Test
//  void testHandleBookingInvalidTrain() {
//    Map<String, String> request = new HashMap<>();
//    request.put("command", "BOOK");
//    request.put("userId", "user1");
//    request.put("trainId", "99999");
//    request.put("coachType", "Sleeper");
//    request.put("numberOfSeats", "2");
//
//    String response = invokeHandleBooking(request);
//
//    assertEquals("404 Train not found", response);
//  }
//
//  @Test
//  void testHandleBookingInvalidCoachType() {
//    Map<String, String> request = new HashMap<>();
//    request.put("command", "BOOK");
//    request.put("userId", "user1");
//    request.put("trainId", "12345");
//    request.put("coachType", "FirstClass");
//    request.put("numberOfSeats", "2");
//
//    String response = invokeHandleBooking(request);
//
//    assertEquals("404 Coach not found", response);
//  }
//
//  @Test
//  void testHandleBookingInvalidNumber() {
//    Map<String, String> request = new HashMap<>();
//    request.put("command", "BOOK");
//    request.put("userId", "user1");
//    request.put("trainId", "12345");
//    request.put("coachType", "Sleeper");
//    request.put("numberOfSeats", "0");
//
//    String response = invokeHandleBooking(request);
//
//    assertEquals("400 Invalid number of seats", response);
//  }
//
//  @Test
//  void testHandleCancellationSuccess() {
//    Map<String, String> bookRequest = new HashMap<>();
//    bookRequest.put("command", "BOOK");
//    bookRequest.put("userId", "user1");
//    bookRequest.put("trainId", "12345");
//    bookRequest.put("coachType", "Sleeper");
//    bookRequest.put("numberOfSeats", "2");
//
//    String bookResponse = invokeHandleBooking(bookRequest);
//    String pnr = bookResponse.split("PNR: ")[1].split(" ")[0];
//    String seats = bookResponse.split("Seats: ")[1];
//    logger.info("User user1 booked seats " + seats + " on train 12345 with PNR " + pnr);
//
//    Map<String, String> cancelRequest = new HashMap<>();
//    cancelRequest.put("command", "CANCEL");
//    cancelRequest.put("userId", "user1");
//    cancelRequest.put("pnr", pnr);
//
//    String response = invokeHandleCancellation(cancelRequest);
//
//
//    assertEquals("Booking with PNR: " + pnr + " cancelled successfully.", response);
//    assertNull(TrainDatabase.INSTANCE.getBookingRecord().get(pnr));
//    List<Coach> sleeperCoaches = trainMap.get("12345").getCoachTypes().get("sleeper");
//    int totalAvailableSeats = sleeperCoaches.stream()
//      .mapToInt(Coach::getAvailableSeatCount)
//      .sum();
//    assertEquals(15, totalAvailableSeats);
//    logger.info("User user1 cancelled booking with PNR " + pnr + ", seats " + seats + " released");
//  }
//
//  @Test
//  void testHandleCancellationNotFound() {
//    Map<String, String> request = new HashMap<>();
//    request.put("command", "CANCEL");
//    request.put("userId", "user1");
//    request.put("pnr", "999");
//
//    String response = invokeHandleCancellation(request);
//
//    assertEquals("400 Booking not found", response);
//  }
//
//  // ### Concurrency Tests
//  @Test
//  void testConcurrentBooking100Seats500Users3() {
//    Coach coach = new Coach("Sleeper", "C1", 1000);
//    int totalUsers = 500;
//    int seatsPerUser = 3;
//    AtomicInteger successfulBookings = new AtomicInteger(0);
//
//    ExecutorService executorService = Executors.newFixedThreadPool(10);
//
//    for (int i = 0; i < totalUsers; i++) {
//      String pnr = "pnr" + i;
//      executorService.submit(() -> {
//        try {
//          List<String> confirmedSeats = new ArrayList<>();
//          while (confirmedSeats.size() < seatsPerUser) {
//            String seat = coach.pollAndBookSeat(pnr);
//            if (seat != null) {
//              confirmedSeats.add(seat);
//            } else {
//              break;
//            }
//          }
//          if (confirmedSeats.size() == seatsPerUser) {
//            successfulBookings.incrementAndGet();
//            System.out.println("Thread " + Thread.currentThread().getId() +
//              " SUCCESS booking seats: " + confirmedSeats);
//          } else {
//            coach.releaseSeats(confirmedSeats);
//            System.out.println("Thread " + Thread.currentThread().getId() +
//              " FAILED to book " + seatsPerUser + " seats");
//          }
//        } catch (Exception e) {
//          // Handle any unexpected exceptions
//          e.printStackTrace();
//        }
//      });
//    }
//
//    // Shut down the executor and wait for all tasks to complete
//    executorService.shutdown();
//    try {
//      boolean terminated = executorService.awaitTermination(10, TimeUnit.SECONDS);
//      if (!terminated) {
//        fail("Test timed out waiting for threads to complete");
//      }
//    } catch (InterruptedException e) {
//      Thread.currentThread().interrupt();
//      fail("Test interrupted while waiting for threads to complete");
//    }
//
//    // Assertions
//    int totalSeats = 1000;
//    int maxPossibleBookings = totalSeats / seatsPerUser;
//    int expectedBookedSeats = successfulBookings.get() * seatsPerUser;
//
//    assertTrue(successfulBookings.get() <= maxPossibleBookings);
//    assertEquals(totalSeats - expectedBookedSeats, coach.getAvailableSeatCount());
//    assertTrue(coach.getAvailableSeatCount() >= 0);
//    assertTrue(coach.getSeatBookings().values().stream()
//      .filter(pnr -> !"UNBOOKED".equals(pnr))
//      .count() <= totalSeats);
//  }
//
//  @Test
//  void testConcurrentBooking100Seats500Users2() {
//    // Create two sleeper coaches with 500 seats each (total 1000 seats)
//    Coach coach1 = new Coach("Sleeper", "C1", 500);
//    Coach coach2 = new Coach("Sleeper", "C2", 500);
//    List<Coach> coaches = Arrays.asList(coach1, coach2); // List of coaches to book from
//
//    int totalUsers = 1500;
//    int seatsPerUser = 3;
//    AtomicInteger successfulBookings = new AtomicInteger(0);
//
//    ExecutorService executorService = Executors.newFixedThreadPool(20);
//    CountDownLatch latch = new CountDownLatch(totalUsers);
//
//    for (int i = 0; i < totalUsers; i++) {
//      String pnr = "pnr" + i;
//      executorService.submit(() -> {
//        try {
//          List<String> confirmedSeats = new ArrayList<>();
//          // Try booking seats from available coaches
//          for (Coach coach : coaches) {
//            while (confirmedSeats.size() < seatsPerUser) {
//              String seat = coach.pollAndBookSeat(pnr);
//              if (seat != null) {
//                confirmedSeats.add(seat);
//              } else {
//                break; // No more seats in this coach, try next one
//              }
//            }
//            if (confirmedSeats.size() == seatsPerUser) {
//              break; // Got all required seats, no need to check other coaches
//            }
//          }
//
//          if (confirmedSeats.size() == seatsPerUser) {
//            successfulBookings.incrementAndGet();
//            System.out.println("Thread " + Thread.currentThread().getId() +
//              " SUCCESS booking seats: " + confirmedSeats);
//          } else {
//            // Release seats from all coaches if booking failed
//            for (Coach coach : coaches) {
//              List<String> seatsToRelease = confirmedSeats.stream()
//                .filter(seat -> seat.startsWith(coach.getCoachId()))
//                .collect(Collectors.toList());
//              coach.releaseSeats(seatsToRelease);
//            }
//            System.out.println("Thread " + Thread.currentThread().getId() +
//              " FAILED to book " + seatsPerUser + " seats");
//          }
//        } finally {
//          latch.countDown();
//        }
//      });
//    }
//
//    // Shutdown the executor and wait for all tasks to complete
//    executorService.shutdown();
//    try {
//      if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
//        executorService.shutdownNow();
//      }
//    } catch (InterruptedException e) {
//      executorService.shutdownNow();
//      Thread.currentThread().interrupt();
//    }
//
//    // Calculate total available seats across both coaches
//    int totalSeats = 1000; // 500 from C1 + 500 from C2
//    int maxPossibleBookings = totalSeats / seatsPerUser;
//    int expectedBookedSeats = successfulBookings.get() * seatsPerUser;
//    int totalAvailableSeats = coaches.stream()
//      .mapToInt(Coach::getAvailableSeatCount)
//      .sum();
//    long totalBookedSeats = coaches.stream()
//      .flatMap(coach -> coach.getSeatBookings().values().stream())
//      .filter(pnr -> !"UNBOOKED".equals(pnr))
//      .count();
//
//    // Assertions
//    assertTrue(successfulBookings.get() <= maxPossibleBookings,
//      "Successful bookings should not exceed maximum possible bookings");
//    assertEquals(totalSeats - expectedBookedSeats, totalAvailableSeats,
//      "Remaining available seats should match expected");
//    assertTrue(totalAvailableSeats >= 0,
//      "Total available seats should not be negative");
//    assertTrue(totalBookedSeats <= totalSeats,
//      "Total booked seats should not exceed total seats");
//  }
//
//  // ### Helper Methods
//  private String invokeHandleSearch(Map<String, String> request) {
//    return requestHandler.handleSearch(request);
//  }
//
//  private String invokeHandleBooking(Map<String, String> request) {
//    return requestHandler.handleBooking(request);
//  }
//
//  private String invokeHandleCancellation(Map<String, String> request) {
//    return requestHandler.handleCancellation(request);
//  }
//}
package org.project;

import org.junit.jupiter.api.*;
import org.project.database.TrainDatabase;
import org.project.schema.Train;
import org.project.server.RequestHandler;
import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class TrainDatabaseTest {
  private TrainDatabase db;
  private RequestHandler requestHandler;
  private Socket mockSocket;

  @BeforeEach
  void setUp() throws IOException {
    db = TrainDatabase.INSTANCE;
    db.reset(); // Clear state before each test

    mockSocket = new Socket() {
      @Override
      public InputStream getInputStream() {
        return new ByteArrayInputStream("".getBytes());
      }

      @Override
      public OutputStream getOutputStream() {
        return new ByteArrayOutputStream();
      }
    };
    requestHandler = new RequestHandler(mockSocket);
  }

  @AfterEach
  void tearDown() throws IOException {
    mockSocket.close();
  }

  // ---- Database Unit Tests ----
  @Test
  void testDatabaseSingleton() {
    TrainDatabase anotherInstance = TrainDatabase.INSTANCE;
    assertSame(db, anotherInstance, "Should return same singleton instance");
  }

  @Test
  void testAddAndRetrieveTrain() {
    Train train = new Train("T1", "A", "B", LocalDate.now(), LocalDate.now().plusDays(1));
    db.addTrain(train);

    assertEquals(train, db.getTrainMap().get("T1"), "Train should be retrievable");
    assertEquals(1, db.getTrainMap().size());
  }

  @Test
  void testResetDatabase() {
    db.addTrain(new Train("T1", "A", "B", LocalDate.now(), LocalDate.now()));
    db.reset();

    assertTrue(db.getTrainMap().isEmpty(), "Train map should be empty after reset");
    assertTrue(db.getBookingRecord().isEmpty(), "Booking records should be empty after reset");
  }

  // ---- Integration Tests ----
  @Test
  void testHandleSearchWithDatabase() {
    // Setup test data
    Train train = new Train("T1", "Paris", "London",
      LocalDate.of(2025, 12, 25), LocalDate.of(2025, 12, 26));
    train.addCoach("AC", "C1", 10);
    db.addTrain(train);

    // Test search
    Map<String, String> request = new HashMap<>();
    request.put("command", "SEARCH");
    request.put("source", "Paris");
    request.put("destination", "London");
    request.put("date", "2025-12-25");

    String response = requestHandler.handleSearch(request);
    assertTrue(response.contains("Available Trains"));
    assertTrue(response.contains("Train ID: T1"));
    assertTrue(response.contains("Type: ac"));
  }

  @Test
  void testBookingLifecycle() {
    // Setup test train
    Train train = new Train("T1", "A", "B", LocalDate.now(), LocalDate.now());
    train.addCoach("Sleeper", "S1", 5);
    db.addTrain(train);

    // Book seats
    Map<String, String> bookRequest = new HashMap<>();
    bookRequest.put("command", "BOOK");
    bookRequest.put("userId", "user1");
    bookRequest.put("trainId", "T1");
    bookRequest.put("coachType", "Sleeper");
    bookRequest.put("numberOfSeats", "2");

    String bookResponse = requestHandler.handleBooking(bookRequest);
    assertTrue(bookResponse.startsWith("Booking successful"));

    // Verify database state
    String pnr = bookResponse.split("PNR: ")[1].split(" ")[0];
    assertNotNull(db.getBookingRecord().get(pnr), "Booking should exist in database");
    assertEquals(3, train.getCoachTypes().get("sleeper").get(0).getAvailableSeatCount());

    // Cancel booking
    Map<String, String> cancelRequest = new HashMap<>();
    cancelRequest.put("command", "CANCEL");
    cancelRequest.put("userId", "user1");
    cancelRequest.put("pnr", pnr);

    String cancelResponse = requestHandler.handleCancellation(cancelRequest);
    assertEquals("Booking with PNR: " + pnr + " cancelled successfully.", cancelResponse);
    assertNull(db.getBookingRecord().get(pnr), "Booking should be removed");
    assertEquals(5, train.getCoachTypes().get("sleeper").get(0).getAvailableSeatCount());
  }

  // ---- Concurrency Tests ----
  @Test
  void testConcurrentBookings() throws InterruptedException {
    // Setup test train with 100 seats
    Train train = new Train("T1", "A", "B", LocalDate.now(), LocalDate.now());
    train.addCoach("AC", "C1", 100);
    db.addTrain(train);

    int threadCount = 50;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);
    AtomicInteger successCount = new AtomicInteger(0);

    // Simulate concurrent bookings
    for (int i = 0; i < threadCount; i++) {
      executor.submit(() -> {
        try {
          Map<String, String> request = new HashMap<>();
          request.put("command", "BOOK");
          request.put("userId", "user" + Thread.currentThread().getId());
          request.put("trainId", "T1");
          request.put("coachType", "AC");
          request.put("numberOfSeats", "2");

          String response = requestHandler.handleBooking(request);
          if (response.startsWith("Booking successful")) {
            successCount.incrementAndGet();
          }
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await(5, TimeUnit.SECONDS);
    executor.shutdown();

    // Verify
    int totalBookedSeats = successCount.get() * 2;
    assertTrue(totalBookedSeats <= 100, "Should not overbook seats");
    assertEquals(100 - totalBookedSeats,
      train.getCoachTypes().get("ac").get(0).getAvailableSeatCount());
  }

  @Test
  void testConcurrentSearchWhileBooking() throws InterruptedException {
    // Setup test train
    Train train = new Train("T1", "A", "B", LocalDate.now(), LocalDate.now());
    train.addCoach("AC", "C1", 10);
    db.addTrain(train);

    int threadCount = 20;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    AtomicInteger searchSuccessCount = new AtomicInteger(0);
    AtomicInteger bookSuccessCount = new AtomicInteger(0);

    // Mixed workload
    for (int i = 0; i < threadCount; i++) {
      if (i % 4 == 0) { // 25% booking requests
        executor.submit(() -> {
          Map<String, String> request = new HashMap<>();
          request.put("command", "BOOK");
          request.put("userId", "user" + Thread.currentThread().getId());
          request.put("trainId", "T1");
          request.put("coachType", "AC");
          request.put("numberOfSeats", "1");

          String response = requestHandler.handleBooking(request);
          if (response.startsWith("Booking successful")) {
            bookSuccessCount.incrementAndGet();
          }
        });
      } else { // 75% search requests
        executor.submit(() -> {
          Map<String, String> request = new HashMap<>();
          request.put("command", "SEARCH");
          request.put("source", "A");
          request.put("destination", "B");
          request.put("date", LocalDate.now().toString());

          String response = requestHandler.handleSearch(request);
          if (response.contains("Available Trains")) {
            searchSuccessCount.incrementAndGet();
          }
        });
      }
    }

    executor.shutdown();
    executor.awaitTermination(3, TimeUnit.SECONDS);

    // Verify
    assertEquals(threadCount * 3 / 4, searchSuccessCount.get(), "All searches should succeed");
    assertTrue(bookSuccessCount.get() <= 10, "Should not book more than available seats");
  }

  // ---- Edge Cases ----
  @Test
  void testBookingInvalidTrain() {
    Map<String, String> request = new HashMap<>();
    request.put("command", "BOOK");
    request.put("userId", "user1");
    request.put("trainId", "INVALID");
    request.put("coachType", "AC");
    request.put("numberOfSeats", "1");

    String response = requestHandler.handleBooking(request);
    assertEquals("404 Train not found", response);
  }

  @Test
  void testCancellationInvalidPNR() {
    Map<String, String> request = new HashMap<>();
    request.put("command", "CANCEL");
    request.put("userId", "user1");
    request.put("pnr", "INVALID");

    String response = requestHandler.handleCancellation(request);
    assertEquals("400 Booking not found", response);
  }
}
