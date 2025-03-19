package org.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Train
{
   private final String trainId;

    private final String source;

    private String destination;

    private HashMap<String, List<Coach>> coaches = new HashMap<>();

    private LocalDate departureDate;

    private LocalDate arrivalTime;

    public Train(String trainId, String source, String destination, LocalDate departureDate, LocalDate arrivalTime)
    {
      this.trainId = trainId;

      this.source = source;

      this.destination = destination;

      this.departureDate = departureDate;

      this.arrivalTime = arrivalTime;

      this.coaches = new HashMap<>();
    }

  public void addCoach(String type, String coachId, int totalSeats) {
    var coach = new Coach(type, coachId, totalSeats);
    coaches.computeIfAbsent(type, k -> new ArrayList<>()).add(coach);
  }

    public String getTrainId()
  {
    return trainId;
  }

    public String getSource()
  {
    return source;
  }

    public String getDestination()
  {
    return destination;
  }

    public Map<String, List<Coach>> getCoachTypes() { return coaches; }

    public LocalDate getDepartureDate()
  {
    return departureDate;
  }

    public LocalDate getArrivalTime()
  {
    return arrivalTime;
  }

  @Override
  public String toString() {
    return "org.example.Train{" +
      "trainId='" + trainId + '\'' +
      ", source='" + source + '\'' +
      ", destination='" + destination + '\'' +
      ", departureTime=" + departureDate +
      ", arrivalTime=" + arrivalTime +
      '}';
  }
}
