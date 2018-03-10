import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class BasicWebCrawler {

    private final Set<URL> links;
    private final long startTime;

    private BasicWebCrawler(final URL startURL) {
        this.links = new HashSet<>();
        this.startTime = System.currentTimeMillis();
        crawl(initURLS(startURL));
    }

    private static Set<URL> initURLS(final URL startURL) {
        final Set<URL> startURLS = new HashSet<>();
        startURLS.add(startURL);
        return startURLS;
    }

    private void crawl(final Set<URL> URLS) {
        URLS.removeAll(this.links);
        if (!URLS.isEmpty()) {
            final Set<URL> newURLS = new HashSet<>();
            try {
                this.links.addAll(URLS);
                for (final URL url : URLS) {
                    System.out.println("time = " + (System.currentTimeMillis() - this.startTime) +
                            " connect to : " + url);
                    final Document document = Jsoup.connect(url.toString()).get();
                    final Elements linksOnPage = document.select("a[href]");
                    for (final Element page : linksOnPage) {
                        final String urlText = page.attr("abs:href").trim();
                        final URL discoveredURL = new URL(urlText);
                        newURLS.add(discoveredURL);
                    }
                }
            } catch (final Exception | Error ignored) {
            }
            crawl(newURLS);
        }
    }

    private void writeResults() throws IOException {
        final File tmpFile = File.createTempFile("crawlResults", ".out");
        try(final FileWriter writer = new FileWriter(tmpFile)) {
            for(final URL url : this.links) {
                writer.write(url + "\n");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        final BasicWebCrawler crawler = new BasicWebCrawler(new URL("http://www.gutenberg.org/"));
        crawler.writeResults();
    }

}