import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.StationIndex;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        try {
            StationIndex mosMetro;
//            Scanner scanner = new Scanner(System.in);
//
//            System.out.println("Парсим веб-сайт или файл?");
//            String input = scanner.nextLine();
//
//            if (input.contains("f") || input.contains("ф")) {
//                final String FILE = "src/main/resources/MosMetro.html";
//                Document fromFile = Jsoup.parse(readFile(FILE));
//                mosMetro = new core.StationIndex(fromFile);
//            } else {
                final String LINK = "https://ru.wikipedia.org/wiki/Список_станций_Московского_метрополитена";
                Document fromLink = Jsoup.connect(LINK).userAgent("Mozilla").maxBodySize(0).get();
                mosMetro = new StationIndex(fromLink);
//            }
//
//            System.out.println("Расшифровать списки линий, станций, переходов и пересадочных узлов?");
//            input = scanner.nextLine();
//
//            boolean expand = false;
//            if (input.contains("y") || input.contains("д")) {
//                expand = true;
//            }
//            mosMetro.printStationIndex(expand);

            mosMetro.printStationIndex(true);

            File jacksonFile = new File("src/main/resources/jresult.json");
            File gsonFile = new File("src/main/resources/gresult.json");

            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                mapper.writeValue(jacksonFile, mosMetro);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try(FileWriter writer = new FileWriter(gsonFile, false)) {
                writer.write(gson.toJson(mosMetro));
                writer.flush();
            }
            catch(IOException ex){
                ex.printStackTrace();
            }

        } catch (IOException e) {
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
