package org.project.schema;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Train
{
  private final String trainId;

  private final String source;

  private final String destination;

  private final HashMap<String, List<Coach>> coaches;

//  private final ConcurrentHashMap<String, AtomicInteger> coachTypeAvailability = new ConcurrentHashMap<>();

  private final LocalDate departureDate;

  private final LocalDate arrivalDate;

  public Train(String trainId, String source, String destination, LocalDate departureDate, LocalDate arrivalDate)
  {
    this.trainId = trainId;

    this.source = source;

    this.destination = destination;

    this.departureDate = departureDate;

    this.arrivalDate = arrivalDate;

    this.coaches = new HashMap<>();
  }

  public void addCoach(String type, String coachId, int totalSeats) {
    var coach = new Coach(type, coachId, totalSeats);
    coaches.computeIfAbsent(type.toLowerCase(), k -> new ArrayList<>()).add(coach);
//    coachTypeAvailability.compute(type.toLowerCase(), (k, v) ->
//      (v == null) ? new AtomicInteger(totalSeats) : new AtomicInteger(v.addAndGet(totalSeats))
//    );
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

  public LocalDate getArrivalDate()
  {
    return arrivalDate;
  }

//  public AtomicInteger getCoachTypeAvailability(String type) {
//    return coachTypeAvailability.getOrDefault(type.toLowerCase(), new AtomicInteger(0));
//  }

  @Override
  public String toString() {
    return "Train{" +
      "trainId='" + trainId + '\'' +
      ", source='" + source + '\'' +
      ", destination='" + destination + '\'' +
      ", departureTime=" + departureDate +
      ", arrivalTime=" + arrivalDate +
      '}';
  }
}
