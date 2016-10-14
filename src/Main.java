import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) {

        Marumaru.images("http://www.yuncomics.com/archives/1791900").forEach(System.out::println);

        /*
        String author = Marumaru.author("http://marumaru.in/b/manga/26");
        String file  = Properties.getProperty("downloadpath") + Marumaru.title("http://marumaru.in/b/manga/26") + File.separator;
        Path path = Paths.get(file);
        if(Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Collection<Future> futures = new LinkedList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        Marumaru.list("http://marumaru.in/b/manga/26")
                .forEach((k, v) -> futures.add(
                        executorService.submit(() -> PDF.builder(file, Marumaru.title(v), author, Marumaru.images(v)))
                ));
        executorService.shutdown();
        futures.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        */

    }

}
