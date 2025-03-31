package org.project.server;

import org.project.schema.Coach;
import org.project.schema.Train;
import org.project.database.TrainDatabase;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;


public class RequestHandler implements Runnable
{
  private final Socket socket;

  private final TrainDatabase database;

//  private final ConcurrentHashMap<String, Train> trainMap;
//
//  private final ConcurrentHashMap<String, Map<String, String>> bookingRecord;

  public RequestHandler(Socket socket)
  {
    this.socket = socket;

    this.database = TrainDatabase.INSTANCE;
//
//    this.trainMap = trainMap;
//
//    this.bookingRecord = bookingRecord;
  }

  @Override
  public void run()
  {
    try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
         ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()))
    {

      System.out.println("Client connected");

      // Read HashMap request
      var request = (Map<String, String>) in.readObject();

      var response = processRequest(request); // TODO use var

      // Send response HashMap
      out.writeObject(response);

      out.flush();

    }
    catch (Exception e)
    {
      System.err.println("Connection error: " + e.getMessage());
    }
    finally
    {
      try
      {
        socket.close();

        System.out.println("Client disconnected: " + socket.getInetAddress());
      }
      catch (Exception e)
      {
        System.err.println("Error closing socket: " + e.getMessage());
      }
    }
  }

  public Map<String, String> processRequest(Map<String, String> request)
  {
    var command = request.get("command");

    Map<String, String> response = new HashMap<>();

    switch (command)
    {
      case "SEARCH":
        response.put("message", handleSearch(request));
        break;

      case "BOOK":
        response.put("message", handleBooking(request));
        break;

      case "CANCEL":
        response.put("message", handleCancellation(request));
        break;

      case "MY_BOOKINGS":
        response.put("message", handleMyBookings(request));
        break;

        default:
        response.put("message", "400 Invalid command");
    }
    return response;
  }

  public String handleSearch(Map<String, String> request)
  {
    var source = request.get("source");

    var destination = request.get("destination");

    var dateStr = request.get("date");

    if (source == null || destination == null || dateStr == null)
    {
      return "400 Invalid Search command";
    }

    LocalDate date;
    try
    {
      date = LocalDate.parse(dateStr);
    }
    catch (DateTimeParseException e)
    {
      return "400 Invalid date format";
    }

    if (date.isBefore(LocalDate.now()))
    {
      return "400 Date cannot be in the past";
    }

    var result = searchTrains(source, destination, date);

    if (result.isEmpty())
    {
      return "404 No trains available for the given source and destination at this time.";
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

      for (var entry : train.getCoachTypes().entrySet())
      {
        var coachType = entry.getKey();
        var coaches = entry.getValue();
        for (var coach : coaches)
        {
          responseBuilder.append("Type: ").append(coachType)
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

    for (var train : database.getTrains().values())
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

  public String handleBooking(Map<String, String> request)
  {
    var userId = request.get("userId");

    var trainId = request.get("trainId");

    var coachType = request.get("coachType");

    var seatsStr = request.get("numberOfSeats");

    if (userId == null || trainId == null || coachType == null || seatsStr == null)
    {
      return "400 Invalid book command";
    }

    int numberOfSeats;

    try
    {
      numberOfSeats = Integer.parseInt(seatsStr);
    }
    catch (NumberFormatException e)
    {
      return "400 Invalid number of seats";
    }

    if (numberOfSeats <= 0)
    {
      return "400 Invalid number of seats";
    }

    Train train = database.getTrains().get(trainId);

    if (train == null)
    {
      return "404 Train not found";
    }

    var coaches = train.getCoachTypes().get(coachType.toLowerCase());

    if (coaches == null || coaches.isEmpty())
    {
      return "404 Coach not found";
    }

    int totalAvailable = coaches.stream()
      .mapToInt(Coach::getAvailableSeatCount)
      .sum();

    if (totalAvailable < numberOfSeats)
    {
      return "409 Not enough seats";
    }

    var pnr = generatePNR();

    var confirmedSeats = new ArrayList<String>();

    for (var coach : coaches)
    {
      while (confirmedSeats.size() < numberOfSeats)
      {
        var seat = coach.pollAndBookSeat(pnr);

        if (seat != null)
        {
          confirmedSeats.add(seat);
        }
        else
        {
          break;
        }
      }
      if (confirmedSeats.size() == numberOfSeats) break;
    }

    if (confirmedSeats.size() < numberOfSeats)
    {
      coaches.forEach(c -> c.releaseSeats(confirmedSeats));

      return "409 Not enough seats available";
    }

    Map<String, String> bookingData = new HashMap<>();
    bookingData.put("userId", userId);
    bookingData.put("trainId", trainId);
    bookingData.put("coachType", coachType);
    bookingData.put("seats", String.join(",", confirmedSeats));
    database.getBookingRecord().put(pnr, bookingData);

    return "Booking successful. Train id: " + trainId + " PNR: " + pnr + " Seats: " + String.join(",", confirmedSeats);
  }

  private String generatePNR()
  {
    return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5);
  }

  public String handleCancellation(Map<String, String> request)
  {
    var userId = request.get("userId");

    var pnr = request.get("pnr");

    if (userId == null || pnr == null)
    {
      return "400 Invalid command";
    }

    Map<String, String> booking = database.getBookingRecord().get(pnr);


    if (booking == null)
    {
      return "400 Booking not found";
    }

    if (!booking.get("userId").equals(userId))
    {
      return "403 Booking cancellation denied";
    }

    database.getBookingRecord().remove(pnr);

    Train train = database.getTrains().get(booking.get("trainId"));

    var coachType = booking.get("coachType").toLowerCase();

    if (train == null) return "Train not found";

    var seats = List.of(booking.get("seats").split(","));

    var coaches = train.getCoachTypes().get(coachType);

    if (coaches == null || coaches.isEmpty())
    {
      return "Coach type not found in train";
    }

    for (Coach coach : coaches)
    {
      var seatsInCoach = seats.stream()
        .filter(seat -> seat.startsWith(coach.getCoachId()))
        .collect(Collectors.toList());

      if (!seatsInCoach.isEmpty())
      {
        coach.releaseSeats(seatsInCoach);
      }
    }
    return "Booking with PNR: " + pnr + " cancelled successfully.";
  }

  public String handleMyBookings(Map<String, String> request) {
    var pnr = request.get("pnr");
    if (pnr == null) {
      return "400 Invalid pnr";
    }

    Map<String, String> userBookings = database.getBookingRecord().get(pnr);

    if (userBookings == null) {
      return "No bookings found for pnr: " + pnr;
    }

    return String.format(
      "Booking Details:\nPNR: %s\nTrain ID: %s\nCoach Type: %s\nSeats: %s",
      pnr,
      userBookings.get("trainId"),
      userBookings.get("coachType"),
      userBookings.get("seats")
    );
  }
}

//Map<String, Integer> coachTypeTotals = new HashMap<>();
//      for (var entry : train.getCoachTypes().entrySet()) {
//String coachType = entry.getKey();
//List<Coach> coaches = entry.getValue();
//int totalAvailableSeats = coaches.stream()
//  .mapToInt(Coach::getAvailableSeatCount)
//  .sum();
//        coachTypeTotals.put(coachType, totalAvailableSeats);
//      }
//
//        // Append aggregated totals to response
//        for (var entry : coachTypeTotals.entrySet()) {
//  responseBuilder.append("Type: ").append(entry.getKey())
//  .append(", Total Available Seats: ").append(entry.getValue())
//  .append("\n");
//      }
//        responseBuilder.append("\n");
