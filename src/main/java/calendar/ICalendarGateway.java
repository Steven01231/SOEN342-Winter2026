package calendar;

import net.fortuna.ical4j.model.Calendar;
import org.example.models.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface ICalendarGateway {

    void exportCalendar(List<Task> tasks, OutputStream outputStream) throws IOException;
}
