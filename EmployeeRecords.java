import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class EmployeeRecord {
    private String positionId;
    private String positionStatus;
    private Date timeIn;
    private Date timeOut;
    private double timecardHours;
    private Date payCycleStartDate;
    private Date payCycleEndDate;
    private String employeeName;
    private String fileNumber;

    public EmployeeRecord(String positionId, String positionStatus, Date timeIn, Date timeOut, double timecardHours, Date payCycleStartDate, Date payCycleEndDate, String employeeName, String fileNumber) {
        this.positionId = positionId;
        this.positionStatus = positionStatus;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.timecardHours = timecardHours;
        this.payCycleStartDate = payCycleStartDate;
        this.payCycleEndDate = payCycleEndDate;
        this.employeeName = employeeName;
        this.fileNumber = fileNumber;
    }

    public String getPositionId() {
        return positionId;
    }

    public String getPositionStatus() {
        return positionStatus;
    }

    public Date getTimeIn() {
        return timeIn;
    }

    public Date getTimeOut() {
        return timeOut;
    }

    public double getTimecardHours() {
        return timecardHours;
    }

    public Date getPayCycleStartDate() {
        return payCycleStartDate;
    }

    public Date getPayCycleEndDate() {
        return payCycleEndDate;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getFileNumber() {
        return fileNumber;
    }
}



public class EmployeeRecords {
    public static void main(String[] args) {
        String fileName = "Assignment_Timecard.xlsx"; // Input File

        try {
            List<EmployeeRecord> records = readEmployeeRecords(fileName);



            for (int i = 0; i < records.size(); i++) {
                EmployeeRecord currentRecord = records.get(i);

                // Check for employees who have worked for 7 consecutive days (assuming pay cycle end date is used)
                if (i + 6 < records.size()) {
                    boolean hasWorkedFor7ConsecutiveDays = true;
                    for (int j = i; j < i + 7; j++) {
                        long timeDifference = records.get(j + 1).getPayCycleStartDate().getTime() -
                                records.get(j).getPayCycleEndDate().getTime();
                        if (timeDifference > 24 * 60 * 60 * 1000) {
                            hasWorkedFor7ConsecutiveDays = false;
                            break;
                        }
                    }
                    if (hasWorkedFor7ConsecutiveDays) {
                        System.out.println("Employee: " + currentRecord.getEmployeeName() +
                                ", Position: " + currentRecord.getPositionId() +
                                " has worked for 7 consecutive days.");
                    }
                }

                // Check for employees with less than 10 hours between shifts but greater than 1 hour
                if (i + 1 < records.size()) {
                    long timeDifference = records.get(i + 1).getTimeIn().getTime() -
                            currentRecord.getTimeOut().getTime();
                    if (timeDifference > 60 * 60 * 1000 && timeDifference < 10 * 60 * 60 * 1000) {
                        System.out.println("Employee: " + currentRecord.getEmployeeName() +
                                ", Position: " + currentRecord.getPositionId() +
                                " has less than 10 hours between shifts but greater than 1 hour.");
                    }
                }

                // Check for employees who have worked for more than 14 hours in a single shift
                if (currentRecord.getTimecardHours() > 14.0) {
                    System.out.println("Employee: " + currentRecord.getEmployeeName() +
                            ", Position: " + currentRecord.getPositionId() +
                            " has worked for more than 14 hours in a single shift.");
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static List<EmployeeRecord> readEmployeeRecords(String fileName) throws IOException, ParseException {
        List<EmployeeRecord> records = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        SimpleDateFormat sdfDateTime = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy");

        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 9) {
                String positionId = parts[0];
                String positionStatus = parts[1];
                String timeInString = parts[2].trim();
                String timeOutString = parts[3].trim();

                // Validate "Time In" and "Time Out" fields before parsing
                if (isValidDateTime(timeInString) && isValidDateTime(timeOutString)) {
                    double timecardHours;
                    try {
                        timecardHours = Double.parseDouble(parts[4]);
                    } catch (NumberFormatException e) {
                        timecardHours = 0.0;
                    }
                    String payCycleStartDateString = parts[5].trim();
                    String payCycleEndDateString = parts[6].trim();
                    String employeeName = parts[7];
                    String fileNumber = parts[8];

                    Date timeIn = sdfDateTime.parse(timeInString);
                    Date timeOut = sdfDateTime.parse(timeOutString);
                    Date payCycleStartDate = sdfDate.parse(payCycleStartDateString);
                    Date payCycleEndDate = sdfDate.parse(payCycleEndDateString);

                    records.add(new EmployeeRecord(positionId, positionStatus, timeIn, timeOut, timecardHours, payCycleStartDate, payCycleEndDate, employeeName, fileNumber));
                } else {
                    // Handle invalid date/time values
                    System.err.println("Invalid date/time format in input file.");
                }
            }
        }

        reader.close();
        return records;
    }

    private static boolean isValidDateTime(String dateTime) {
        SimpleDateFormat sdfDateTime = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        try {
            Date date = sdfDateTime.parse(dateTime);
            return date != null;
        } catch (ParseException e) {
            return false;
        }
    }
}

