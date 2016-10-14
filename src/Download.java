import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Download implements Runnable {

    private String file;
    private String url;

    private static String regex(String url) {

        Pattern pattern = Pattern.compile("\\.(jpe?g|png|gif)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        if(matcher.find()) {
            return matcher.group();
        } else {
            return null;
        }

    }

    public static void builder(String name, Collection<String> urls) {

        String file  = Properties.getProperty("downloadpath") + name;
        Path path = Paths.get(file);
        if(Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ExecutorService executorService = Executors.newFixedThreadPool(8);
        int count = 0;
        for(String url : urls){
            Download download = new Download(file + File.separator + count + regex(url), url);
            executorService.execute(download);
            count++;
        }
        executorService.shutdown();

    }

    public Download(String file, String url) {
        this.file = file;
        this.url = url;
    }

    @Override
    public void run() {

        try {
            URLConnection connection = new URL(url).openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla");
            ReadableByteChannel rbc = Channels.newChannel(connection.getInputStream());
            FileOutputStream fos = new FileOutputStream(new File(file));
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

    }

}
