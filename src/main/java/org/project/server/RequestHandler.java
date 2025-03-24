package org.project.server;

import org.project.Coach;
import org.project.Train;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RequestHandler implements Runnable
{
  private Socket socket;

  private ConcurrentHashMap<String, Train> trainMap;

  private ConcurrentHashMap<String, Map<String, String>> bookings;

  public RequestHandler(Socket socket, ConcurrentHashMap<String, Train> trainMap,
                        ConcurrentHashMap<String, Map<String, String>> bookings)
  {
    this.socket = socket;

    this.trainMap = trainMap;

    this.bookings = bookings;
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
    for (var train : result)
    {
      responseBuilder.append("Train ID: ").append(train.getTrainId())
        .append(", Source: ").append(train.getSource())
        .append(", Destination: ").append(train.getDestination())
        .append(", Departure: ").append(train.getDepartureDate())
        .append(", Arrival: ").append(train.getArrivalDate())
        .append("\nCoaches:\n");

      // Iterate through all coach types and their coaches
      for (var entry : train.getCoachTypes().entrySet()) {
        var coachType = entry.getKey();
        var coaches = entry.getValue();

        for (var coach : coaches) {
          responseBuilder
            .append("Type: ").append(coachType)
            .append(", Available Seats: ").append(coach.getAvailableSeatCount())
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

    for (var train : trainMap.values())
    {
      if (train.getSource().equalsIgnoreCase(source) &&
        train.getDestination().equalsIgnoreCase(destination) &&
        train.getDepartureDate().equals(date))
      {
        result.add(train);
      }
    }
    return result;
  }

  public String handleBooking(String[] parts)
  {
    String userId = parts[1];
    String trainId = parts[2];
    String coachType = parts[3];
    int numberOfSeats = Integer.parseInt(parts[4]);

    if (numberOfSeats <= 0)
    {
      return "Invalid number of seats";
    }

    Train train = trainMap.get(trainId);
    if (train == null)
    {
      return "Train not found";
    }

    var coaches = train.getCoachTypes().get(coachType.toLowerCase());

    if (coaches == null || coaches.isEmpty())
    {
      return "Coach not found";
    }

    String pnr = generatePNR();
    var confirmedSeats = new ArrayList<String>();

    for (var coach : coaches)
    {
      while (confirmedSeats.size() < numberOfSeats)
      {
        String seat = coach.pollAndBookSeat(pnr);
        if (seat != null)
        {
          confirmedSeats.add(seat);
        }
        else
        {
          break; // No more seats in this coach
        }
      }
      if (confirmedSeats.size() == numberOfSeats) break;
    }

    if (confirmedSeats.size() < numberOfSeats)
    {
      coaches.forEach(c -> c.releaseSeats(confirmedSeats));
      return "Not enough seats available";
    }

    // Store booking data
    Map<String, String> bookingData = new HashMap<>();
    bookingData.put("userId", userId);
    bookingData.put("trainId", trainId);
    bookingData.put("coachType", coachType);
    bookingData.put("seats", String.join(",", confirmedSeats));
    bookings.put(pnr, bookingData);

    return "Booking successful. PNR: " + pnr + " Seats: " + String.join(",", confirmedSeats);
  }

  private String generatePNR()
  {
    return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5);
  }

  public String handleCancellation(String[] parts)
  {
    String pnr = parts[1];

    Map<String, String> booking = bookings.remove(pnr);

    if (booking == null)
    {
      return "Booking not found";
    }

    Train train = trainMap.get(booking.get("trainId"));

    String coachType = booking.get("coachType").toLowerCase();

    if (train == null) return "Train not found";

    // Group seats by coach based on prefix (e.g., "C1-S1" -> "S1")
    List<String> seats = List.of(booking.get("seats").split(","));

    List<Coach> coaches = train.getCoachTypes().get(coachType);

    if (coaches == null || coaches.isEmpty())
      return "Coach type not found in train";

    for (Coach coach : coaches)
    {
      List<String> seatsInCoach = seats.stream()
        .filter(seat -> seat.startsWith(coach.getCoachId()))
        .collect(Collectors.toList());

      if (!seatsInCoach.isEmpty())
      {
        coach.releaseSeats(seatsInCoach);
      }
    }
    return "Booking with PNR: " + pnr + " cancelled successfully.";
  }
}



//  public String handleBooking(String[] parts)
//  {
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
//    if (train == null)
//    {
//      return "Train not found";
//    }
//
//    var coaches = train.getCoachTypes().get(coachType.toLowerCase());
//
//    System.out.println(coaches);
//
//    if (coaches == null || coaches.isEmpty())
//    {
//      return "Coach not found";
//    }
//
//    var totalAvailable = coaches.stream()
//      .mapToInt(Coach::getAvailableSeatCount)
//      .sum();
//
//    if (totalAvailable < numberOfSeats)
//    {
//      return "Not enough seats available";
//    }
//
//    // Collect seats across coaches
//    var confirmedSeats = new ArrayList<String>();
//    String pnr = generatePNR();
//    for (var coach : coaches)
//    {
//      var availableSeats = coach.getAvailableSeats();
//
//      while (confirmedSeats.size() < numberOfSeats && !availableSeats.isEmpty())
//      {
//
//        String seat = availableSeats.get(0);
//        if(coach.tryBookSeats(List.of(seat), pnr))
//        {
//          confirmedSeats.add(seat);
//        }
//        else
//        {
//          continue;
//        }
//      }
//      if (confirmedSeats.size() == numberOfSeats) break;
//    }
//
//    if (confirmedSeats.size() < numberOfSeats)
//    {
//      coaches.forEach(c -> c.releaseSeats(confirmedSeats));
//      return "Not enough seats available";
//    }
//
//    Map<String, String> bookingData = new HashMap<>();
//
//    bookingData.put("userId", userId);
//
//    bookingData.put("trainId", trainId);
//
//    bookingData.put("seats", String.join(",", confirmedSeats));
//
//    bookings.put(pnr, bookingData);
//
//    return "Booking successful. PNR: " + pnr + " Seats: " + String.join(",", confirmedSeats);
//  }
