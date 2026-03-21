package org.example.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecurrencePattern {
    private int id;
    private String patternType;
    private int customInterval;
    private Date startDate;
    private Date endDate;
    private String selectedDays; // e.g., "Mon,Wed,Fri"
    private int taskId;

    // Constructors
    public RecurrencePattern(int id, String patternType, int customInterval,
                             Date startDate, Date endDate, String selectedDays, int taskId) {
        this.id = id;
        this.patternType = patternType;
        this.customInterval = customInterval;
        this.startDate = startDate;
        this.endDate = endDate;
        this.selectedDays = selectedDays;
        this.taskId = taskId;
    }

    public RecurrencePattern(String patternType, int customInterval,
                             Date startDate, Date endDate, String selectedDays, int taskId) {
        this.patternType = patternType;
        this.customInterval = customInterval;
        this.startDate = startDate;
        this.endDate = endDate;
        this.selectedDays = selectedDays;
        this.taskId = taskId;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getPatternType() {
        return patternType;
    }

    public int getCustomInterval() {
        return customInterval;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getSelectedDays() {
        return selectedDays;
    }

    public int getTaskId() {
        return taskId;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setPatternType(String patternType) {
        this.patternType = patternType;
    }

    public void setCustomInterval(int customInterval) {
        this.customInterval = customInterval;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setSelectedDays(String selectedDays) {
        this.selectedDays = selectedDays;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
    
    public List<Date> generateOccurrences() {
        List<Date> occurrences = new ArrayList<>();

        if (startDate == null || endDate == null || startDate.after(endDate)) {
            return occurrences;
        }

        Calendar cursor = Calendar.getInstance();
        cursor.setTime(startDate);
        clearTime(cursor);

        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        clearTime(end);

        switch (patternType.toUpperCase()) {
            case "DAILY":
                while (!cursor.after(end)) {
                    occurrences.add(cursor.getTime());
                    cursor.add(Calendar.DAY_OF_MONTH, 1);
                }
                break;

            case "WEEKLY":
                List<Integer> targetDays = parseSelectedDays(selectedDays);
                while (!cursor.after(end)) {
                    int dayOfWeek = cursor.get(Calendar.DAY_OF_WEEK);
                    if (targetDays.contains(dayOfWeek)) {
                        occurrences.add(cursor.getTime());
                    }
                    cursor.add(Calendar.DAY_OF_MONTH, 1);
                }
                break;

            case "MONTHLY":
                int dayOfMonth = cursor.get(Calendar.DAY_OF_MONTH);
                while (!cursor.after(end)) {
                    int maxDay = cursor.getActualMaximum(Calendar.DAY_OF_MONTH);
                    cursor.set(Calendar.DAY_OF_MONTH, Math.min(dayOfMonth, maxDay));
                    if (!cursor.after(end)) {
                        occurrences.add(cursor.getTime());
                    }
                    cursor.add(Calendar.MONTH, 1);
                }
                break;

            case "CUSTOM":
                int interval = customInterval > 0 ? customInterval : 1;
                while (!cursor.after(end)) {
                    occurrences.add(cursor.getTime());
                    cursor.add(Calendar.DAY_OF_MONTH, interval);
                }
                break;

            default:
                break;
        }

        return occurrences;
    }

    private void clearTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Parses a comma-separated string of weekday abbreviations into
     * Calendar.DAY_OF_WEEK constants.
     * Accepted formats: "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
     * or full names "Monday", "Tuesday", etc. (case-insensitive).
     */
    private List<Integer> parseSelectedDays(String selectedDays) {
        List<Integer> days = new ArrayList<>();
        if (selectedDays == null || selectedDays.trim().isEmpty()) {
            return days;
        }
        for (String token : selectedDays.split(",")) {
            switch (token.trim().toLowerCase()) {
                case "sun": case "sunday":    days.add(Calendar.SUNDAY);    break;
                case "mon": case "monday":    days.add(Calendar.MONDAY);    break;
                case "tue": case "tuesday":   days.add(Calendar.TUESDAY);   break;
                case "wed": case "wednesday": days.add(Calendar.WEDNESDAY); break;
                case "thu": case "thursday":  days.add(Calendar.THURSDAY);  break;
                case "fri": case "friday":    days.add(Calendar.FRIDAY);    break;
                case "sat": case "saturday":  days.add(Calendar.SATURDAY);  break;
            }
        }
        return days;
    }

    @Override
    public String toString() {
        return "RecurrencePattern{" +
                "id=" + id +
                ", patternType='" + patternType + '\'' +
                ", customInterval=" + customInterval +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", selectedDays='" + selectedDays + '\'' +
                ", taskId=" + taskId +
                '}';
    }
}
