package org.project;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TrainClient {
  private static final int PORT = 8080;
  private static final String HOST = "127.0.0.1";

  public static void main(String[] args)
  {
    Scanner scanner = new Scanner(System.in);

    while (true)
    {
      System.out.println("\nOptions: \n1. Search Trains\n2. Book Seats\n3. Cancel Booking\n4. Exit");

      String choice = scanner.nextLine();

      Map<String, String> commandMap = new HashMap<>();

      switch (choice)
      {
        case "1":
          System.out.println("Source: ");
          String source = scanner.nextLine();
          if (source.trim().isEmpty()) System.out.println("Source cannot be empty");

          System.out.println("Destination: ");
          String destination = scanner.nextLine();
          if (destination.trim().isEmpty()) System.out.println("Destination cannot be empty");

          System.out.println("Date (YYYY-MM-DD): ");
          String date = scanner.nextLine();

          commandMap.put("command", "SEARCH");
          commandMap.put("source", source);
          commandMap.put("destination", destination);
          commandMap.put("date", date);
          break;

        case "2":
          System.out.println("User id: ");
          String id = scanner.nextLine();
          if (id.trim().isEmpty()) System.out.println("User ID cannot be empty");

          System.out.println("Train id: ");
          String trainId = scanner.nextLine();
          if (trainId.trim().isEmpty()) System.out.println("Train ID cannot be empty");

          System.out.println("Coach type: ");
          String coachType = scanner.nextLine();
          if (coachType.trim().isEmpty()) System.out.println("Coach type cannot be empty");

          System.out.println("Number of Seats: ");
          String numberOfSeats = scanner.nextLine();
          try
          {
            if (Integer.parseInt(numberOfSeats) <= 0)
              System.out.println("Number of seats must be positive");
          }
          catch (NumberFormatException e)
          {
            System.out.println("Number of seats must be integer");

            continue;
          }

          commandMap.put("command", "BOOK");
          commandMap.put("userId", id);
          commandMap.put("trainId", trainId);
          commandMap.put("coachType", coachType);
          commandMap.put("numberOfSeats", numberOfSeats);
          break;

        case "3":
          System.out.println("User id: ");
          String userId = scanner.nextLine();
          System.out.println("PNR number: ");
          String pnrNumber = scanner.nextLine();
          if (pnrNumber.trim().isEmpty()) System.out.println("PNR cannot be empty");

          commandMap.put("command", "CANCEL");
          commandMap.put("userId", userId);
          commandMap.put("pnr", pnrNumber);
          break;

        case "4":
          System.exit(0);
          break;

        default:
          System.out.println("Invalid choice");
          continue;
      }

      try (Socket socket = new Socket(HOST, PORT);
           ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
           ObjectInputStream in = new ObjectInputStream(socket.getInputStream()))
      {
        // Send HashMap
        out.writeObject(commandMap);

        out.flush();

        // Receive response HashMap
        Map<String, String> response = (Map<String, String>) in.readObject();

        if (response == null)
        {
          System.out.println("Error: No response received from server");
        }
        else
        {
          System.out.println(response.get("message"));
        }
      }
      catch (IOException | ClassNotFoundException e)
      {
        System.out.println("Connection error: " + e.getMessage());
      }
    }
  }
}
