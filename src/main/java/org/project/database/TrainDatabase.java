package org.project.database;

import org.project.schema.Train;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum TrainDatabase {
  INSTANCE;

  private final ConcurrentHashMap<String, Train> trainMap = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, Map<String, String>> bookingRecord = new ConcurrentHashMap<>();

  public ConcurrentHashMap<String, Train> getTrainMap() {
    return trainMap;
  }

  public ConcurrentHashMap<String, Map<String, String>> getBookingRecord() {
    return bookingRecord;
  }

  public void addTrain(Train train) {
    trainMap.put(train.getTrainId(), train);
  }

  public void reset() {
    trainMap.clear();
    bookingRecord.clear();
  }
}
