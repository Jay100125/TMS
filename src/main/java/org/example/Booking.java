package org.example;

import java.util.ArrayList;
import java.util.List;

public class Booking {

  private String pnr, userId, trainId, coachId;

  private List<String> seats;

  private boolean cancelled;

  public Booking(String pnr, String userId, String trainId, String coachId, List<String> seats)
  {
    this.pnr = pnr;

    this.userId = userId;

    this.trainId = trainId;

    this.coachId = coachId;

    this.seats = new ArrayList<>(seats);

    this.cancelled = false;
  }

  public String getPnr()
  {
    return pnr;
  }

  public String getUserId()
  {
    return userId;
  }

  public String getTrainId()
  {
    return trainId;
  }

  public String getCoachId()
  {
    return coachId;
  }

  public List<String> getSeats()
  {
    return seats;
  }

  public boolean isCancelled()
  {
    return cancelled;
  }

  public void setCancelled(boolean cancelled)
  {
    this.cancelled = cancelled;
  }

}
