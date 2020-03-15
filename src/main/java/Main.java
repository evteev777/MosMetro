import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.StationIndex;
import json.StationIndexSerializer;
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

            final String LINK = "https://ru.wikipedia.org/wiki/Список_станций_Московского_метрополитена";
            Document fromLink = Jsoup.connect(LINK).userAgent("Mozilla").maxBodySize(0).get();
            mosMetro = new StationIndex(fromLink);

            final File gsonFile = new File("src/main/resources/mosmetro.json");

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(StationIndex.class, new StationIndexSerializer())
                    .create();
            FileWriter writer = new FileWriter(gsonFile, false);
            writer.write(gson.toJson(mosMetro));
            writer.flush();
            writer.close();

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
