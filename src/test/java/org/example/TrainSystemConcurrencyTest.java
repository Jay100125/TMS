package org.example;//package org.example;
/////
//
////import org.junit.jupiter.api.BeforeEach;
////import org.junit.jupiter.api.Test;
////import static org.junit.jupiter.api.Assertions.*;
////
////import org.slf4j.Logger;
////import org.slf4j.LoggerFactory;
//
////
////import java.time.LocalDate;
////import java.util.*;
////import java.util.concurrent.*;
////import java.util.concurrent.atomic.AtomicInteger;
////
////public class TrainSystemConcurrencyTest {
////  private static final Logger logger = LoggerFactory.getLogger(TrainSystemConcurrencyTest.class);
////  private List<org.example.Train> trains;
////  private Map<String, org.example.Train> trainMap;
////  private Map<String, org.example.Booking> bookings;
////  private RequestHandler requestHandler;
////
////  @BeforeEach
////  public void setUp() {
////    // Initialize test data
////    trains = new ArrayList<>();
////    trainMap = new HashMap<>();
////    bookings = new ConcurrentHashMap<>();
////
////    // Create test train with 5 seats
////    org.example.Train testTrain = new org.example.Train("TEST1", "London", "Paris",
////      LocalDate.now(), LocalDate.now().plusDays(1));
////    testTrain.getCoaches().add(new org.example.Coach("AC", "C1", 5));
////    trains.add(testTrain);
////    trainMap.put("TEST1", testTrain);
////
////    // Initialize request handler with test data
////    requestHandler = new RequestHandler(null, trains, trainMap, bookings);
////  }
////
////  @Test
////  public void testConcurrentBooking() throws InterruptedException {
////    final int THREAD_COUNT = 100;
////    final AtomicInteger successCount = new AtomicInteger(0);
////    final AtomicInteger failureCount = new AtomicInteger(0);
////
////    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
////
////    // Create 100 booking requests
////    for (int i = 0; i < THREAD_COUNT; i++) {
////      executor.execute(() -> {
////        String response = requestHandler.handleBooking(
////          new String[]{"BOOK", "user1", "TEST1", "C1", "1"}
////        );
////        if (response.contains("org.example.Booking successful")) {
////          successCount.incrementAndGet();
////        } else {
////          failureCount.incrementAndGet();
////        }
////      });
////    }
////
////    executor.shutdown();
////    executor.awaitTermination(10, TimeUnit.SECONDS);
////
////    logger.info("Successful bookings: {}, Failed bookings: {}",
////      successCount.get(), failureCount.get());
////
////    // Verify only 5 seats were booked
////    org.example.Coach coach = trainMap.get("TEST1").getCoaches().get(0);
////    long bookedSeats = coach.getSeatBookings().values()
////      .stream()
////      .filter(Objects::nonNull)
////      .count();
////
////    assertEquals(5, successCount.get());
////    assertEquals(95, failureCount.get());
////    assertEquals(5, bookedSeats);
////  }
////
////  @Test
////  public void testConcurrentSearchAndBooking() throws InterruptedException {
////    final int THREAD_COUNT = 100;
////    final AtomicInteger searchSuccess = new AtomicInteger(0);
////    final AtomicInteger bookingSuccess = new AtomicInteger(0);
////
////    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
////
////    // 50% search requests, 50% booking requests
////    for (int i = 0; i < THREAD_COUNT; i++) {
////      if (i % 2 == 0) {
////        // Search request
////        executor.execute(() -> {
////          String response = requestHandler.handleSearch(
////            new String[]{"SEARCH", "London", "Paris", LocalDate.now().toString()}
////          );
////          if (!response.contains("No trains available")) {
////            searchSuccess.incrementAndGet();
////          }
////        });
////      } else {
////        // org.example.Booking request
////        executor.execute(() -> {
////          String response = requestHandler.handleBooking(
////            new String[]{"BOOK", "user1", "TEST1", "C1", "1"}
////          );
////          if (response.contains("org.example.Booking successful")) {
////            bookingSuccess.incrementAndGet();
////          }
////        });
////      }
////    }
////
////    executor.shutdown();
////    executor.awaitTermination(10, TimeUnit.SECONDS);
////
////    logger.info("Successful searches: {}, Successful bookings: {}",
////      searchSuccess.get(), bookingSuccess.get());
////
////    // Verify data consistency
////    org.example.Coach coach = trainMap.get("TEST1").getCoaches().get(0);
////    long availableSeats = coach.getSeatBookings().values()
////      .stream()
////      .filter(Objects::isNull)
////      .count();
////
//////    assertTrue(searchSuccess.get() > 0);
////    assertEquals(5 - bookingSuccess.get(), availableSeats);
////  }
////}
////
////import org.junit.jupiter.api.BeforeEach;
////import org.junit.jupiter.api.Test;
////import static org.junit.jupiter.api.Assertions.*;
////
////import org.slf4j.Logger;
////import org.slf4j.LoggerFactory;
////
////import java.time.LocalDate;
////import java.util.*;
////import java.util.concurrent.*;
////import java.util.concurrent.atomic.AtomicInteger;
////
////public class TrainSystemConcurrencyTest {
////  private static final Logger logger = LoggerFactory.getLogger(TrainSystemConcurrencyTest.class);
////  private List<org.example.Train> trains;
////  private Map<String, org.example.Train> trainMap;
////  private Map<String, org.example.Booking> bookings;
////  private RequestHandler requestHandler;
////
////  @BeforeEach
////  public void setUp() {
////    // Initialize test data
////    resetTestData();
////    logger.info("Test setup completed - fresh test environment initialized");
////  }
////
////  private void resetTestData() {
////    trains = new ArrayList<>();
////    trainMap = new HashMap<>();
////    bookings = new ConcurrentHashMap<>();
////
////    // Create test train with 5 seats
////    org.example.Train testTrain = new org.example.Train("TEST1", "London", "Paris",
////      LocalDate.now(), LocalDate.now().plusDays(1));
////    testTrain.getCoaches().add(new org.example.Coach("AC", "C1", 5));
////    trains.add(testTrain);
////    trainMap.put("TEST1", testTrain);
////
////    // Initialize request handler with test data
////    requestHandler = new RequestHandler(null, trains, trainMap, bookings);
////  }
////
////  @Test
////  public void testConcurrentBooking() throws InterruptedException {
////    final int THREAD_COUNT = 100;
////    logger.info("Starting testConcurrentBooking with {} threads", THREAD_COUNT);
////
////    AtomicInteger successCount = new AtomicInteger(0);
////    AtomicInteger failureCount = new AtomicInteger(0);
////
////    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
////
////    for (int i = 0; i < THREAD_COUNT; i++) {
////      executor.execute(() -> {
////        String response = requestHandler.handleBooking(
////          new String[]{"BOOK", "user1", "TEST1", "C1", "1"}
////        );
////        if (response.contains("org.example.Booking successful")) {
////          logger.debug("org.example.Booking succeeded for thread {}", Thread.currentThread().getId());
////          successCount.incrementAndGet();
////        } else {
////          logger.debug("org.example.Booking failed for thread {}", Thread.currentThread().getId());
////          failureCount.incrementAndGet();
////        }
////      });
////    }
////
////    executor.shutdown();
////    assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS),
////      "Test execution timed out");
////
////    logger.info("Test results - Success: {}, Failures: {}", successCount.get(), failureCount.get());
////
////    org.example.Coach coach = trainMap.get("TEST1").getCoaches().get(0);
////    long bookedSeats = coach.getSeatBookings().values().stream()
////      .filter(Objects::nonNull)
////      .count();
////
////    assertEquals(5, successCount.get(), "Incorrect number of successful bookings");
////    assertEquals(95, failureCount.get(), "Incorrect number of failed bookings");
////    assertEquals(5, bookedSeats, "Mismatch in actual booked seats");
////  }
////
////  @Test
////  public void testConcurrentSearchAndBooking() throws InterruptedException {
////    final int THREAD_COUNT = 100;
////    logger.info("Starting testConcurrentSearchAndBooking with {} threads", THREAD_COUNT);
////
////    AtomicInteger searchSuccess = new AtomicInteger(0);
////    AtomicInteger bookingSuccess = new AtomicInteger(0);
////
////    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
////
////    for (int i = 0; i < THREAD_COUNT; i++) {
////      if (i % 2 == 0) {
////        executor.execute(() -> {
////          String response = requestHandler.handleSearch(
////            new String[]{"SEARCH", "London", "Paris", LocalDate.now().toString()}
////          );
////          if (!response.contains("No trains available")) {
////            logger.debug("Search succeeded");
////            searchSuccess.incrementAndGet();
////          }
////        });
////      } else {
////        executor.execute(() -> {
////          String response = requestHandler.handleBooking(
////            new String[]{"BOOK", "user1", "TEST1", "C1", "1"}
////          );
////          if (response.contains("org.example.Booking successful")) {
////            logger.debug("org.example.Booking succeeded in mixed test");
////            bookingSuccess.incrementAndGet();
////          }
////        });
////      }
////    }
////
////    executor.shutdown();
////    assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS),
////      "Test execution timed out");
////
////    logger.info("Mixed test results - Successful searches: {}, Successful bookings: {}",
////      searchSuccess.get(), bookingSuccess.get());
////
////    org.example.Coach coach = trainMap.get("TEST1").getCoaches().get(0);
////    long availableSeats = coach.getSeatBookings().values().stream()
////      .filter(Objects::isNull)
////      .count();
////
////    assertEquals(5 - bookingSuccess.get(), availableSeats,
////      "Available seats count mismatch");
////  }
////
////  @Test
////  public void testConcurrentCancellations() throws InterruptedException {
////    logger.info("Starting testConcurrentCancellations");
////
////    // First create 5 valid bookings
////    List<String> pnrs = new ArrayList<>();
////    for (int i = 0; i < 5; i++) {
////      String response = requestHandler.handleBooking(
////        new String[]{"BOOK", "user1", "TEST1", "C1", "1"}
////      );
////      if (response.contains("org.example.Booking successful")) {
////        pnrs.add(response.split("PNR: ")[1].split(" ")[0]);
////      }
////    }
////
////    final int THREAD_COUNT = pnrs.size();
////    AtomicInteger cancellationSuccess = new AtomicInteger(0);
////
////    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
////
////    for (String pnr : pnrs) {
////      executor.execute(() -> {
////        String response = requestHandler.handleCancellation(
////          new String[]{"CANCEL", pnr}
////        );
////        if (response.contains("cancelled successfully")) {
////          logger.debug("Cancellation succeeded for PNR: {}", pnr);
////          cancellationSuccess.incrementAndGet();
////        }
////      });
////    }
////
////    executor.shutdown();
////    assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS),
////      "Cancellation test timed out");
////
////    logger.info("Cancellation results - Success: {}/{}",
////      cancellationSuccess.get(), THREAD_COUNT);
////
////    assertEquals(THREAD_COUNT, cancellationSuccess.get(),
////      "Not all cancellations succeeded");
////
////    org.example.Coach coach = trainMap.get("TEST1").getCoaches().get(0);
////    long availableSeats = coach.getSeatBookings().values().stream()
////      .filter(Objects::isNull)
////      .count();
////    assertEquals(5, availableSeats, "All seats should be available after cancellations");
////  }
////
////  @Test
////  public void testZeroSeatBooking() {
////    logger.info("Starting testZeroSeatBooking");
////    String response = requestHandler.handleBooking(
////      new String[]{"BOOK", "user1", "TEST1", "C1", "0"}
////    );
////    logger.info("Zero seat booking response: {}", response);
////    assertTrue(response.contains("Invalid seat count"),
////      "Should reject zero seat booking");
////  }
////
////  @Test
////  public void testInvalidTrainId() {
////    logger.info("Starting testInvalidTrainId");
////    String response = requestHandler.handleBooking(
////      new String[]{"BOOK", "user1", "INVALID_TRAIN", "C1", "1"}
////    );
////    logger.info("Invalid train ID response: {}", response);
////    assertTrue(response.contains("org.example.Train not found"),
////      "Should detect invalid train ID");
////  }
////}
//
////import org.junit.jupiter.api.BeforeEach;
////import org.junit.jupiter.api.Test;
////import static org.junit.jupiter.api.Assertions.*;
////
////import java.time.LocalDate;
////import java.util.*;
////import java.util.concurrent.*;
////import java.util.concurrent.atomic.AtomicInteger;
////
////public class TrainSystemConcurrencyTest {
////  private List<org.example.Train> trains;
////  private Map<String, org.example.Train> trainMap;
////  private Map<String, org.example.Booking> bookings;
////  private RequestHandler requestHandler;
////
////  @BeforeEach
////  public void setUp() {
////    // Initialize test data
////    resetTestData();
////    System.out.println("Test setup completed - fresh test environment initialized");
////  }
////
////  private void resetTestData() {
////    trains = new ArrayList<>();
////    trainMap = new HashMap<>();
////    bookings = new ConcurrentHashMap<>();
////
////    // Create test train with 5 seats
////    org.example.Train testTrain = new org.example.Train("TEST1", "London", "Paris",
////      LocalDate.now(), LocalDate.now().plusDays(1));
////    testTrain.getCoaches().add(new org.example.Coach("AC", "C1", 5));
////    trains.add(testTrain);
////    trainMap.put("TEST1", testTrain);
////
////    // Initialize request handler with test data
////    requestHandler = new RequestHandler(null, trains, trainMap, bookings);
////  }
////
////  @Test
////  public void testConcurrentBooking() throws InterruptedException {
////    final int THREAD_COUNT = 100;
////    System.out.println("Starting testConcurrentBooking with " + THREAD_COUNT + " threads");
////
////    AtomicInteger successCount = new AtomicInteger(0);
////    AtomicInteger failureCount = new AtomicInteger(0);
////
////    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
////
////    for (int i = 0; i < THREAD_COUNT; i++) {
////      executor.execute(() -> {
////        String response = requestHandler.handleBooking(
////          new String[]{"BOOK", "user1", "TEST1", "C1", "1"}
////        );
////        if (response.contains("org.example.Booking successful")) {
////          System.out.println("org.example.Booking succeeded for thread " + Thread.currentThread().getId());
////          successCount.incrementAndGet();
////        } else {
////          System.out.println("org.example.Booking failed for thread " + Thread.currentThread().getId());
////          failureCount.incrementAndGet();
////        }
////      });
////    }
////
////    executor.shutdown();
////    assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS),
////      "Test execution timed out");
////
////    System.out.println("Test results - Success: " + successCount.get() + ", Failures: " + failureCount.get());
////
////    org.example.Coach coach = trainMap.get("TEST1").getCoaches().get(0);
////    long bookedSeats = coach.getSeatBookings().values().stream()
////      .filter(Objects::nonNull)
////      .count();
////
////    assertEquals(5, successCount.get(), "Incorrect number of successful bookings");
////    assertEquals(95, failureCount.get(), "Incorrect number of failed bookings");
////    assertEquals(5, bookedSeats, "Mismatch in actual booked seats");
////  }
////
////  @Test
////  public void testConcurrentSearchAndBooking() throws InterruptedException {
////    final int THREAD_COUNT = 100;
////    System.out.println("Starting testConcurrentSearchAndBooking with " + THREAD_COUNT + " threads");
////
////    AtomicInteger searchSuccess = new AtomicInteger(0);
////    AtomicInteger bookingSuccess = new AtomicInteger(0);
////
////    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
////
////    for (int i = 0; i < THREAD_COUNT; i++) {
////      if (i % 2 == 0) {
////        executor.execute(() -> {
////          String response = requestHandler.handleSearch(
////            new String[]{"SEARCH", "London", "Paris", LocalDate.now().toString()}
////          );
////          if (!response.contains("No trains available")) {
////            System.out.println("Search succeeded");
////            searchSuccess.incrementAndGet();
////          }
////        });
////      } else {
////        executor.execute(() -> {
////          String response = requestHandler.handleBooking(
////            new String[]{"BOOK", "user1", "TEST1", "C1", "1"}
////          );
////          if (response.contains("org.example.Booking successful")) {
////            System.out.println("org.example.Booking succeeded in mixed test");
////            bookingSuccess.incrementAndGet();
////          }
////        });
////      }
////    }
////
////    executor.shutdown();
////    assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS),
////      "Test execution timed out");
////
////    System.out.println("Mixed test results - Successful searches: " + searchSuccess.get() + ", Successful bookings: " + bookingSuccess.get());
////
////    org.example.Coach coach = trainMap.get("TEST1").getCoaches().get(0);
////    long availableSeats = coach.getSeatBookings().values().stream()
////      .filter(Objects::isNull)
////      .count();
////
////    assertEquals(5 - bookingSuccess.get(), availableSeats,
////      "Available seats count mismatch");
////  }
////
////  @Test
////  public void testConcurrentCancellations() throws InterruptedException {
////    System.out.println("Starting testConcurrentCancellations");
////
////    // First create 5 valid bookings
////    List<String> pnrs = new ArrayList<>();
////    for (int i = 0; i < 5; i++) {
////      String response = requestHandler.handleBooking(
////        new String[]{"BOOK", "user1", "TEST1", "C1", "1"}
////      );
////      if (response.contains("org.example.Booking successful")) {
////        pnrs.add(response.split("PNR: ")[1].split(" ")[0]);
////      }
////    }
////
////    final int THREAD_COUNT = pnrs.size();
////    AtomicInteger cancellationSuccess = new AtomicInteger(0);
////
////    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
////
////    for (String pnr : pnrs) {
////      executor.execute(() -> {
////        String response = requestHandler.handleCancellation(
////          new String[]{"CANCEL", pnr}
////        );
////        if (response.contains("cancelled successfully")) {
////          System.out.println("Cancellation succeeded for PNR: " + pnr);
////          cancellationSuccess.incrementAndGet();
////        }
////      });
////    }
////
////    executor.shutdown();
////    assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS),
////      "Cancellation test timed out");
////
////    System.out.println("Cancellation results - Success: " + cancellationSuccess.get() + "/" + THREAD_COUNT);
////
////    assertEquals(THREAD_COUNT, cancellationSuccess.get(),
////      "Not all cancellations succeeded");
////
////    org.example.Coach coach = trainMap.get("TEST1").getCoaches().get(0);
////    long availableSeats = coach.getSeatBookings().values().stream()
////      .filter(Objects::isNull)
////      .count();
////    assertEquals(5, availableSeats, "All seats should be available after cancellations");
////  }
////
////  @Test
////  public void testZeroSeatBooking() {
////    System.out.println("Starting testZeroSeatBooking");
////    String response = requestHandler.handleBooking(
////      new String[]{"BOOK", "user1", "TEST1", "C1", "0"}
////    );
////    System.out.println("Zero seat booking response: " + response);
////    assertTrue(response.contains("Invalid seat count"),
////      "Should reject zero seat booking");
////  }
////
////  @Test
////  public void testInvalidTrainId() {
////    System.out.println("Starting testInvalidTrainId");
////    String response = requestHandler.handleBooking(
////      new String[]{"BOOK", "user1", "INVALID_TRAIN", "C1", "1"}
////    );
////    System.out.println("Invalid train ID response: " + response);
////    assertTrue(response.contains("org.example.Train not found"),
////      "Should detect invalid train ID");
////  }
////}
////
////import org.junit.jupiter.api.BeforeEach;
////import org.junit.jupiter.api.Test;
////import static org.junit.jupiter.api.Assertions.*;
////
////import org.slf4j.Logger;
////import org.slf4j.LoggerFactory;
////
////import java.time.LocalDate;
////import java.util.*;
////import java.util.concurrent.*;
////import java.util.concurrent.atomic.AtomicInteger;
////import main.java.server.RequestHandler;
////import main.java.*;
////
////
////public class TrainSystemConcurrencyTest {
////  private static final Logger logger = LoggerFactory.getLogger(TrainSystemConcurrencyTest.class);
////  private List<org.example.Train> trains;
////  private Map<String, org.example.Train> trainMap;
////  private Map<String, org.example.Booking> bookings;
////  private RequestHandler requestHandler;
////
////  @BeforeEach
////  public void setUp() {
////    // Initialize test data
////    resetTestData();
////    logger.info("Test setup completed - fresh test environment initialized");
////  }
////
////  private void resetTestData() {
////    trains = new ArrayList<>();
////    trainMap = new HashMap<>();
////    bookings = new ConcurrentHashMap<>();
////
////    // Create test train with 5 seats
////    org.example.Train testTrain = new org.example.Train("TEST1", "London", "Paris",
////      LocalDate.now(), LocalDate.now().plusDays(1));
////    testTrain.getCoaches().add(new org.example.Coach("AC", "C1", 200));
////    trains.add(testTrain);
////    trainMap.put("TEST1", testTrain);
////
////    // Initialize request handler with test data
////    requestHandler = new RequestHandler(null, trains, trainMap, bookings);
////  }
////
////  @Test
////  public void testConcurrentBooking() throws InterruptedException {
////    final int THREAD_COUNT = 100;
////    logger.info("Starting testConcurrentBooking with {} threads", THREAD_COUNT);
////
////    AtomicInteger successCount = new AtomicInteger(0);
////    AtomicInteger failureCount = new AtomicInteger(0);
////
////    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
////
////    for (int i = 0; i < THREAD_COUNT; i++) {
////      executor.execute(() -> {
////        String response = requestHandler.handleBooking(
////          new String[]{"BOOK", "user1", "TEST1", "C1", "1"}
////        );
////        if (response.contains("org.example.Booking successful")) {
////          logger.debug("org.example.Booking succeeded for thread {}", Thread.currentThread().getId());
////          successCount.incrementAndGet();
////        } else {
////          logger.debug("org.example.Booking failed for thread {}", Thread.currentThread().getId());
////          failureCount.incrementAndGet();
////        }
////      });
////    }
////
////    executor.shutdown();
////    assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS),
////      "Test execution timed out");
////
////    logger.info("Test results - Success: {}, Failures: {}", successCount.get(), failureCount.get());
////
////    org.example.Coach coach = trainMap.get("TEST1").getCoaches().get(0);
////    long bookedSeats = coach.getSeatBookings().values().stream()
////      .filter(Objects::nonNull)
////      .count();
////
////    assertEquals(100, successCount.get(), "Incorrect number of successful bookings");
////    assertEquals(0, failureCount.get(), "Incorrect number of failed bookings");
////    assertEquals(100, bookedSeats, "Mismatch in actual booked seats");
////  }
////
////  @Test
////  public void testConcurrentSearchAndBooking() throws InterruptedException {
////    final int THREAD_COUNT = 100;
////    logger.info("Starting testConcurrentSearchAndBooking with {} threads", THREAD_COUNT);
////
////    AtomicInteger searchSuccess = new AtomicInteger(0);
////    AtomicInteger bookingSuccess = new AtomicInteger(0);
////
////    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
////
////    for (int i = 0; i < THREAD_COUNT; i++) {
////      if (i % 2 == 0) {
////        executor.execute(() -> {
////          String response = requestHandler.handleSearch(
////            new String[]{"SEARCH", "London", "Paris", LocalDate.now().toString()}
////          );
////          if (!response.contains("No trains available")) {
////            logger.debug("Search succeeded");
////            searchSuccess.incrementAndGet();
////          }
////        });
////      } else {
////        executor.execute(() -> {
////          String response = requestHandler.handleBooking(
////            new String[]{"BOOK", "user1", "TEST1", "C1", "1"}
////          );
////          if (response.contains("org.example.Booking successful")) {
////            logger.debug("org.example.Booking succeeded in mixed test");
////            bookingSuccess.incrementAndGet();
////          }
////        });
////      }
////    }
////
////    executor.shutdown();
////    assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS),
////      "Test execution timed out");
////
////    logger.info("Mixed test results - Successful searches: {}, Successful bookings: {}",
////      searchSuccess.get(), bookingSuccess.get());
////
////    org.example.Coach coach = trainMap.get("TEST1").getCoaches().get(0);
////    long availableSeats = coach.getSeatBookings().values().stream()
////      .filter(Objects::isNull)
////      .count();
////
////    assertEquals(5 - bookingSuccess.get(), availableSeats,
////      "Available seats count mismatch");
////  }
////
////  @Test
////  public void testConcurrentCancellations() throws InterruptedException {
////    logger.info("Starting testConcurrentCancellations");
////
////    // First create 5 valid bookings
////    List<String> pnrs = new ArrayList<>();
////    for (int i = 0; i < 5; i++) {
////      String response = requestHandler.handleBooking(
////        new String[]{"BOOK", "user1", "TEST1", "C1", "1"}
////      );
////      if (response.contains("org.example.Booking successful")) {
////        pnrs.add(response.split("PNR: ")[1].split(" ")[0]);
////      }
////    }
////
////    final int THREAD_COUNT = pnrs.size();
////    AtomicInteger cancellationSuccess = new AtomicInteger(0);
////
////    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
////
////    for (String pnr : pnrs) {
////      executor.execute(() -> {
////        String response = requestHandler.handleCancellation(
////          new String[]{"CANCEL", pnr}
////        );
////        if (response.contains("cancelled successfully")) {
////          logger.debug("Cancellation succeeded for PNR: {}", pnr);
////          cancellationSuccess.incrementAndGet();
////        }
////      });
////    }
////
////    executor.shutdown();
////    assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS),
////      "Cancellation test timed out");
////
////    logger.info("Cancellation results - Success: {}/{}",
////      cancellationSuccess.get(), THREAD_COUNT);
////
////    assertEquals(THREAD_COUNT, cancellationSuccess.get(),
////      "Not all cancellations succeeded");
////
////    org.example.Coach coach = trainMap.get("TEST1").getCoaches().get(0);
////    long availableSeats = coach.getSeatBookings().values().stream()
////      .filter(Objects::isNull)
////      .count();
////    assertEquals(5, availableSeats, "All seats should be available after cancellations");
////  }
////
////  @Test
////  public void testZeroSeatBooking() {
////    logger.info("Starting testZeroSeatBooking");
////    String response = requestHandler.handleBooking(
////      new String[]{"BOOK", "user1", "TEST1", "C1", "0"}
////    );
////    logger.info("Zero seat booking response: {}", response);
////    assertTrue(response.contains("Invalid seat count"),
////      "Should reject zero seat booking");
////  }
////
////  @Test
////  public void testInvalidTrainId() {
////    logger.info("Starting testInvalidTrainId");
////    String response = requestHandler.handleBooking(
////      new String[]{"BOOK", "user1", "INVALID_TRAIN", "C1", "1"}
////    );
////    logger.info("Invalid train ID response: {}", response);
////    assertTrue(response.contains("org.example.Train not found"),
////      "Should detect invalid train ID");
////  }
////
////
////}
//
//import org.example.server.RequestHandler;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.time.LocalDate;
//import java.util.*;
//import java.util.concurrent.*;
//import java.util.concurrent.atomic.AtomicInteger;
//import org.example.*;
//
//public class TrainSystemConcurrencyTest
//{
//  private static final Logger logger = LoggerFactory.getLogger(TrainSystemConcurrencyTest.class);
//
//  private List<Train> trains;
//
//  private Map<String, Train> trainMap;
//
//  private Map<String, Booking> bookings;
//
//  private RequestHandler requestHandler;
//
//  @BeforeEach
//  public void setUp()
//  {
//    resetTestData();
//
//    logger.info("Test setup completed - fresh test environment initialized");
//  }
//
//  private void resetTestData()
//  {
//    trains = new ArrayList<>();
//
//    trainMap = new HashMap<>();
//
//    bookings = new ConcurrentHashMap<>();
//
//    Train testTrain = new Train("TEST1", "London", "Paris",
//      LocalDate.now(), LocalDate.now().plusDays(1));
//
//    testTrain.getCoaches().add(new Coach("AC", "C1", 200));
//
//    trains.add(testTrain);
//
//    trainMap.put("TEST1", testTrain);
//
//    requestHandler = new RequestHandler(null, trainMap, bookings);
//  }
//
//  @Test
//  public void testConcurrentBooking() throws InterruptedException
//  {
//    final int THREAD_COUNT = 100;
//
//    logger.info("Starting testConcurrentBooking with {} threads", THREAD_COUNT);
//
//    AtomicInteger successCount = new AtomicInteger(0);
//
//    AtomicInteger failureCount = new AtomicInteger(0);
//
//    ExecutorService executor = Executors.newFixedThreadPool(50);
//
//    for (int i = 0; i < THREAD_COUNT; i++) {
//      executor.execute(() -> {
//        String response = requestHandler.handleBooking(
//          new String[]{"BOOK", "user1", "TEST1", "C1", "1"}
//        );
//
//        if (response.contains("Booking successful"))
//        {
//          logger.debug("Booking succeeded for thread {}", Thread.currentThread().getId());
//
//          successCount.incrementAndGet();
//        }
//        else
//        {
//          logger.debug("Booking failed for thread {}", Thread.currentThread().getId());
//
//          failureCount.incrementAndGet();
//        }
//      });
//    }
//
//    executor.shutdown();
//
//    assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS), "Test execution timed out");
//
//    logger.info("Test results - Success: {}, Failures: {}", successCount.get(), failureCount.get());
//
//    Coach coach = trainMap.get("TEST1").getCoaches().get(0);
//
//    long bookedSeats = coach.getSeatBookings().values().stream()
//      .filter(Objects::nonNull)
//      .count();
//
//    assertEquals(100, successCount.get(), "Incorrect number of successful bookings");
//
//    assertEquals(0, failureCount.get(), "Incorrect number of failed bookings");
//
//    assertEquals(100, bookedSeats, "Mismatch in actual booked seats");
//  }
//
//  @Test
//  public void testOverbooking500Seats() throws InterruptedException
//  {
//    final int THREAD_COUNT = 500;
//
//    logger.info("Starting testOverbooking500Seats with {} threads", THREAD_COUNT);
//
//    AtomicInteger successCount = new AtomicInteger(0);
//
//    AtomicInteger failureCount = new AtomicInteger(0);
//
//    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
//
//    for (int i = 0; i < THREAD_COUNT; i++)
//    {
//      executor.execute(() -> {
//        String response = requestHandler.handleBooking(
//          new String[]{"BOOK", "user1", "TEST1", "C1", "3"}
//        );
//        if (response.contains("Booking successful"))
//        {
//          logger.debug("Booking succeeded for thread {}", Thread.currentThread().getId());
//
//          successCount.incrementAndGet();
//        }
//        else
//        {
//          logger.debug("Booking failed for thread {}", Thread.currentThread().getId());
//
//          failureCount.incrementAndGet();
//        }
//      });
//    }
//
//    executor.shutdown();
//
//    assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS), "Test execution timed out");
//
//    logger.info("Test results - Success: {}, Failures: {}", successCount.get(), failureCount.get());
//
//    Coach coach = trainMap.get("TEST1").getCoaches().get(0);
//
//    long bookedSeats = coach.getSeatBookings().values().stream()
//      .filter(Objects::nonNull)
//      .count();
//
//    assertEquals(66, successCount.get(), "Should succeed for 66 bookings (198 seats)");
//
//    assertEquals(434, failureCount.get(), "Should fail for 434 bookings");
//
//    assertEquals(198, bookedSeats, "Should book 198 seats");
//  }
//
//  @Test
//  public void testConcurrentSearchAndBooking() throws InterruptedException
//  {
//    final int THREAD_COUNT = 100;
//
//    logger.info("Starting testConcurrentSearchAndBooking with {} threads", THREAD_COUNT);
//
//    AtomicInteger searchSuccess = new AtomicInteger(0);
//
//    AtomicInteger bookingSuccess = new AtomicInteger(0);
//
//    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
//
//    for (int i = 0; i < THREAD_COUNT; i++)
//    {
//      if (i % 2 == 0)
//      {
//        executor.execute(() -> {
//          String response = requestHandler.handleSearch(
//            new String[]{"SEARCH", "London", "Paris", LocalDate.now().toString()}
//          );
//
//          if (!response.contains("No trains available"))
//          {
//            logger.debug("Search succeeded");
//
//            searchSuccess.incrementAndGet();
//          }
//        });
//      }
//      else
//      {
//        executor.execute(() -> {
//          String response = requestHandler.handleBooking(
//            new String[]{"BOOK", "user1", "TEST1", "C1", "1"}
//          );
//
//          if (response.contains("Booking successful"))
//          {
//            logger.debug("Booking succeeded in mixed test");
//
//            bookingSuccess.incrementAndGet();
//          }
//        });
//      }
//    }
//
//    executor.shutdown();
//
//    assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS), "Test execution timed out");
//
//    logger.info("Mixed test results - Successful searches: {}, Successful bookings: {}",
//      searchSuccess.get(), bookingSuccess.get());
//
//    Coach coach = trainMap.get("TEST1").getCoaches().get(0);
//
//    long availableSeats = coach.getSeatBookings().values().stream()
//      .filter(Objects::isNull)
//      .count();
//
//    assertEquals(200 - bookingSuccess.get(), availableSeats, "Available seats count mismatch");
//  }
//
//  @Test
//  public void testConcurrentCancellations() throws InterruptedException
//  {
//    logger.info("Starting testConcurrentCancellations");
//
//    List<String> pnrs = new ArrayList<>();
//
//    for (int i = 0; i < 200; i++)
//    {
//      String response = requestHandler.handleBooking(
//        new String[]{"BOOK", "user1", "TEST1", "C1", "1"}
//      );
//
//      if (response.contains("Booking successful"))
//      {
//        pnrs.add(response.split("PNR: ")[1].split(" ")[0]);
//      }
//    }
//
//    final int THREAD_COUNT = pnrs.size();
//
//    if (THREAD_COUNT == 0)
//    {
//      fail("No bookings were successful, cannot test cancellations");
//    }
//
//    AtomicInteger cancellationSuccess = new AtomicInteger(0);
//
//    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
//
//    for (String pnr : pnrs)
//    {
//      executor.execute(() -> {
//        String response = requestHandler.handleCancellation(
//          new String[]{"CANCEL", pnr}
//        );
//
//        if (response.contains("cancelled successfully"))
//        {
//          logger.debug("Cancellation succeeded for PNR: {}", pnr);
//
//          cancellationSuccess.incrementAndGet();
//        }
//      });
//    }
//
//    executor.shutdown();
//
//    assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS), "Cancellation test timed out");
//
//    logger.info("Cancellation results - Success: {}/{}", cancellationSuccess.get(), THREAD_COUNT);
//
//    assertEquals(THREAD_COUNT, cancellationSuccess.get(), "Not all cancellations succeeded");
//
//    Coach coach = trainMap.get("TEST1").getCoaches().get(0);
//
//    long availableSeats = coach.getSeatBookings().values().stream()
//      .filter(Objects::isNull)
//      .count();
//
//    assertEquals(200, availableSeats, "All seats should be available after cancellations");
//  }
//
//  @Test
//  public void testZeroSeatBooking()
//  {
//    logger.info("Starting testZeroSeatBooking");
//
//    String response = requestHandler.handleBooking(
//      new String[]{"BOOK", "user1", "TEST1", "C1", "0"}
//    );
//
//    logger.info("Zero seat booking response: {}", response);
//
//    assertTrue(response.contains("Invalid number of seats"), "Should reject zero seat booking");
//  }
//
//  @Test
//  public void testInvalidTrainId()
//  {
//    logger.info("Starting testInvalidTrainId");
//
//    String response = requestHandler.handleBooking(
//      new String[]{"BOOK", "user1", "INVALID_TRAIN", "C1", "1"}
//    );
//
//    logger.info("Invalid train ID response: {}", response);
//
//    assertTrue(response.contains("Train not found"), "Should detect invalid train ID");
//  }
//}


import org.example.server.RequestHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.example.*;

public class TrainSystemConcurrencyTest {
  private static final Logger logger = LoggerFactory.getLogger(TrainSystemConcurrencyTest.class);
  private ConcurrentHashMap<String, Train> trainMap;
  private ConcurrentHashMap<String, Booking> bookings;
  private RequestHandler requestHandler;

  @BeforeEach
  public void setUp() {
    resetTestData();
    logger.info("Test setup completed - fresh test environment initialized");
  }

  private void resetTestData() {
    trainMap = new ConcurrentHashMap<>();
    bookings = new ConcurrentHashMap<>();

    // Create test train with 3 coaches, 5 seats each (15 total)
    Train testTrain = new Train("TEST1", "London", "Paris",
      LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
    testTrain.addCoach("Sleeper", "C1", 5); // C1-S1 to C1-S5
    testTrain.addCoach("Sleeper", "C2", 5); // C2-S1 to C2-S5
    testTrain.addCoach("Sleeper", "C3", 5); // C3-S1 to C3-S5
    trainMap.put("TEST1", testTrain);

    requestHandler = new RequestHandler(null, trainMap, bookings);
  }

  @Test
  public void testConcurrentBookingSingleSeat() throws InterruptedException {
    final int THREAD_COUNT = 20; // More threads than seats (15)
    logger.info("Starting testConcurrentBookingSingleSeat with {} threads", THREAD_COUNT);

    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failureCount = new AtomicInteger(0);

    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

    for (int i = 0; i < THREAD_COUNT; i++) {
      executor.execute(() -> {
        String response = requestHandler.handleBooking(
          new String[]{"BOOK", "user" + Thread.currentThread().getId(), "TEST1", "Sleeper", "1"}
        );
        if (response.contains("Booking successful")) {
          logger.debug("Booking succeeded for thread {}", Thread.currentThread().getId());
          successCount.incrementAndGet();
        } else {
          logger.debug("Booking failed for thread {}", Thread.currentThread().getId());
          failureCount.incrementAndGet();
        }
      });
    }

    executor.shutdown();
    assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS), "Test execution timed out");

    logger.info("Test results - Success: {}, Failures: {}", successCount.get(), failureCount.get());

    Train train = trainMap.get("TEST1");
    long totalBookedSeats = train.getCoachTypes().get("Sleeper").stream()
      .mapToLong(coach -> coach.getSeatBookings().values().stream()
        .filter(booking -> booking != Coach.UNBOOKED).count())
      .sum();

    assertEquals(15, successCount.get(), "Should book all 15 seats");
    assertEquals(5, failureCount.get(), "Should fail for excess threads");
    assertEquals(15, totalBookedSeats, "Mismatch in total booked seats");
  }

  @Test
  public void testConcurrentBookingMultipleSeats() throws InterruptedException {
    final int THREAD_COUNT = 100; // Each booking 3 seats, total 30 requested vs 15 available
    logger.info("Starting testConcurrentBookingMultipleSeats with {} threads", THREAD_COUNT);

    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failureCount = new AtomicInteger(0);

    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

    for (int i = 0; i < THREAD_COUNT; i++) {
      executor.execute(() -> {
        String response = requestHandler.handleBooking(
          new String[]{"BOOK", "user" + Thread.currentThread().getId(), "TEST1", "Sleeper", "3"}
        );
        if (response.contains("Booking successful")) {
          logger.debug("Booking succeeded for thread {}", Thread.currentThread().getId());
          successCount.incrementAndGet();
        } else {
          logger.debug("Booking failed for thread {}", Thread.currentThread().getId());
          failureCount.incrementAndGet();
        }
      });
    }

    executor.shutdown();
    assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS), "Test execution timed out");

    logger.info("Test results - Success: {}, Failures: {}", successCount.get(), failureCount.get());

    Train train = trainMap.get("TEST1");
    long totalBookedSeats = train.getCoachTypes().get("Sleeper").stream()
      .mapToLong(coach -> coach.getSeatBookings().values().stream()
        .filter(booking -> booking != Coach.UNBOOKED).count())
      .sum();

    assertEquals(5, successCount.get(), "Should succeed for 5 bookings (15 seats)");
    assertEquals(95, failureCount.get(), "Should fail for remaining 5");
    assertEquals(15, totalBookedSeats, "Should book exactly 15 seats");
  }

  @Test
  public void testConcurrentSearchAndBooking() throws InterruptedException {
    final int THREAD_COUNT = 20;
    logger.info("Starting testConcurrentSearchAndBooking with {} threads", THREAD_COUNT);

    AtomicInteger searchSuccess = new AtomicInteger(0);
    AtomicInteger bookingSuccess = new AtomicInteger(0);

    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

    for (int i = 0; i < THREAD_COUNT; i++) {
      if (i % 2 == 0) {
        executor.execute(() -> {
          String response = requestHandler.handleSearch(
            new String[]{"SEARCH", "London", "Paris", LocalDate.now().plusDays(1).toString()}
          );
          if (!response.contains("No trains available")) {
            logger.debug("Search succeeded");
            searchSuccess.incrementAndGet();
          }
        });
      } else {
        executor.execute(() -> {
          String response = requestHandler.handleBooking(
            new String[]{"BOOK", "user" + Thread.currentThread().getId(), "TEST1", "Sleeper", "2"}
          );
          if (response.contains("Booking successful")) {
            logger.debug("Booking succeeded in mixed test");
            bookingSuccess.incrementAndGet();
          }
        });
      }
    }

    executor.shutdown();
    assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS), "Test execution timed out");

    logger.info("Mixed test results - Successful searches: {}, Successful bookings: {}",
      searchSuccess.get(), bookingSuccess.get());

    Train train = trainMap.get("TEST1");
    long availableSeats = train.getCoachTypes().get("Sleeper").stream()
      .mapToLong(Coach::getAvailableSeatCount)
      .sum();

    assertEquals(10, searchSuccess.get(), "All searches should succeed");
    assertEquals(15 - (bookingSuccess.get() * 2), availableSeats, "Available seats count mismatch");
  }

  @Test
  public void testConcurrentCancellations() throws InterruptedException {
    logger.info("Starting testConcurrentCancellations");

    // Pre-book 10 seats across multiple coaches
    List<String> pnrs = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      String response = requestHandler.handleBooking(
        new String[]{"BOOK", "user" + i, "TEST1", "Sleeper", "2"}
      );
      if (response.contains("Booking successful")) {
        pnrs.add(response.split("PNR: ")[1].split(" ")[0]);
      }
    }

    final int THREAD_COUNT = pnrs.size();
    if (THREAD_COUNT == 0) {
      fail("No bookings were successful, cannot test cancellations");
    }

    AtomicInteger cancellationSuccess = new AtomicInteger(0);

    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

    for (String pnr : pnrs) {
      executor.execute(() -> {
        String response = requestHandler.handleCancellation(
          new String[]{"CANCEL", pnr}
        );
        if (response.contains("cancelled successfully")) {
          logger.debug("Cancellation succeeded for PNR: {}", pnr);
          cancellationSuccess.incrementAndGet();
        }
      });
    }

    executor.shutdown();
    assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS), "Cancellation test timed out");

    logger.info("Cancellation results - Success: {}/{}", cancellationSuccess.get(), THREAD_COUNT);

    Train train = trainMap.get("TEST1");
    long availableSeats = train.getCoachTypes().get("Sleeper").stream()
      .mapToLong(Coach::getAvailableSeatCount)
      .sum();

    assertEquals(THREAD_COUNT, cancellationSuccess.get(), "Not all cancellations succeeded");
    assertEquals(15, availableSeats, "All seats should be available after cancellations");
  }

  @Test
  public void testZeroSeatBooking() {
    logger.info("Starting testZeroSeatBooking");
    String response = requestHandler.handleBooking(
      new String[]{"BOOK", "user1", "TEST1", "Sleeper", "0"}
    );
    logger.info("Zero seat booking response: {}", response);
    assertTrue(response.contains("Invalid number of seats"), "Should reject zero seat booking");
  }

  @Test
  public void testInvalidTrainId() {
    logger.info("Starting testInvalidTrainId");
    String response = requestHandler.handleBooking(
      new String[]{"BOOK", "user1", "INVALID_TRAIN", "Sleeper", "1"}
    );
    logger.info("Invalid train ID response: {}", response);
    assertTrue(response.contains("Train not found"), "Should detect invalid train ID");
  }
}
