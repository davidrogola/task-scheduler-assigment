package com.watucredit.schedulerservice;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TaskHandler {
  final static Logger logger = LoggerFactory.getLogger(TaskHandler.class);

  @Scheduled(fixedRateString = "${scheduler.fixedRate}")
  public void runTask() {
    TimeZone zone = TimeZone.getTimeZone(ZoneId.of("Africa/Lagos"));
    ZonedDateTime date = new Date().toInstant().atZone(zone.toZoneId());
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm");
    int dayOfWeek = date.getDayOfWeek().getValue();
    String time = date.format(dateFormat);

    List<TaskDetail> details = loadTasks("tasks.csv");
    List<TaskDetail> matchedTasks = details.stream()
        .filter(t-> t.bitmask == dayOfWeek && t.time.equals(time))
        .collect(Collectors.toList());

    logger.info(String.format("The time in Nigeria is %s and the Matched tasks size is %d",
        time, matchedTasks.size()));
  }

  public List<TaskDetail> loadTasks(String fileName) {
    List<TaskDetail> tasks = new ArrayList<>();
    try {
      InputStream stream = getClass().getClassLoader().getResourceAsStream(fileName);
      BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));
      String record;
      while ((record = buffer.readLine()) != null) {
        String [] taskArr = record.split(",");
        tasks.add(new TaskDetail(taskArr[0], Integer.parseInt(taskArr[1])));
      }
      buffer.close();
    } catch (Exception e) {
      logger.error("An error occurred while loading tasks", e);
    }

    return tasks;
  }

  static class TaskDetail {

    private final String time;
    private final int bitmask;

    public TaskDetail(String time, int bitmask) {
      this.time = time;
      this.bitmask = bitmask;
    }

    public String getTime() {
      return time;
    }

    public int getBitmask() {
      return bitmask;
    }
  }
}
