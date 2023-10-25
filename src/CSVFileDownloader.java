

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CSVFileDownloader {
    public static void main(String[] args) {
        try {
            String url = "https://people.sc.fsu.edu/~jburkardt/data/csv/csv.html";
            Document document = Jsoup.connect(url).get();

            Elements links = document.select("a[href$=.csv]"); // Filtrar enlaces que terminan en .csv

            List<Thread> downloadThreads = new ArrayList<>();

            for (Element link : links) {
                String csvURL = link.absUrl("href");
                String csvName = link.text();
                Thread thread = new Thread(new CSVDownloader(csvURL, csvName));
                downloadThreads.add(thread);
                thread.start();
            }

            for (Thread thread : downloadThreads) {
                thread.join();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class CSVDownloader implements Runnable {
    private final String csvURL;
    private final String csvName;

    public CSVDownloader(String csvURL, String csvName) {
        this.csvURL = csvURL;
        this.csvName = csvName;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(csvURL);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            int lineCount = 0;

            while (reader.readLine() != null) {
                lineCount++;
            }

            reader.close();

            System.out.println("File: " + csvName + ", Lines: " + lineCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
