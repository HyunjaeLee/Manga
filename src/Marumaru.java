import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Marumaru {

    public static Map<String, String> cookies = new HashMap<>();

    private static Document connect(String url) {

        Document doc = null;

        try {
            doc = Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .timeout(10000)
                    .cookies(cookies)
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(doc.title().equals("You are being redirected...")) {

            String script =
                    "var Marumaru = Java.type('Marumaru');" +
                            "var document = {};" +
                            "var location = {reload: function(){" +
                            "var cookie = document.cookie.toString().split('=');" +
                            "Marumaru.cookies.put(cookie[0], cookie[1])" +
                            "}};" +
                            doc.select("script").first().data() + ";";

            ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");

            try {
                scriptEngine.eval(script);
            } catch (ScriptException e) {
                e.printStackTrace();
            }

            try {
                doc = Jsoup.connect(url)
                        .userAgent("Mozilla")
                        .timeout(10000)
                        .cookies(cookies)
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
        if(url.contains("http://marumaru.in/b/")) {
            return doc.select(".subject").first().text();
        } else if(url.contains("archives")) {
            return doc.select(".entry-title").first().text();
        } else {
            return null;
        }

    }

    public static String author(String url) {

        Document doc = connect(url);
        String meta = doc.select("meta[name=keywords]").first().toString();
        Pattern pattern = Pattern.compile("A:(.*?),");
        Matcher matcher = pattern.matcher(meta);
        if(matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }

    }

    public static Map<String, String> search(String keyword) {

        String keywordEncoded = keyword.replaceAll(" ", "%20");
        Document doc = connect("http://marumaru.in/?r=home&mod=search&keyword=" + keywordEncoded);
        Elements elements = doc.select(".postbox a[href]");
        return toMap(elements);

    }

    public static Map<String, String> all() {

        Document doc = connect("http://marumaru.in/c/1/");
        Elements elements = doc.select(".widget_review01 ul a[href]");
        return toMap(elements);

    }

    public static Map<String, String> list(String url) {

        Document doc = connect(url);
        Elements elements = doc.select(".content a[href*=archives]");
        return toMap(elements);

    }

    public static Collection<String> images(String url) {

        Document doc = connect(url);
        Elements elements = doc.select(".entry-content > p a[href]");
        Collection<String> collection = new LinkedList<>();
        elements.forEach(element -> collection.add(element.attr("abs:href")));
        return collection;

    }

}
