package org.project.database;

import org.project.schema.Train;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum TrainDatabase {
  INSTANCE;

  private final ConcurrentHashMap<String, Train> trains = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, Map<String, String>> bookingRecord = new ConcurrentHashMap<>();

  public ConcurrentHashMap<String, Train> getTrains() {
    return trains;
  }

  public ConcurrentHashMap<String, Map<String, String>> getBookingRecord() {
    return bookingRecord;
  }

  public void addTrain(Train train) {
    trains.put(train.getTrainId(), train);
  }

  public void reset() {
    trains.clear();
    bookingRecord.clear();
  }
}
