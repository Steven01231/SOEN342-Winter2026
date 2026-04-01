package org.example;

import calendar.CalendarGateway;
import catalogs.TaskCatalog;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.FluentCalendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.model.property.Duration;
import org.example.models.PriorityLevel;
import org.example.models.StatusType;
import org.example.models.Task;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import java.time.Instant;

public class ICalendarExportExample {

    public static void main(String[] args) {

        tryMethod();

        Calendar calendar = new Calendar();
        calendar.add(new Version());
        calendar.add(new CalScale("GREGORIAN"));
        calendar.add(new ProdId("-//My Organization//My Product//EN"));

// 2. Create an Event
        LocalDateTime start = LocalDateTime.of(2026, 4, 1, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 4, 1, 10, 0);
        VEvent meeting = new VEvent(start, end, "Project Sync");

// 3. Add necessary properties
        meeting.add(new Uid("unique-id-12345"));
        calendar.add(meeting);

// 4. Output the result
        System.out.println(calendar.toString());

        try (OutputStream fout = new FileOutputStream("my-calendar.ics")) {
            CalendarOutputter outputter = new CalendarOutputter();

            // Validate before writing to ensure RFC 5545 compliance
            calendar.validate();

            // Write the calendar to the file
            outputter.output(calendar, fout);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void tryMethod() {

        List<Task> tasks = new ArrayList<>();

        tasks.add(new Task(
                "Database Migration",
                "Migrate the production schema to the new RDS instance.",
                new Date(),                       // creationDate
                PriorityLevel.HIGH,               // priorityLevel
                StatusType.IN_PROGRESS,           // status
                Date.from(Instant.now().plusSeconds(2 * 86400)),                 // dueDate (2 days from now)
                501                               // projectId
        ));
        CalendarGateway calGat = new CalendarGateway();

        try (OutputStream outputStream = new FileOutputStream("my-tasks.ics")) {
            calGat.exportCalendar(tasks, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}