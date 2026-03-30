package org.example.controllers;

import calendar.CalendarGateway;
import catalogs.TaskCatalog;
import org.example.models.Task;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ExportController {

    public void exportSingleTask(int taskID, TaskCatalog taskCat){
        List<Task> tasks = new ArrayList<>();

        for(Task task: taskCat.getTasks()){
            if (task.getTaskId() == taskID){
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
}
