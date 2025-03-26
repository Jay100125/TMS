package org.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Coach
{
  private final String type;

  private final String coachId;

  private final ConcurrentHashMap<String, String> seatBookings = new ConcurrentHashMap<>();  // If i want to add another funcitonality from admin side

  private final ConcurrentLinkedQueue<String> availableSeats;

  private final AtomicInteger availableSeatCount;

  public Coach (String type, String coachId, int totalSeats)
  {
    this.type = type;

    this.coachId = coachId;

    this.availableSeatCount = new AtomicInteger(totalSeats);

    this.availableSeats = new ConcurrentLinkedQueue<>();


    for (int i = 1; i <= totalSeats; i++)
    {
      var seat = coachId + "-S" + i;

      availableSeats.add(seat);

      seatBookings.put(seat, "UNBOOKED");
    }
  }

  public String getCoachId() { return coachId; }
  public String getType() { return type; }
  public int getAvailableSeatCount()
  {
    return availableSeatCount.get();
  }

  public String pollAndBookSeat(String pnr)
  {
    String seat = availableSeats.poll();
    if (seat != null)
    {
      seatBookings.replace(seat, "UNBOOKED", pnr);
      availableSeatCount.decrementAndGet();
    }
    return seat;
  }

  public void releaseSeats(List<String> seats)
  {
    for (String seat : seats)
    {
      String pnr = seatBookings.get(seat);
      seatBookings.replace(seat, pnr, "UNBOOKED");

      if (pnr != null)
      {
        availableSeats.add(seat);

        availableSeatCount.incrementAndGet();
      }
    }
  }

  public ConcurrentHashMap<String, String> getSeatBookings()
  {
    return seatBookings;
  }
}
