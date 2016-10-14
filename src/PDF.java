import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;

public class PDF {

    public static void builder(String path, String name, String author, Collection<String> urls) {

        try {

            PDDocument pdDocument = new PDDocument();

            for (String url : urls) {

                PDPage pdPage = new PDPage();

                URLConnection urlConnection = new URL(url).openConnection();
                urlConnection.setRequestProperty("User-Agent", "Mozilla");

                BufferedImage bufferedImage = ImageIO.read(urlConnection.getInputStream());
                int width = bufferedImage.getWidth();
                int height = bufferedImage.getHeight();

                PDRectangle pdRectangle = new PDRectangle(width, height);
                pdPage.setMediaBox(pdRectangle);

                PDImageXObject pdImageXObject = LosslessFactory.createFromImage(pdDocument, bufferedImage);
                PDPageContentStream contentStream = new PDPageContentStream(pdDocument, pdPage);

                contentStream.drawImage(pdImageXObject, 0, 0, width, height);
                contentStream.close();

                pdDocument.addPage(pdPage);

            }

            PDDocumentInformation pdDocumentInformation = new PDDocumentInformation();
            pdDocumentInformation.setTitle(name);
            pdDocumentInformation.setAuthor(author);
            pdDocument.setDocumentInformation(pdDocumentInformation);

            pdDocument.save(path + name + ".pdf");
            pdDocument.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

}
