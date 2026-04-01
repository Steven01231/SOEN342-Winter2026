package org.example.controllers;

import calendar.CalendarGateway;
import catalogs.TaskCatalog;
import org.example.models.Task;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class ExportController {

    public void exportSingleTask(int taskID, TaskCatalog taskCat){
        List<Task> tasks = new ArrayList<>();

        for(Task task: taskCat.getTasks()){
            if (task.getTaskId() == taskID && task.getDueDate() != null){
                tasks.add(task);
            }
        }
        System.out.println(tasks);
        CalendarGateway calGat = new CalendarGateway();

        try (OutputStream outputStream = new FileOutputStream("my-tasks.ics")) {
            calGat.exportCalendar(tasks, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportTasksByProject(int projectID, TaskCatalog taskCat){
        List<Task> tasks = taskCat.getTasks().stream()
                .filter(t -> t.getDueDate() != null) // Keep this (Date is an object)
                // .filter(t -> t.getTaskId() != null) <-- REMOVE THIS LINE
                .filter(t -> t.getProjectId() == projectID) // Direct comparison is safe
                .toList();
        System.out.println(tasks);
        CalendarGateway calGat = new CalendarGateway();

        try (OutputStream outputStream = new FileOutputStream("my-tasks.ics")) {
            calGat.exportCalendar(tasks, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportTasksByThisWeek( TaskCatalog taskCat){
        LocalDate now = LocalDate.now();
        LocalDate weekEnd = now.plusDays(7);
        List<Task> tasks = taskCat.getTasks().stream()
                .filter(t -> t.getDueDate() != null)
                .filter(t -> {
                    // convert java.util.Date to LocalDate safely
                    LocalDate due = new java.sql.Date(t.getDueDate().getTime()).toLocalDate();
                    return !due.isBefore(now) && !due.isAfter(weekEnd);
                })
                .toList();
        System.out.println(tasks);
        CalendarGateway calGat = new CalendarGateway();

        try (OutputStream outputStream = new FileOutputStream("my-tasks.ics")) {
            calGat.exportCalendar(tasks, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
