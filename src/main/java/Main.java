import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        try {
            final String LINK = "https://ru.wikipedia.org/wiki/Список_станций_Московского_метрополитена";
//            final String LINK = "https://ru.wikipedia.org/wiki/%D0%A1%D0%BF%D0%B8%D1%81%D0%BE%D0%BA_%D1%81%D1%82%D0%B0%D0%BD%D1%86%D0%B8%D0%B9_%D0%9C%D0%BE%D1%81%D0%BA%D0%BE%D0%B2%D1%81%D0%BA%D0%BE%D0%B3%D0%BE_%D0%BC%D0%B5%D1%82%D1%80%D0%BE%D0%BF%D0%BE%D0%BB%D0%B8%D1%82%D0%B5%D0%BD%D0%B0";

            Document doc = Jsoup.connect(LINK).userAgent("Mozilla").maxBodySize(0).get();
            Elements tables = doc.select("table");

            for (Element table : tables) {
                String tableHTML = table.outerHtml();

                if (tableHTML.contains("standard sortable")) { // TODO: Переделать через html-тэги
                    Document tableDoc = Jsoup.parse(tableHTML); // TODO: Убрать второй парсинг

                    Elements rows = tableDoc.select("tr");

                    for (int j = 1; j < rows.size(); j++) {
                        Element row = rows.get(j);
                        Elements columns = row.select("td");
                        if (columns.size() == 0) continue;

                        String[] lineNums = columns.get(0).text().
                                substring(0, columns.get(0).text().length() - 2). // TODO: Переделать через html-тэги
                                split(" ");

                        for (String lineNumber : lineNums) {

                            lineNumber = lineNumber.replaceAll("^0", "");

                            String lineName = columns.get(0).select("span").get(1).attr("title");

                            String stationName = columns.get(1).select("a").first().text();

                            ArrayList<String> connections = new ArrayList<>();
                            for (Element connection : columns.get(3).select("span")) {
                                if (!connection.text().isEmpty()) {
                                    connections.add(connection.text().
                                            replaceAll("^0", ""));
                                }
                            }
                            System.out.printf("%-7s\t%-32s\t%-24s\t%s\n",
                                    lineNumber,
                                    lineName,
                                    stationName,
                                    connections);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
