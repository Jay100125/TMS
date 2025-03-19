package org.example.server;

import org.example.*;
//import main.java.Coach;
//import main.java.Train;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class RequestHandler implements Runnable
{
  private Socket socket;

  private ConcurrentHashMap<String, Train> trainMap;

  private ConcurrentHashMap<String, Booking> bookings;

  public RequestHandler(Socket socket, ConcurrentHashMap<String, Train> trainMap,
                        Map<String, Booking> bookings)
  {
    this.socket = socket;

    this.trainMap = trainMap;

    this.bookings = (ConcurrentHashMap<String, Booking>) bookings;
  }

  @Override
  public void run()
  {
    try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         PrintWriter out = new PrintWriter(socket.getOutputStream(), true))
    {
      System.out.println("Client connected");

      String request = in.readLine();

      if (request != null)
      {
        String response = processRequest(request);

        if (response != null)
        {
          out.println(response);
        }
      }
    }
    catch (IOException e)
    {
      System.err.println("Connection error: " + e.getMessage());
    }
  }

  public String processRequest(String request)
  {
    String[] parts = request.split(" ");

    String command = parts[0];

    switch (command)
    {
      case "SEARCH":
        return handleSearch(parts);

      case "BOOK":
        return handleBooking(parts);

      case "CANCEL":
        return handleCancellation(parts);

      default:
        return "Invalid command";
    }
  }
//  public String handleSearch(String[] parts) {
//    String source = parts[1];
//    String destination = parts[2];
//    LocalDate date;
//    try {
//      date = LocalDate.parse(parts[3]);
//    } catch (DateTimeParseException e) {
//      return "Invalid date format";
//    }
//    if (date.isBefore(LocalDate.now())) {
//      return "Date cannot be in the past";
//    }
//
//    List<Train> result = searchTrains(source, destination, date);
//    if (result.isEmpty()) {
//      return "No trains available for the given criteria.";
//    }
//
//    var responseBuilder = new StringBuilder("Available Trains:\n");
//    for (var train : result) {
//      responseBuilder.append("Train ID: ").append(train.getTrainId())
//        .append(", Source: ").append(train.getSource())
//        .append(", Destination: ").append(train.getDestination())
//        .append(", Departure: ").append(train.getDepartureDate())
//        .append(", Arrival: ").append(train.getArrivalTime())
//        .append("\nCoach Types:\n");
//
//      // Aggregate by coach type
//      for (var entry : train.getCoachTypes().entrySet()) {
//        var coachType = entry.getKey();
//        var coaches = entry.getValue();
//
//        // Collect all available seats for this coach type
//        var allAvailableSeats = new ArrayList<String>();
//        int totalSeats = 0;
//        for (var coach : coaches) {
//          allAvailableSeats.addAll(coach.getAvailableSeats());
//          totalSeats += coach.getSeatBookings().size();
//        }
//        int availableSeatsCount = allAvailableSeats.size();
//
//        responseBuilder.append("  Type: ").append(coachType)
//          .append(", Total Seats: ").append(totalSeats)
//          .append(", Available Seats: ").append(availableSeatsCount);
//        if (!allAvailableSeats.isEmpty()) {
//          responseBuilder.append(", Seats: ").append(String.join(",", allAvailableSeats));
//        }
//        responseBuilder.append("\n");
//      }
//      responseBuilder.append("\n");
//    }
//    return responseBuilder.toString();
//  }
  public String handleSearch(String[] parts)
  {
    String source = parts[1];

    String destination = parts[2];

    LocalDate date;

    try
    {
      date = LocalDate.parse(parts[3]);
    }
    catch (DateTimeParseException e)
    {
      return "Invalid date format";
    }

    if(date.isBefore(LocalDate.now()))
    {
      return "Date cannot be in the past";
    }

    List<Train> result = searchTrains(source, destination, date);

    if (result.isEmpty())
    {
      return "No trains available for the given criteria.";
    }

    var responseBuilder = new StringBuilder("Available Trains:\n");
    for (var train : result) {
      responseBuilder.append("Train ID: ").append(train.getTrainId())
        .append(", Source: ").append(train.getSource())
        .append(", Destination: ").append(train.getDestination())
        .append(", Departure: ").append(train.getDepartureDate())
        .append(", Arrival: ").append(train.getArrivalTime())
        .append("\nCoaches:\n");

      // Iterate through all coach types and their coaches
      for (var entry : train.getCoachTypes().entrySet()) {
        var coachType = entry.getKey();
        var coaches = entry.getValue();

        for (var coach : coaches) {
          var availableSeats = coach.getAvailableSeatCount();

          responseBuilder
            .append("Type: ").append(coachType)
            .append(", Available Seats: ").append(availableSeats)
            .append("\n");
        }
      }
      responseBuilder.append("\n");
    }
    return responseBuilder.toString();
  }

  private List<Train> searchTrains(String source, String destination, LocalDate date)
  {
    var result = new ArrayList<Train>();
    for (var train : trainMap.values()) {
      if (train.getSource().equalsIgnoreCase(source) &&
        train.getDestination().equalsIgnoreCase(destination) &&
        train.getDepartureDate().equals(date)) {
        result.add(train);
      }
    }
    return result;
  }

//  public String handleBooking(String[] parts) {
//    System.out.println("Booking called");
//    String userId = parts[1];
//
//    String trainId = parts[2];
//
//    String coachType = parts[3];
//
//    int numberOfSeats = Integer.parseInt(parts[4]);
//
//    if (numberOfSeats <= 0) {
//      return "Invalid number of seats";
//    }
//
//    Train train = trainMap.get(trainId);
//
//    if (train == null) return "Train not found";
//
//    var coaches = train.getCoachTypes().get(coachType);
//
//    System.out.println(coaches);
//
//    if (coaches == null || coaches.isEmpty()) {
//      return "Coach not found";
//    }
//
//    var totalAvailable = coaches.stream()
//      .mapToInt(Coach::getAvailableSeatCount)
//      .sum();
//
//    if (totalAvailable < numberOfSeats) {
//      return "Not enough seats available";
//    }
//    System.out.println(totalAvailable);
//    var confirmedSeats = new ArrayList<String>();
//
//    for (var coach : coaches) {
//      var availableSeats = coach.getAvailableSeats();
//      while (confirmedSeats.size() < numberOfSeats && !availableSeats.isEmpty()) {
//        var seat = availableSeats.peek();
//        System.out.println(seat);
//        confirmedSeats.add(seat);
//      }
//    }
//
//      if (confirmedSeats.size() == numberOfSeats) {
//        var pnr = generatePNR();
//        var booking = new Booking(pnr, userId, trainId, coach.getCoachId(), confirmedSeats);
//
//        if (coach.tryBookSeats(confirmedSeats, booking)) {
//          bookings.put(pnr, booking);
//          return "Booking successful. PNR: " + pnr + " Seats: " +
//            String.join(",", confirmedSeats);
//        } else {
//          confirmedSeats.clear();
//        }
//      }
//    }
//    return "Failed";
//  }
public String handleBooking(String[] parts) {
  String userId = parts[1];
  String trainId = parts[2];
  String coachType = parts[3];
  int numberOfSeats = Integer.parseInt(parts[4]);

  if (numberOfSeats <= 0) {
    return "Invalid number of seats";
  }

  Train train = trainMap.get(trainId);
  if (train == null) return "Train not found";

  var coaches = train.getCoachTypes().get(coachType);
  System.out.println(coaches);
  if (coaches == null || coaches.isEmpty()) {
    return "Coach not found";
  }

  var totalAvailable = coaches.stream()
    .mapToInt(Coach::getAvailableSeatCount)
    .sum();
  if (totalAvailable < numberOfSeats) {
    return "Not enough seats available";
  }

  // Collect seats across coaches
  var confirmedSeats = new ArrayList<String>();
  for (var coach : coaches) {
    var availableSeats = coach.getAvailableSeats();
    while (confirmedSeats.size() < numberOfSeats && !availableSeats.isEmpty()) {
      confirmedSeats.add(availableSeats.poll()); // Remove and add seat
    }
    if (confirmedSeats.size() == numberOfSeats) break;
  }

  // Create a single booking with all seats
  var pnr = generatePNR();
  var booking = new Booking(pnr, userId, trainId, coachType, confirmedSeats); // coachType as placeholder

  // Book seats on their respective coaches
  for (var coach : coaches) {
    var seatsInCoach = confirmedSeats.stream()
      .filter(seat -> seat.startsWith(coach.getCoachId()))
      .collect(Collectors.toList());
    if (!seatsInCoach.isEmpty()) {
      if (!coach.tryBookSeats(seatsInCoach, booking)) {
        // Rollback all seats booked so far
        coaches.forEach(c -> {
          var bookedHere = confirmedSeats.stream()
            .filter(s -> s.startsWith(c.getCoachId()))
            .collect(Collectors.toList());
          if (!bookedHere.isEmpty()) {
            c.releaseSeats(bookedHere);
          }
        });
        return "Failed";
      }
    }
  }

  bookings.put(pnr, booking);
  return "Booking successful. PNR: " + pnr + " Seats: " + String.join(",", confirmedSeats);
}
  private String generatePNR()
  {
    long pnr = ThreadLocalRandom.current().nextLong(1000000000L, 10000000000L);

    return String.valueOf(pnr);
  }

  public String handleCancellation(String[] parts) {
    String pnr = parts[1];
    Booking booking = bookings.remove(pnr);
    if (booking == null) {
      return "Booking not found";
    }

    Train train = trainMap.get(booking.getTrainId());
    if (train == null) return "Train not found";

    // Group seats by coach based on prefix (e.g., "S1-S1" -> "S1")
    var seatsByCoach = new HashMap<String, List<String>>();
    for (var seat : booking.getSeats()) {
      String coachId = seat.split("-")[0]; // Extract "S1" from "S1-S1"
      seatsByCoach.computeIfAbsent(coachId, k -> new ArrayList<>()).add(seat);
    }

    // Release seats on each affected coach
    var coaches = train.getCoachTypes().get(booking.getCoachId()); // coachId is coachType here
    if (coaches == null) {
      return "Train data corrupted"; // Rare edge case
    }

    for (var coach : coaches) {
      var seatsToRelease = seatsByCoach.getOrDefault(coach.getCoachId(), Collections.emptyList());
      if (!seatsToRelease.isEmpty()) {
        coach.releaseSeats(seatsToRelease);
      }
    }

    return "Booking with PNR: " + pnr + " cancelled successfully.";
  }
}
