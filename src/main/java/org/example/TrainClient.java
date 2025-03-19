package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
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
          System.out.println("Source : ");
          String source = scanner.nextLine();
          System.out.println("Destination : ");
          String destination = scanner.nextLine();
          System.out.println("Date (YYYY-MM-DD) : ");
          String date = scanner.nextLine();
          command = "SEARCH " + source + " " + destination + " " + date + "\n";
          break;

        case "2":
          System.out.println("User id");
          String id = scanner.nextLine();
          System.out.println("Train id");
          String trainId = scanner.nextLine();
          System.out.println("Coach Id");
          String couchType = scanner.nextLine();
          System.out.println("Number of Seats");
          int numberOfSeats = scanner.nextInt();
          scanner.nextLine();
          command = "BOOK " + id + " " + trainId + " " + couchType + " " + numberOfSeats;
          break;

        case "3":
          System.out.println("PNR number");
          String pnrNumber = scanner.nextLine();
          command = "CANCEL " + pnrNumber + "\n";
          break;

        case "4":
          System.exit(0);
          break;

        default:
          System.out.println("Invalid command");

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
