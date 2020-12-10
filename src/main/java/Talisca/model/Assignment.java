package Talisca.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.ChronoUnit.DAYS;

public class Assignment {

    private final StringProperty unit;
    private final StringProperty name;
    private final StringProperty dueDateProperty;
    private final StringProperty dueInProperty;
    private final OffsetDateTime dueDate;
    private final OffsetDateTime unlockDate;

    public Assignment(String unit, String name, String dueDate, String unlockDate) {
        this.unit = new SimpleStringProperty(unit);
        this.name = new SimpleStringProperty(name);
        this.dueDate = OffsetDateTime.parse(dueDate);
        this.unlockDate = OffsetDateTime.parse(unlockDate);
        dueInProperty = new SimpleStringProperty();
        update();
        dueDateProperty = new SimpleStringProperty("Due at " + (DateTimeFormatter.ofPattern("yyyy-MM-dd")).format(this.dueDate));
    }

    public boolean isAvailable() {
        return unlockDate.isAfter(OffsetDateTime.now());
    }

    public StringProperty getUnit() {
        return unit;
    }

    public StringProperty getName() {
        return name;
    }

    public StringProperty getDueDateProperty() {
        return dueDateProperty;
    }

    public StringProperty getDueInProperty() {
        return dueInProperty;
    }

    public void update() {
        OffsetDateTime now = OffsetDateTime.now();
        now.atZoneSameInstant(ZoneId.of("Australia/Sydney"));
        long days = now.until(dueDate, DAYS);
        if (days > 7 && days < 30) {
            dueInProperty.set(String.format("Due In %.0f weeks", ((double) days) / 7));
        } else if (days > 30) {
            dueInProperty.set(String.format("Due In %.0f months", ((double) days) / 30));
        } else {
            dueInProperty.set(String.format("Due In %d days", days));
        }
    }
}
