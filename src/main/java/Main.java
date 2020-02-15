import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {

        try {
            final String LINK = "https://ru.wikipedia.org/wiki/Список_станций_Московского_метрополитена";
            Document doc = Jsoup.connect(LINK).userAgent("Mozilla").maxBodySize(0).get();

//            final String FILE = "src/main/resources/MosMetro.html";
//            Document doc = Jsoup.parse(readFile(FILE));

            Element table = doc.selectFirst("table.standard.sortable");

            Elements trs = table.select("tr");
            for (int i = 1; i < trs.size(); i++) {
                Element tr = trs.get(i);
                Elements tds = tr.select("td");
                System.out.printf("%-7s\t%-32s\t%-24s\t%8s\t%8s\n",
                        tds.get(0).selectFirst("span").text().replaceAll("^0", ""),
                        tr.select("span").get(1).attr("title"),
                        tds.get(1).selectFirst("a").text(),
                        tds.get(3).select("span.sortkey").stream()
                                .map(e -> e.text()
                                        .replaceAll("^0", ""))
                                .collect(Collectors.joining("|")),
                        tds.get(3).select("span[title]").stream()
                                .map(e -> e.attr("title")
                                        .replaceAll("Переход на станцию ", "")
                                        .replaceAll("Московского центрального кольца", "")
                                        .replaceAll("Кросс-платформенная пересадка на станцию ", "")
                                        .replaceAll("[А-Яа-яё]+-*[А-Яа-яё]+\\s[А-Яа-яё]+$", "")
                                        .trim())
                                .collect(Collectors.joining("|")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private static String readFile(String file) throws IOException {
//        StringBuilder sb = new StringBuilder();
//        List<String> lines = Files.readAllLines(Paths.get(file));
//        lines.forEach(line -> sb.append(line).append("\n"));
//        return sb.toString();
//    }
}
