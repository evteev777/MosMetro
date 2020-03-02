package core;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Station implements Comparable<Station> {

    private final String name;
    private final Line line;
    private boolean isClosed;
    private LocalDate closingDate;

    public Station(String name, Line line) {
        this.name = name;
        this.line = line;
        this.isClosed = false;
        this.closingDate =LocalDate.now();
    }

    public Line getLine() {
        return line;
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
        int lineComparison = line.compareTo(station.getLine());
        if(lineComparison != 0) {
            return lineComparison;
        }
        return name.compareToIgnoreCase(station.getName());
    }

    @Override
    public boolean equals(Object obj) {
        return compareTo((Station) obj) == 0;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        return line.getNum() + ":" + name + (isClosed ? " (закрыта с " + formatter.format(closingDate) + ")" : "");
    }
}