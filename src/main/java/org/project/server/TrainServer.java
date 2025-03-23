package org.project.server;

import org.project.Train;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TrainServer
{
  private static final ConcurrentHashMap<String, Train> trainMap = new ConcurrentHashMap<>();

  private static final ConcurrentHashMap<String, Map<String, String>> bookings = new ConcurrentHashMap<>();

  private static final int PORT = 8080;

  public static void main(String[] args) throws IOException
  {
    createSampleTrain();

    ExecutorService executorService = Executors.newFixedThreadPool(5);

    System.out.println("Server started on port " + PORT);

    try( ServerSocket serverSocket = new ServerSocket(PORT);)
    {
      while(true)
      {
        Socket clientSocket = serverSocket.accept();

        System.out.println("New client connected: " + clientSocket.getInetAddress());

        executorService.submit(new RequestHandler(clientSocket, trainMap, bookings));
      }
    }
    catch (IOException e)
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
    Train train = new Train("12345", "sa", "SB",
      LocalDate.of(2025, 10, 10), LocalDate.of(2025, 10, 11));

    train.addCoach("Sleeper", "C1", 5);
    train.addCoach("Sleeper", "C2", 5);
    train.addCoach("Sleeper", "C3", 5);
    train.addCoach("AC", "A1", 5);

    trainMap.put(train.getTrainId(), train);


    Train train2 = new Train("54321", "SA", "SB",
      LocalDate.of(2025, 10, 10), LocalDate.of(2025, 10, 11));

    train2.addCoach("Sleeper", "C1", 3);
    train2.addCoach("Sleeper", "C2", 2);

    trainMap.put(train2.getTrainId(), train2);

    System.out.println("Created sample train data");
  }
}
