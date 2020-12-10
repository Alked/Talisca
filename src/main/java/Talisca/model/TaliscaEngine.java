package Talisca.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TaliscaEngine {

    private final StringProperty date = new SimpleStringProperty();
    private final StringProperty pekTime = new SimpleStringProperty();
    private final StringProperty sydTime = new SimpleStringProperty();
    private final StringProperty weekday = new SimpleStringProperty();

    private final DateTimeFormatter dateFormat;
    private final DateTimeFormatter timeFormat;
    private final DateTimeFormatter weekdayFormat;

    private final List<Assignment> assignments = new ArrayList<>();

    public TaliscaEngine() {
        dateFormat = DateTimeFormatter.ofPattern("yyyy - MM - dd");
        timeFormat = DateTimeFormatter.ofPattern("kk : mm : ss");
        weekdayFormat = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH);
        // TODO:TEST PURPOSE ONLY
        Assignment sample1 = new Assignment("COMP2022", "Proof logics 3", "2020-12-24T00:00:00+11:00", "2020-11-24T00:00:00+11:00");
        for (int i = 0; i < 2; i += 1) {
            assignments.add(sample1);
        }

        Assignment sample2 = new Assignment("COMP2017", "Computer Asm very long name", "2020-12-16T00:00:00+11:00", "2020-11-24T00:00:00+11:00");
        for (int i = 0; i < 2; i += 1) {
            assignments.add(sample2);
        }

        Assignment sample3 = new Assignment("INFO2222", "A very far away assignment", "2021-02-16T00:00:00+11:00", "2020-11-24T00:00:00+11:00");
        for (int i = 0; i < 2; i += 1) {
            assignments.add(sample3);
        }

        update();
    }

    public void update() {
        OffsetDateTime now = OffsetDateTime.now();
        date.set(dateFormat.format(now.atZoneSameInstant(ZoneId.of("Australia/Sydney"))));
        pekTime.set(timeFormat.format(now));
        weekday.set(weekdayFormat.format(now).toUpperCase());
        sydTime.set(timeFormat.format(now.atZoneSameInstant(ZoneId.of("Australia/Sydney"))));
        for (Assignment assignment : assignments) {
            assignment.update();
        }
    }

    public StringProperty getDate() {
        return date;
    }

    public StringProperty getPekTime() {
        return pekTime;
    }

    public StringProperty getSydTime() {
        return sydTime;
    }

    public StringProperty getWeekday() {
        return weekday;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }
}
