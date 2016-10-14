import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class Zangsisi {

    private static Document connect(String url) {

        Document doc = null;
        try {
            doc = Jsoup.connect(url)
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;

    }

    private static Map<String, String> toMap(Elements elements) {

        Map<String, String> map = new LinkedHashMap<>();
        elements.forEach(element -> map.put(element.text(), element.attr("abs:href")));
        return map;

    }

    public static String title(String url) {

        Document doc = connect(url);
        return doc.title().replaceAll("\\u00BB", "").trim();

    }

    public static Map<String, String> search(String keyword) {

        Map<String ,String> map = new LinkedHashMap<>();
        all().forEach((k,v) -> {
            if(k.contains(keyword)) {
                map.put(k, v);
            }
        });
        return map;

    }

    public static Map<String, String> all() {

        Document doc = connect("http://zangsisi.net/");
        Elements elements = doc.select("#manga-list a[href]");
        elements.addAll(doc.select("#recent-post a[href]"));
        // Blacklist
        Map<String, String> map = toMap(elements);
        map.remove("(수정/건의)", "http://zangsisi.net/?page_id=10705");
        map.remove("(완결 작품 목록)", "http://zangsisi.net/");
        return map;

    }

    public static Map<String, String> list(String url) {

        Document doc = connect(url);
        Elements elements = doc.select("a[href]");
        return toMap(elements);

    }

    public static Collection<String> images(String url) {

        Document doc = connect(url);
        Elements elements = doc.select("#post img[src]");
        Collection<String> collection = new LinkedList<>();
        elements.forEach(element -> collection.add(element.attr("src")));
        return collection;

    }

}
