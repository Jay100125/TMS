//package org.example.server;
//
////import main.java.Booking;
////import main.java.Coach;
////import main.java.Train;
//import org.example.*;
//
//import java.io.*;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
//public class TextDataPersistenceManager {
//  private static final String TRAINS_FILE = "./data/trains.csv";
//  private static final String COACHES_FILE = "./data/coaches.csv";
//  private static final String BOOKINGS_FILE = "./data/bookings.csv";
//  private static final String SEATS_FILE = "./data/seats.csv";
//
//  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//  public static void saveTrains(List<Train> trains) {
////    createDirectoryIfNotExists();
//
//    try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRAINS_FILE))) {
//      writer.write("trainId,source,destination,departureDate,arrivalDate");
//
//      writer.newLine();
//
//      for (Train train : trains) {
//        writer.write(String.format("%s,%s,%s,%s,%s",
//          train.getTrainId(),
//          train.getSource(),
//          train.getDestination(),
//          train.getDepartureDate().format(DATE_FORMAT),
//          train.getArrivalTime().format(DATE_FORMAT)));
//
//        writer.newLine();
//      }
//
//      System.out.println("Saved " + trains.size() + " trains to file");
//    } catch (IOException e) {
//      System.err.println("Error saving trains data: " + e.getMessage());
//    }
//
//    saveCoaches(trains); // Save coaches for all trains
//  }
//
//  private static void saveCoaches(List<Train> trains) {
//    try (BufferedWriter writer = new BufferedWriter(new FileWriter(COACHES_FILE));
//         BufferedWriter seatsWriter = new BufferedWriter(new FileWriter(SEATS_FILE))) {
//      writer.write("trainId,coachId,type,totalSeats");
//
//      writer.newLine();
//
//      seatsWriter.write("trainId,coachId,seatNumber,bookingPnr");
//
//      seatsWriter.newLine();
//
//      for (Train train : trains) {
//        for (Coach coach : train.getCoaches()) {
//          writer.write(String.format("%s,%s,%s,%d",
//            train.getTrainId(),
//            coach.getCoachId(),
//            coach.getType(),
//            coach.getSeatBookings().size()));
//
//          writer.newLine();
//
//
//          for (Map.Entry<String, Booking> seatEntry : coach.getSeatBookings().entrySet())  // Write seat data for this coach
//          {
//            String seatNumber = seatEntry.getKey();
//
//            Booking booking = seatEntry.getValue();
//
//            String pnr = (booking == null) ? "null" : booking.getPnr();
//
//            seatsWriter.write(String.format("%s,%s,%s,%s",
//              train.getTrainId(),
//              coach.getCoachId(),
//              seatNumber,
//              pnr));
//            seatsWriter.newLine();
//          }
//        }
//      }
//      System.out.println("Saved coaches and seats data to file");
//    } catch (IOException e) {
//      System.err.println("Error saving coaches data: " + e.getMessage());
//    }
//  }
//
//  public static void saveBookings(Map<String, Booking> bookings) {
////    createDirectoryIfNotExists();
//
//    try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKINGS_FILE))) {
//      writer.write("pnr,userId,trainId,coachId,seats,cancelled");
//
//      writer.newLine();
//
//      // Write booking data
//      for (Booking booking : bookings.values()) {
//        writer.write(String.format("%s,%s,%s,%s,%s,%b",
//          booking.getPnr(),
//          booking.getUserId(),
//          booking.getTrainId(),
//          booking.getCoachId(),
//          String.join("|", booking.getSeats()),
//          booking.isCancelled()));
//
//        writer.newLine();
//      }
//
//      System.out.println("Saved " + bookings.size() + " bookings to file");
//    } catch (IOException e) {
//      System.err.println("Error saving bookings data: " + e.getMessage());
//    }
//  }
//
//  // Load trains from text file
//  public static void loadTrains(List<Train> trains, Map<String, Train> trainMap) {
//    trains.clear();
//
//    trainMap.clear();
//
//    File trainsFile = new File(TRAINS_FILE);
//
//    if (!trainsFile.exists()) {
//      System.out.println("No trains data file found");
//
//      return;
//    }
//
//    try (BufferedReader reader = new BufferedReader(new FileReader(trainsFile))) {
//      String line;
//
//      reader.readLine();
//
//      while ((line = reader.readLine()) != null)
//      {
//        String[] parts = line.split(",");
//
//        if (parts.length == 5)
//        {
//          String trainId = parts[0];
//
//          String source = parts[1];
//
//          String destination = parts[2];
//
//          LocalDate departureDate = LocalDate.parse(parts[3], DATE_FORMAT);
//
//          LocalDate arrivalDate = LocalDate.parse(parts[4], DATE_FORMAT);
//
//          Train train = new Train(trainId, source, destination, departureDate, arrivalDate);
//
//          trains.add(train);
//
//          trainMap.put(trainId, train);
//        }
//      }
//      System.out.println("Loaded " + trains.size() + " trains from file");
//    }
//    catch (IOException e)
//    {
//      System.err.println("Error loading trains data: " + e.getMessage());
//    }
//
//    loadCoaches(trainMap);     // Load coaches for all trains
//  }
//
//  private static void loadCoaches(Map<String, Train> trainMap)
//  {
//    File coachesFile = new File(COACHES_FILE);
//
//    if (!coachesFile.exists())
//    {
//      System.out.println("No coaches data file found");
//
//      return;
//    }
//
//    try (BufferedReader reader = new BufferedReader(new FileReader(coachesFile)))
//    {
//      String line;
//
//      reader.readLine();
//      while ((line = reader.readLine()) != null)
//      {
//        String[] parts = line.split(",");
//
//        if (parts.length == 4)
//        {
//          String trainId = parts[0];
//
//          String coachId = parts[1];
//
//          String type = parts[2];
//
//          int totalSeats = Integer.parseInt(parts[3]);
//
//          Train train = trainMap.get(trainId);
//
//          if (train != null)
//          {
//            Coach coach = new Coach(type, coachId, totalSeats);
//
//            train.getCoaches().add(coach);
//          }
//        }
//      }
//
//      System.out.println("Loaded coaches data from file");
//    }
//    catch (IOException e)
//    {
//      System.err.println("Error loading coaches data: " + e.getMessage());
//    }
//  }
//
//  public static void loadBookings(Map<String, Booking> bookings, Map<String, Train> trainMap) // First load the bookings
//  {
//    bookings.clear();
//
//    File bookingsFile = new File(BOOKINGS_FILE);
//
//    if (!bookingsFile.exists())
//    {
//      System.out.println("No bookings data file found");
//
//      return;
//    }
//
//    try (BufferedReader reader = new BufferedReader(new FileReader(bookingsFile)))
//    {
//      String line;
//
//      reader.readLine();
//
//      while ((line = reader.readLine()) != null)
//      {
//        String[] parts = line.split(",");
//
//        if (parts.length == 6)
//        {
//          String pnr = parts[0];
//
//          String userId = parts[1];
//
//          String trainId = parts[2];
//
//          String coachId = parts[3];
//
//          List<String> seats = new ArrayList<>(Arrays.asList(parts[4].split("\\|")));
//
//          boolean cancelled = Boolean.parseBoolean(parts[5]);
//
//          Booking booking = new Booking(pnr, userId, trainId, coachId, seats);
//
//          if (cancelled)
//          {
//            booking.setCancelled(true);
//          }
//
//          bookings.put(pnr, booking);
//        }
//      }
//      System.out.println("Loaded " + bookings.size() + " bookings from file");
//    }
//    catch (IOException e)
//    {
//      System.err.println("Error loading bookings data: " + e.getMessage());
//    }
//
//    loadSeatAssignments(bookings, trainMap);     // Then load the seat assignments
//  }
//
//  private static void loadSeatAssignments(Map<String, Booking> bookings, Map<String, Train> trainMap)
//  {
//    File seatsFile = new File(SEATS_FILE);
//
//    if (!seatsFile.exists())
//    {
//      System.out.println("No seats data file found");
//
//      return;
//    }
//
//    try (BufferedReader reader = new BufferedReader(new FileReader(seatsFile)))
//    {
//      String line;
//
//      while ((line = reader.readLine()) != null)
//      {
//        String[] parts = line.split(",");
//
//        if (parts.length == 4)
//        {
//          String trainId = parts[0];
//
//          String coachId = parts[1];
//
//          String seatNumber = parts[2];
//
//          String pnr = parts[3];
//
//          Train train = trainMap.get(trainId);
//
//          if (train != null)
//          {
//            for (Coach coach : train.getCoaches())
//            {
//              if (coach.getCoachId().equals(coachId))
//              {
//                if ("null".equals(pnr))
//                {
//                  coach.getSeatBookings().put(seatNumber, null);
//                }
//                else
//                {
//                  Booking booking = bookings.get(pnr);
//
//                  if (booking != null)
//                  {
//                    coach.getSeatBookings().put(seatNumber, booking);
//                  }
//                }
//                break;
//              }
//            }
//          }
//        }
//      }
//      System.out.println("Loaded seat assignments from file");
//
//    }
//    catch (IOException e)
//    {
//      System.err.println("Error loading seat assignments: " + e.getMessage());
//    }
//  }
//
//  public static void saveAllData(List<Train> trains, Map<String, Booking> bookings)
//  {
//    saveTrains(trains);
//
//    saveBookings(bookings);
//  }
//
//  public static void loadAllData(List<Train> trains, Map<String, Train> trainMap, Map<String, Booking> bookings)
//  {
//    loadTrains(trains, trainMap);
//
//    loadBookings(bookings, trainMap);
//  }
//}
