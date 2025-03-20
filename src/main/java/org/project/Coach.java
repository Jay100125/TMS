package org.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Coach
{
  private final String type;

  private final String coachId;

  private final ConcurrentHashMap<String, String> seatBookings = new ConcurrentHashMap<>();

  private final AtomicInteger availableSeatCount;

  public Coach (String type, String coachId, int totalSeats)
  {
    this.type = type;

    this.coachId = coachId;

    this.availableSeatCount = new AtomicInteger(totalSeats);

    for (int i = 1; i <= totalSeats; i++)
    {
      var seat = coachId + "-S" + i;

      seatBookings.put(seat, "UNBOOKED");
    }
  }

  public String getCoachId() { return coachId; }
  public String getType() { return type; }
  public int getAvailableSeatCount()
  {
    return availableSeatCount.get();
  }

  public List<String> getAvailableSeats()
  {
    return seatBookings.entrySet().stream()
      .filter(e -> "UNBOOKED".equals(e.getValue()))
      .map(Map.Entry::getKey)
      .collect(Collectors.toList());
  }

  public boolean tryBookSeats(List<String> seats, String pnr)
  {
    List<String> booked = new ArrayList<>();
    for (String seat : seats)
    {
      if (seatBookings.replace(seat, "UNBOOKED", pnr))
      {
        booked.add(seat);

        availableSeatCount.decrementAndGet();
      }
      else
      {
        for (String bookedSeat : booked)
        {
          seatBookings.put(bookedSeat, "UNBOOKED");

          availableSeatCount.incrementAndGet();
        }
        return false;
      }
    }
    return true;
  }

  public void releaseSeats(List<String> seats)
  {
    for (String seat : seats)
    {
      if (seatBookings.replace(seat, seatBookings.get(seat), "UNBOOKED"))
      {
        availableSeatCount.incrementAndGet();
      }
    }
  }

  public ConcurrentHashMap<String, String> getSeatBookings()
  {
    return seatBookings;
  }
}
