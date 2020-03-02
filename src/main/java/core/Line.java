package core;

import java.util.ArrayList;
import java.util.List;

public class Line implements Comparable<Line> {

    private final String number;
    private final String name;
    private final List<Station> stations;

    public Line(String number, String name) {
        this.number = number;
        this.name = name;
        stations = new ArrayList<>();
    }

    public void addStation(Station station) {
        stations.add(station);
    }

    public String getNum() {
        return number;
    }

    public String getName() {
        return name;
    }

    public List<Station> getStations() {
        return stations;
    }

    @Override
    public int compareTo(Line line) {
        return Double.compare(stringToDouble(number), stringToDouble(line.getNum()));
    }

    public double stringToDouble(String number) {
        if (number.matches("\\d+[А-Я]$")) {
            int endChar = number.charAt(number.length() - 1);
            String integerPart = number.substring(0, number.length() - 1);
            String fractionPart = String.valueOf((endChar - 1000) / 100.0).substring(1);
            number = integerPart + fractionPart;
        }
        return Double.parseDouble(number);
    }

    @Override
    public boolean equals(Object obj) {
        return compareTo((Line) obj) == 0;
    }

    @Override
    public String toString() {
        return name;
    }
}