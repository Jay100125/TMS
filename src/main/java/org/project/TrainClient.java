package org.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TrainClient
{
  public static void main(String[] args)
  {
    Scanner scanner = new Scanner(System.in);

    while(true)
    {
      System.out.println("\nOptions: \n1. Search Trains\n2. Book Seats\n3. Cancel Booking\n4. Exit");

      String choice = scanner.nextLine();

      String command = null;

      switch(choice)
      {
        case "1":
          System.out.println("Source: ");
          var source = scanner.nextLine();
          if (source.trim().isEmpty()) System.out.println("Source cannot be empty");

          System.out.println("Destination: ");
          var destination = scanner.nextLine();
          if (destination.trim().isEmpty()) System.out.println("Destination cannot be empty");

          System.out.println("Date (YYYY-MM-DD): ");
          var date = scanner.nextLine();
          command = "SEARCH " + source + " " + destination + " " + date;
          break;

        case "2":
          System.out.println("User id: ");
          var id = scanner.nextLine();
          if (id.trim().isEmpty()) System.out.println("User ID cannot be empty");

          System.out.println("Train id: ");
          var trainId = scanner.nextLine();
          if (trainId.trim().isEmpty()) System.out.println("Train ID cannot be empty");

          System.out.println("Coach type: ");
          var coachType = scanner.nextLine();
          if (coachType.trim().isEmpty()) System.out.println("Coach type cannot be empty");

          System.out.println("Number of Seats: ");
          try
          {
            var numberOfSeats = Integer.parseInt(scanner.nextLine());

            if (numberOfSeats <= 0) System.out.println("Number of seats must be positive");

            command = "BOOK " + id + " " + trainId + " " + coachType + " " + numberOfSeats;
          }
          catch (NumberFormatException e)
          {
            System.out.println("Number of seats must be integer");
          }
          break;

        case "3":
          System.out.println("PNR number: ");
          var pnrNumber = scanner.nextLine();
          if (pnrNumber.trim().isEmpty()) System.out.println("PNR cannot be empty");

          command = "CANCEL " + pnrNumber;
          break;

        case "4":
          System.exit(0);
          break;

        default:
          System.out.println("Invalid choice");
          continue;
      }
      try(Socket socket = new Socket("127.0.0.1", 8080);
          PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
          BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      )
      {
        out.println(command);

        String response;

        while ((response = in.readLine()) != null)
        {
          System.out.println(response);
        }
      }
      catch (NumberFormatException e)
      {
        System.out.println("Invalid number format: " + e.getMessage());
      }
      catch (IllegalArgumentException e)
      {
        System.out.println("Input error: " + e.getMessage());
      }
      catch (IOException e)
      {
        System.out.println("Connection error: " + e.getMessage());
      }
      finally
      {
        System.out.println("Closing connection");
      }
    }
  }
}
