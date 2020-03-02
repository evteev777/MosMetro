import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        try {
            StationIndex mosMetro = null;
            Scanner scanner = new Scanner(System.in);

            System.out.println("Парсим веб-сайт или файл?");
            String input = scanner.nextLine();

            if (input.contains("f") || input.contains("ф")) {
                final String FILE = "src/main/resources/MosMetro.html";
                Document fromFile = Jsoup.parse(readFile(FILE));
                mosMetro = new StationIndex(fromFile);
            } else {
                final String LINK = "https://ru.wikipedia.org/wiki/Список_станций_Московского_метрополитена";
                Document fromLink = Jsoup.connect(LINK).userAgent("Mozilla").maxBodySize(0).get();
                mosMetro = new StationIndex(fromLink);
            }

            System.out.println("Расшифровать списки линий, станций, переходов и пересадочных узлов?");
            input = scanner.nextLine();

            boolean expand = false;
            if (input.contains("y") || input.contains("д")) {
                expand = true;
            }
            mosMetro.printStationIndex(expand);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readFile(String file) throws IOException {
        StringBuilder sb = new StringBuilder();
        List<String> lines = Files.readAllLines(Paths.get(file));
        lines.forEach(line -> sb.append(line).append("\n"));
        return sb.toString();
    }
}
