# TMS
### testing concurrency
- 50 users are trying to book 5 seats parallel
- only 20 user pass
- other will fail

```java
void testConcurrentBookings() throws InterruptedException {
    // Setup test train with 100 seats
    Train train = new Train("T1", "A", "B", LocalDate.now(), LocalDate.now());
    train.addCoach("AC", "C1", 100);
    db.addTrain(train);

    int threadCount = 50;
    ExecutorService executor = Executors.newFixedThreadPool(5);
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
          request.put("numberOfSeats", "5");

          String response = requestHandler.handleBooking(request);
          if (response.startsWith("Booking successful")) {
            successCount.incrementAndGet();
            System.out.println(response);
          }
          else {
            System.out.println("Failed to book " + response);
          }
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await(5, TimeUnit.SECONDS);
    executor.shutdown();

    // Verify
    int totalBookedSeats = successCount.get() * 5;
    assertTrue(totalBookedSeats <= 100, "Should not overbook seats");
    assertEquals(100 - totalBookedSeats,
      train.getCoachTypes().get("ac").get(0).getAvailableSeatCount());
  }
```

```java
Booking successful. Train id: T1 PNR: d2d76 Seats: C1-S1,C1-S2,C1-S3,C1-S4,C1-S5
Booking successful. Train id: T1 PNR: 59562 Seats: C1-S11,C1-S12,C1-S13,C1-S14,C1-S15
Booking successful. Train id: T1 PNR: 2a64d Seats: C1-S16,C1-S17,C1-S18,C1-S19,C1-S20
Booking successful. Train id: T1 PNR: 22c1e Seats: C1-S6,C1-S7,C1-S8,C1-S9,C1-S10
Booking successful. Train id: T1 PNR: b1ce2 Seats: C1-S21,C1-S22,C1-S23,C1-S24,C1-S25
Booking successful. Train id: T1 PNR: bae70 Seats: C1-S31,C1-S32,C1-S33,C1-S34,C1-S35
Booking successful. Train id: T1 PNR: 29e25 Seats: C1-S36,C1-S37,C1-S38,C1-S39,C1-S40
Booking successful. Train id: T1 PNR: d2f6e Seats: C1-S26,C1-S27,C1-S28,C1-S29,C1-S30
Booking successful. Train id: T1 PNR: 32f03 Seats: C1-S41,C1-S42,C1-S43,C1-S44,C1-S45
Booking successful. Train id: T1 PNR: aa0fa Seats: C1-S46,C1-S47,C1-S48,C1-S49,C1-S50
Booking successful. Train id: T1 PNR: 66262 Seats: C1-S51,C1-S52,C1-S53,C1-S54,C1-S55
Booking successful. Train id: T1 PNR: a7934 Seats: C1-S56,C1-S57,C1-S58,C1-S59,C1-S60
Booking successful. Train id: T1 PNR: 599ef Seats: C1-S61,C1-S62,C1-S63,C1-S64,C1-S65
Booking successful. Train id: T1 PNR: b49f4 Seats: C1-S66,C1-S67,C1-S68,C1-S69,C1-S70
Booking successful. Train id: T1 PNR: be2b6 Seats: C1-S71,C1-S73,C1-S75,C1-S77,C1-S79
Booking successful. Train id: T1 PNR: 7b193 Seats: C1-S72,C1-S74,C1-S76,C1-S78,C1-S80
Booking successful. Train id: T1 PNR: 0470f Seats: C1-S81,C1-S82,C1-S83,C1-S84,C1-S85
Booking successful. Train id: T1 PNR: 36064 Seats: C1-S86,C1-S87,C1-S88,C1-S89,C1-S90
Booking successful. Train id: T1 PNR: 840b1 Seats: C1-S91,C1-S92,C1-S93,C1-S94,C1-S95
Booking successful. Train id: T1 PNR: cb0a3 Seats: C1-S96,C1-S97,C1-S98,C1-S99,C1-S100
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats available
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats available
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats available
Failed to book 409 Not enough seats
Failed to book 409 Not enough seats

```
