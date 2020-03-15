package core;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Station implements Comparable<Station> {

    private final String name;
    private final String lineNumber;
    private boolean isClosed;
    private LocalDate closingDate;

    public Station(String name, String lineNumber) {
        this.name = name;
        this.lineNumber = lineNumber;
        this.isClosed = false;
        this.closingDate = LocalDate.now();
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public String getName() {
        return name;
    }

    public boolean getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(boolean closed) {
        isClosed = closed;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    @Override
    public int compareTo(Station station) {
        int lineComparison = lineNumber.compareTo(station.lineNumber);
        if(lineComparison != 0) {
            return lineComparison;
        }
        return name.compareToIgnoreCase(station.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return name.equals(station.name) && lineNumber.equals(station.lineNumber);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        return lineNumber + ":" + name + (isClosed ?
                " (закрыта с " + formatter.format(closingDate) + ")" : "");
    }
}