package Talisca.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TaliscaEngine {

    private final StringProperty date = new SimpleStringProperty();
    private final StringProperty pekTime = new SimpleStringProperty();
    private final StringProperty sydTime = new SimpleStringProperty();
    private final StringProperty weekday = new SimpleStringProperty();
    private final StringProperty weekNo = new SimpleStringProperty();

    private final DateTimeFormatter dateFormat;
    private final DateTimeFormatter timeFormat;
    private final DateTimeFormatter weekdayFormat;

    private final List<Assignment> assignments = new ArrayList<>();

    public TaliscaEngine() throws InterruptedException, ParseException, IOException {
        dateFormat = DateTimeFormatter.ofPattern("yyyy - MM - dd");
        timeFormat = DateTimeFormatter.ofPattern("KK : mm : ss a", Locale.ENGLISH);
        weekdayFormat = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH);

        retrieveAsm();

        update();
    }

    public void update() throws IOException, InterruptedException {
        OffsetDateTime now = OffsetDateTime.now();
        date.set(dateFormat.format(now.atZoneSameInstant(ZoneId.of("Australia/Sydney"))));
        pekTime.set(timeFormat.format(now));
        weekday.set(weekdayFormat.format(now).toUpperCase());
        sydTime.set(timeFormat.format(now.atZoneSameInstant(ZoneId.of("Australia/Sydney"))));
        for (Assignment assignment : assignments) {
            assignment.update();
        }
        updateWeekNumber();
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

    public StringProperty getWeekNo() {
        return weekNo;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    private static HttpResponse<String> requestGET(URI uri) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(uri)
                .setHeader("user_agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36").build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void updateWeekNumber() throws IOException, InterruptedException {
        InputStream commencingDateConf = getClass().getClassLoader().getResourceAsStream("commencing_dates.conf");
        assert commencingDateConf != null;
        Scanner scanner = new Scanner(commencingDateConf);
        List<OffsetDateTime> dates = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String[] dateRaw = scanner.nextLine().strip().split("\\.");
            OffsetDateTime date = OffsetDateTime.of(Integer.parseInt(dateRaw[0]),Integer.parseInt(dateRaw[1]),Integer.parseInt(dateRaw[2]), 0, 0, 0,0, ZoneOffset.UTC);
            if (date.isBefore(OffsetDateTime.now())) {
                dates.add(date);
            }
        }
        var commencingDate = dates.get(0);
        for (OffsetDateTime date : dates) {
            if (date.isAfter(commencingDate)) {
                commencingDate = date;
            }
        }
        var weekNumberMap = getWeekNumberMap(commencingDate);
        String today = dateFormat.format(OffsetDateTime.now().atZoneSameInstant(ZoneId.of("Australia/Sydney")));
        for (OffsetDateTime date : weekNumberMap.keySet()) {
            // Get current year/month/day at sydney
            if (dateFormat.format(date.atZoneSameInstant(ZoneId.of("Australia/Sydney"))).equals(today)) {
                this.weekNo.set(weekNumberMap.get(date));
            }
        }
    }

    private Map<OffsetDateTime, String> getWeekNumberMap(OffsetDateTime commencingDate) {
        Map<OffsetDateTime, String> map = new HashMap<>();
        OffsetDateTime dateCursor = commencingDate;
        // week 1-6
        for (int week = 1; week < 7; week++) {
            for (int day = 0; day < 7; day++) {
                map.put(dateCursor, "" + week);
                dateCursor = dateCursor.plusDays(1);
            }
        }
        // mid term break
        for (int day = 0; day < 7; day++) {
            map.put(dateCursor, "M");
            dateCursor = dateCursor.plusDays(1);
        }
        // week 7-13
        for (int week = 7; week < 14; week++) {
            for (int day = 0; day < 7; day++) {
                map.put(dateCursor, "" + week);
                dateCursor = dateCursor.plusDays(1);
            }
        }
        // STUVAC
        for (int day = 0; day < 7; day++) {
            map.put(dateCursor, "S");
            dateCursor = dateCursor.plusDays(1);
        }
        // Vacation
        while (dateCursor.isBefore(OffsetDateTime.now())) {
            map.put(dateCursor, "V");
            dateCursor = dateCursor.plusDays(1);
        }
        return map;
    }

    public void retrieveAsm() throws IOException, InterruptedException, ParseException {
        JSONParser jsonParser = new JSONParser();
        String canvasBase = "https://canvas.sydney.edu.au/api/v1/";
        // TODO: Secure way to record token
        InputStream conf = new FileInputStream(new File("config"));
        Scanner scanner = new Scanner(conf);
        StringBuilder configBuilder = new StringBuilder();
        while (scanner.hasNextLine()) configBuilder.append(scanner.nextLine());

        JSONObject config = (JSONObject) jsonParser.parse(configBuilder.toString());

        String oauthToken = (String) config.get("oauthToken");

        // Get course list.
        URI courseListURI = URI.create(canvasBase + "courses?access_token=" + oauthToken);
        JSONArray courses = (JSONArray) jsonParser.parse(requestGET(courseListURI).body());

        // Get assignments of each not-yet-ended course.
        for (Object courseRaw : courses) {
            JSONObject course = (JSONObject) courseRaw;
            // Courses with assignments must be normal sem-based courses.
            if (course.get("end_at") != null) {
                // Courses ends after current time is counted.
                if (OffsetDateTime.parse((CharSequence) course.get("end_at")).isAfter(OffsetDateTime.now())) {
                    // Course UOS code is the first term of its name. Other courses are not UOS.
                    String courseName = (String) course.get("name");
                    if (courseName.split(" ").length > 0) {
                        if (!courseName.split(" ")[0].matches("^\\w{4}\\d{4}.*")) {
                            continue;
                        } else {
                            courseName = courseName.split(" ")[0];
                        }
                    }
                    // Valid courses reach this line.
                    String courseID = String.valueOf(course.get("id"));
                    // Get assignments
                    URI assignmentListURI = URI.create(canvasBase
                            + String.format("courses/%s/assignments?access_token=", courseID) + oauthToken);
                    JSONArray assignments = (JSONArray) jsonParser.parse(requestGET(assignmentListURI).body());

                    for (Object assignmentRaw : assignments) {
                        JSONObject assignment = (JSONObject) assignmentRaw;
                        // Validate assignment
                        if (assignment.get("name") != null && assignment.get("due_at") != null) {
                            String name = (String) assignment.get("name");
                            OffsetDateTime dueDate = OffsetDateTime.parse((CharSequence) assignment.get("due_at"));
                            OffsetDateTime unlockDate;
                            if (assignment.get("unlock_at") != null) {
                                unlockDate = OffsetDateTime.parse((CharSequence) assignment.get("unlock_at"));
                            } else {
                                unlockDate = OffsetDateTime.now();
                            }
                            // Build the assignment object
                            Assignment asm = new Assignment(courseName, name, dueDate, unlockDate);
                            this.assignments.add(asm);
                        }
                    }
                }
            }
        }
    }
}
