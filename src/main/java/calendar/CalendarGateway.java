package calendar;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.validate.ValidationException;
import org.example.models.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class CalendarGateway implements ICalendarGateway {
    @Override
    public void exportCalendar(List<Task> tasks, OutputStream outputStream) throws IOException {
        Calendar calendar = new Calendar();
        calendar.add(new ProdId("-//My Organization//My Product//EN"));
        calendar.add(new Version());
        calendar.add(new CalScale("GREGORIAN"));

        // 2. Map your domain 'Task' objects to iCal4j 'VToDo' components
        for (Task task : tasks) {

            LocalDateTime start = (task.getCreationDate() instanceof java.sql.Date)
                    ? ((java.sql.Date) task.getCreationDate()).toLocalDate().atStartOfDay()
                    : task.getCreationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

// Convert Due Date (End)
            LocalDateTime end = (task.getDueDate() instanceof java.sql.Date)
                    ? ((java.sql.Date) task.getDueDate()).toLocalDate().atTime(23, 59, 59)
                    : task.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            String combinedTitle = task.getTitle();
            VEvent event = new VEvent(start, end, combinedTitle);

            // 3. Mandatory Unique ID and Timestamp
            event.add(new Uid(String.valueOf(task.getTaskId())));
            event.add(new DtStamp(Instant.now()));

            // 4. Description
            if (task.getDescription() != null) {
                event.add(new Description(task.getDescription()));
            }

            // 5. Status Mapping
            // Standard values: NEEDS-ACTION, IN-PROCESS, COMPLETED, CANCELLED
            if (task.getStatus() != null) {
                event.add(new Status(task.getStatus().toString().toUpperCase()));
            }

            // 6. Priority Mapping (1=High, 5=Medium, 9=Low)
            if (task.getPriorityLevel() != null) {
                int pVal = switch (task.getPriorityLevel()) {
                    case HIGH -> 1;
                    case MEDIUM -> 5;
                    case LOW -> 9;
                    default -> 0;
                };
                event.add(new Priority(pVal));
            }

            // 7. Project Name as a Category (Tagging)
            /*if (task.getProjectName() != null) {
                event.add(new Categories(task.getProjectName()));
            }*/


            calendar.add(event);
        }

        // 3. Finalize and Output
        try {
            // Validate the internal structure against RFC 5545
            calendar.validate();

            // CalendarOutputter handles line-folding and UTF-8 encoding
            CalendarOutputter outputter = new CalendarOutputter();
            outputter.output(calendar, outputStream);

        } catch (ValidationException e) {
            // It's better to wrap this in an IOException for the interface contract
            throw new IOException("Failed to generate valid iCalendar data", e);
        }


    }
}
