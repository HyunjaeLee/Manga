import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptJobManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class MannaSpace {

    static {
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF); // Silent
    }

    private static Document connect(String url) {

        Document doc = null;
        try {
            doc = Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .timeout(10000)
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;

    }

    private static Map<String, String> toMap(Elements elements) {

        Map<String, String> map = new LinkedHashMap<>();
        elements.forEach(element -> map.put(element.attr("title"), "https://manaa.space" + element.attr("link")));
        return map;

    }

    public static String title(String url) {

        Document doc = connect(url);
        return doc.title().replaceAll(" \\| 마나스페이스", "").trim();

    }

    public static Map<String, String> search(String keyword) {

        String keywordEncoded = keyword.replaceAll(" ", "%20");
        Document doc = connect("https://manaa.space/comics/?keyword=" + keywordEncoded);
        Elements elements = doc.select("[link*=work]");
        return toMap(elements);

    }

    public static Map<String, String> all() {

         return null;

    }

    public static Map<String, String> list(String url) {

        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_45);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setJavaScriptEnabled(true);

        HtmlPage page  = null;
        try {
            page = webClient.getPage(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String html = page.getWebResponse().getContentAsString();

        Document doc = Jsoup.parse(html);
        Elements names = doc.select("#content h4");
        Elements urls = doc.select("#content a[href~=https://manaa.space/post/uploader/[\\d\\w]+$]");

        Map<String, String> map = new LinkedHashMap<>();

        Iterator<Element> i1 = names.iterator();
        Iterator<Element> i2 = urls.iterator();
        while (i1.hasNext() && i2.hasNext()) {
            map.put(i1.next().text(), i2.next().attr("abs:href"));
        }

        return map;

    }

    public static Collection<String> images(String url) {

        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_45);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setJavaScriptEnabled(true);

        HtmlPage page  = null;
        try {
            page = webClient.getPage(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JavaScriptJobManager manager = page.getEnclosingWindow().getJobManager();
        while (manager.getJobCount() > 0) { // Wait for javascript loading
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<?> domList = page.getByXPath("//*[@id=\"view\"]/img/@src");
        Collection<String> collection = new LinkedList<>();
        for (Object object : domList) {
            DomAttr domAttr = (DomAttr) object;
            collection.add(domAttr.getTextContent());
        }

        return collection;

    }

}
