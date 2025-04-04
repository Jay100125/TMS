package org.project.server;

import org.project.schema.Train;
import org.project.database.TrainDatabase;

import java.net.ServerSocket;
import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server
{
  //  {TrainId -> train details}
//  private static final ConcurrentHashMap<String, Train> trainMap = new ConcurrentHashMap<>(); // TODO
//
////  pnr -> {userId, trainId, coachType, seats}
//  private static final ConcurrentHashMap<String, Map<String, String>> bookingRecord = new ConcurrentHashMap<>();

  private static final int PORT = 8080;

  public static void main(String[] args)
  {
    createSampleTrain();

    ExecutorService executorService = Executors.newFixedThreadPool(5);

    System.out.println("Server started on port " + PORT);

    try (ServerSocket serverSocket = new ServerSocket(PORT))
    {
      while (true)
      {
        var clientSocket = serverSocket.accept(); // TODO close in same method

        System.out.println("New client connected: " + clientSocket.getInetAddress());

        try
        {
          executorService.submit(new RequestHandler(clientSocket)); // TODO why pass every time
        }
        catch (Exception e)
        {
          if(!clientSocket.isClosed())
          {
            clientSocket.close();
          }
        }
      }
    }
    catch (Exception e) // TODO Accept generalized exception
    {
      System.err.println("Error accepting client connection: " + e.getMessage());
    }
    finally
    {
      executorService.shutdown();

      System.out.println("Shutting down server...");
      try
      {
        if (!executorService.awaitTermination(60, TimeUnit.SECONDS))
        {
          executorService.shutdownNow();
        }
      }
      catch (InterruptedException e)
      {
        executorService.shutdownNow();
      }
      System.out.println("Server shutdown complete");
    }
  }

  private static void createSampleTrain()
  {
    TrainDatabase db = TrainDatabase.INSTANCE;

    Train train = new Train("12345", "Ahmedabad", "Surat",
      LocalDate.of(2025, 10, 10), LocalDate.of(2025, 10, 11));
    train.addCoach("Sleeper", "C1", 5);
    train.addCoach("Sleeper", "C2", 5);
    train.addCoach("Sleeper", "C3", 5);
    train.addCoach("AC", "A1", 5);
    db.addTrain(train);

    Train train2 = new Train("54321", "SA", "SB",
      LocalDate.of(2025, 10, 10), LocalDate.of(2025, 10, 11));
    train2.addCoach("Sleeper", "C1", 3);
    train2.addCoach("Sleeper", "C2", 2);
    db.addTrain(train2);

    System.out.println("Created sample train data");
  }
}
