package core;

import core.Line;
import core.Station;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class StationIndex {

    Map<String, Line> lines;
    Map<String, Station> stations;
    private static int closedStationsCount = 0;
    Map<Station, TreeSet<Station>> connections;
    List<Set<Station>> nodes;

    public StationIndex() {
        lines = new HashMap<>();
        stations = new LinkedHashMap<>();
        connections = new LinkedHashMap<>();
        nodes = new LinkedList<>();
    }

    public StationIndex(Document doc) throws NullPointerException {
        this();

        // Принимаем, что станции в первой колонке указаны верно, а в переходах возможны ошибки
        // Так как на момент парсинга переходов некоторые станции еще не добавлены в Map stations,
        // сначала в цикле собираем переходы в виде строк в Set connectionsAsString,
        // а после цикла преобразуем их в Map connections и Set nodes
        Set<String> connectionsAsString = new TreeSet<>(String::compareTo);

        // Три таблицы со станциями: Метро, Монорельс, МЦК
        Elements tablesWithStations = doc.select("table.standard.sortable");

        for (Element table : tablesWithStations) {
            Elements rows = table.select("tr");

            for (Element row : rows) {
                Elements cells = row.select("td");

                if (!cells.isEmpty()) {
                    int linesOnOneStation = cells.get(0).select("span[title]").size();
                    for (int i = 0; i < linesOnOneStation; i++) {
                        Line line = parseLine(cells, i);
                        Station station = parseStation(cells, line);
                        connectionsAsString.addAll(parseConnectionsAsString(cells, station));
                    }
                }
            }
        }

        connections.putAll(parseConnections(connectionsAsString));
        nodes.addAll(parseNodes(connections));
    }

    public void addLine(Line line) {
        lines.put(line.getNumber(), line);
    }

    public void addStation(Station station) {
        String key = station.getLine().getNumber() + ":" + station.getName();
        stations.put(key, station);
//        station.getLine().addStation(station);
    }

    private Line parseLine(Elements cells, int i) {
        Line line;

        String lineNum = lineNumCorrection(cells.get(0).select("span[style]").get(i).text());
        String lineName = cells.get(0).select("span[title]").get(i).attr("title");

        if (lines.containsKey(lineNum)) {
            line = lines.get(lineNum);
        } else {
            line = new Line(lineNum, lineName);
            addLine(line);
        }
        return line;
    }

    private Station parseStation(Elements cells, Line line) {

        Station station;
        String stationName = cells.get(1).selectFirst("a").text();

        if (stations.containsKey(stationName)) {
            station = stations.get(stationName);
        } else {
            station = new Station(stationName, line);
            addStation(station);
        }

        if (cells.get(1).html().contains("акрыт")) { // (З|з)акрыт(а|о)
//            final String dateRegex = "^.*([1-9]|[0-3]\\d)(\\.|\\s+)(0?[1-9]|1[0-2]|января|февраля|марта|апреля|мая|июня|июля|августа|сентября|октября|ноября|декабря)(\\.|\\s+)((\\d\\d)|(19[6-9]\\d|20\\d\\d))($|\\s|\\)|г)";
//            String dateAsString = cells.get(1).select("small").text().replaceAll(dateRegex, "$1 $3 $5");
//            station.setClosingDate(LocalDate.parse(dateAsString, DateTimeFormatter.ofPattern("d MMMM yyyy")));

            station.setIsClosed(true);
            closedStationsCount++;
        }
        return station;
    }

    private Set<String> parseConnectionsAsString(Elements cells, Station station) {
        Set<String> connectionsAsString = new TreeSet<>(String::compareTo);
        List<String> connectedLinesNums = getConnectedLinesNums(cells);

        if (!connectedLinesNums.isEmpty()) {
            List<String> connectedStationsNames = getConnectedStationsNames(cells);

            TreeSet<String> connectedStationsSet = new TreeSet<>(String::compareTo);
            for (int i = 0; i < connectedStationsNames.size(); i++) {
                connectedStationsSet.add(connectedLinesNums.get(i) + ":" + connectedStationsNames.get(i));
            }

            String stationAsString = station.getLine().getNumber() + ":" + station.getName();
            String connectedStationsAsString = connectedStationsSet.toString()
                    .substring(1, connectedStationsSet.toString().length() - 1);
            connectionsAsString.add(stationAsString + " --> " + connectedStationsAsString);
        }
        return connectionsAsString;
    }

    private Map<Station, TreeSet<Station>> parseConnections(Set<String> connectionsAsString) {
        Map<Station, TreeSet<Station>> connections = new LinkedHashMap<>();
        for (String c : connectionsAsString) {
            String[] connectAsStringSplit = c.split(" --> ");
            Station stationFrom = stations.get(connectAsStringSplit[0]);

            TreeSet<Station> stationsTo = new TreeSet<>(Station::compareTo);

            for (String s : connectAsStringSplit[1]
                    .split(", ")) {
                if (stations.containsKey(s)) {
                    stationsTo.add(stations.get(s));
                    connections.put(stationFrom, stationsTo);
                } else {
                    System.err.println("ОШИБКА:\tПарсинг переходов:\tтакой станции не существует:\t" + s);
                }
            }
        }
        return connections;
    }

    private Set<Set<Station>> parseNodes(Map<Station, TreeSet<Station>> connectionsAsString) {
        Set<Set<Station>> connectionsSet = new TreeSet<>(Comparator.comparing(Set::toString));
        connectionsAsString.forEach((k, v) -> {
            Set<Station> conection = new TreeSet<>(Station::compareTo);
            conection.add(k);
            conection.addAll(v);
            connectionsSet.add(conection);
        });
        // Приводим к одному виду разные переходы с одинаковой первой станцией
        List<Set<Station>> connectionsList = new LinkedList<>(connectionsSet);
        for (int i = 0; i < connectionsList.size() - 1; i++) {
            List<Station> thisStations = new LinkedList<>(connectionsList.get(i));
            List<Station> nextStations = new LinkedList<>(connectionsList.get(i + 1));

            Set<Station> tempSet1 = new TreeSet<>(Station::compareTo);
            if (thisStations.get(0).equals(nextStations.get(0))) {
                tempSet1.addAll(thisStations);
                tempSet1.addAll(nextStations);
                connectionsList.set(i, tempSet1);
                connectionsList.set(i + 1, tempSet1);
            }
        }
        // Удаляем дубликаты
        Set<Set<Station>> nodes = new TreeSet<>(Comparator.comparing(Set::toString));
        nodes.addAll(connectionsList);
        return nodes;
    }

    private String lineNumCorrection(String lineNum) {
        return lineNum.replaceAll("(^\\d\\D)", "0$1") // добавляем ведущий 0 у номеров типа 8А
                .replaceAll("(.*)(\\d{2}\\D*)$", "$2");// убираем ведущий 0 у номеров типа 011А)
    }

    private List<String> getConnectedStationsNames(Elements cells) {
        return cells.get(3).select("span[title]").stream()
                .map(e -> e.attr("title")
                        .replaceAll("Переход на станцию ", "")
                        .replaceAll("Кросс-платформенная пересадка на станцию ", "")
                        .replaceAll("Московского центрального кольца", "")
                        .replaceAll("Большой кольцевой линии", "")
                        .replaceAll("[А-Яа-яё]+-*[А-Яа-яё]+\\s[А-Яа-яё]+$", "") // удаляем линии
                        .replaceAll("\\(.*\\)", "") // удаляем текст в скобках
                        .trim())
                .collect(Collectors.toList());
    }

    private List<String> getConnectedLinesNums(Elements cells) {
        return cells.get(3).select("span.sortkey").stream()
                .map(lineNum -> lineNumCorrection(lineNum.text()))
                .collect(Collectors.toList());
    }

    public void printStationIndex(boolean expand) {
        System.out.printf("Линий:             \t%3s\n", lines.size());
        System.out.printf("Станций:           \t%3s (из них закрыто %s станций)\n", stations.size(), closedStationsCount);
        System.out.printf("Переходов:         \t%3s\n", connections.size());
        System.out.printf("Пересадочных узлов:\t%3s\n", nodes.size());

        System.out.printf("\n%-36s | %s станции на %s линиях\n", "ЛИНИИ И СТАНЦИИ", stations.size(), lines.size());
        System.out.println("в том числе:");
        lines.values().stream().sorted().forEach(line -> {
            System.out.printf("%-3s %-32s | Станций: %3s\n", line.getNumber(), line.getName(), line.getStations().size());
            if (expand) printEachStation(line);
        });

        System.out.printf("\n%-36s | %s станций\n", "ЗАКРЫТЫЕ СТАНЦИИ", closedStationsCount);
        if (expand) printEachClosedStation();

        System.out.printf("\n%-36s | %s переходов\n", "ПЕРЕХОДЫ", connections.size());
        if (expand) printEachConnect();

        System.out.printf("\n%-36s | %s пересадочных узлов\n", "ПЕРЕСАДОЧНЫЕ УЗЛЫ", nodes.size());
        if (expand) printEachNodes();
    }

    private void printEachStation(Line line) {
        line.getStations().forEach(s -> System.out.println("\t• " + s));
    }

    private void printEachClosedStation() {
        stations.values().stream().sorted().filter(Station::getIsClosed)
//                .forEach(s -> System.out.printf("%-3s:%-32s\tзакрыта с %s\n",
//                        s.getLine().getNumber(), s.getName(), s.getClosingDate()));
        .forEach(s -> System.out.printf("%-3s:%-32s\tзакрыта\n",
                        s.getLine().getNumber(), s.getName()));
    }

    private void printEachConnect() {
        for (Map.Entry<Station, TreeSet<Station>> entry : connections.entrySet()) {
            System.out.printf("%-36s\t-->\t\t", entry.getKey());
            for (Station station : entry.getValue()) {
                System.out.printf("%s, ", station);
            }
            System.out.println();
        }
    }

    private void printEachNodes() {
        for (Set<Station> node : nodes) {
            for (Station station : node) {
                System.out.printf("%-36s\t", station);
            }
            System.out.println();
        }
    }
}