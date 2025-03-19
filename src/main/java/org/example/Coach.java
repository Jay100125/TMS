package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Coach
{
  private String type;

  private String coachId;

  private ConcurrentHashMap<String, Booking> seatBookings = new ConcurrentHashMap<>();

  private ConcurrentLinkedQueue<String> availableSeats;

   static final Booking UNBOOKED = new Booking("UNBOOKED", null, null, null, new ArrayList<>());

  public Coach (String type, String coachId, int totalSeats)
  {
    this.type = type;

    this.coachId = coachId;

    this.availableSeats = new ConcurrentLinkedQueue<>();

    if (this.seatBookings == null) {
      this.seatBookings = new ConcurrentHashMap<>();
    }
    System.out.println("Initializing Coach: " + coachId + ", seatBookings is " + (seatBookings == null ? "null" : "not null"));

    for (int i = 1; i <= totalSeats; i++)
    {
//      var seat = "S" + i;
      var seat = coachId + "-S" + i;
      availableSeats.add(seat);
      seatBookings.put(seat, UNBOOKED);
    }
  }

  public String getCoachId()
  {
    return coachId;
  }

  public String getType()
  {
    return type;
  }

  public ConcurrentHashMap<String, Booking> getSeatBookings() { return seatBookings; }
  public ConcurrentLinkedQueue<String> getAvailableSeats() { return availableSeats; }
  public int getAvailableSeatCount() {
//    System.out.println(availableSeats.size());
    return availableSeats.size(); }

  public boolean tryBookSeats(List<String> seats, Booking booking) {
    var booked = new ArrayList<String>();
    for (var seat : seats) {
      if (seatBookings.replace(seat, UNBOOKED, booking)) {
        booked.add(seat);
        availableSeats.remove(seat);
      } else {
        // Rollback if any seat fails
        booked.forEach(s -> seatBookings.put(s, UNBOOKED));
        booked.forEach(availableSeats::add);
        return false;
      }
    }
    return true;
  }

  // Release seats atomically
  public void releaseSeats(List<String> seats) {
    seats.forEach(seat -> {
      seatBookings.put(seat, UNBOOKED);
      availableSeats.add(seat);
    });
  }

}
