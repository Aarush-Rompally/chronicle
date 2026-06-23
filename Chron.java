import java.time.ZonedDateTime;

public class Chron<E> {
    E entry; // EHR content
    String whenEntry; // String represenation of zone + date + time info for when entry was input
    String author; // ID corresponding to author of entry (physician, patient)

    // solo constructor
    public Chron(E entry, String author) {
        ZonedDateTime zdt = ZonedDateTime.now();
        // pull zone + date + time info from ZonedDateTime object
        String zone = String.format("ZONE:%s", zdt.getZone());
        String year = String.format("YEAR:%s", zdt.getYear());
        String month = String.format("MONTH:%s", zdt.getMonthValue());
        String day = String.format("DAY:%s", zdt.getDayOfMonth());
        String hour = String.format("HOUR:%s", zdt.getHour());
        String minute = String.format("MINUTE:%s", zdt.getMinute());
        String second = String.format("SECOND:%s", zdt.getSecond());
        String nano = String.format("NANO:%s", zdt.getNano());
        // turn into String representation
        String[] temp = {zone, year, month, day, hour, minute, second, nano};
        this.whenEntry = "";
        for(String item : temp) {
            this.whenEntry += item;
            this.whenEntry += " ";
        }
        this.whenEntry = this.whenEntry.substring(0, this.whenEntry.length() - 1);
        // other attributes
        this.entry = entry;
        this.author = author;
    }

    // getters
    public E getEntry() {return this.entry;}
    public String getWhenEntry() {return this.whenEntry;}
    public String getAuthor() {return this.author;}
}