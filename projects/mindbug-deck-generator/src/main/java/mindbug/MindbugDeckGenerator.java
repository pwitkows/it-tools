package mindbug;

import com.itextpdf.text.Document;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

class MindbugDeckGenerator {

    private final static String MAIN_DIR_PATH = "C:/mindbug/";

    private final static File OUTPUT_CARDS_DIR = new File(MAIN_DIR_PATH + "cards/");
    private final static String GRAPHICS_FILENAME = "/cards.jpg";
    private final static int CARD_WIDTH = 815;
    private final static int CARD_HEIGHT = 1111;

    public static void main(String[] args) throws Exception {
        MindbugDeckGenerator deckGenerator = new MindbugDeckGenerator();
        deckGenerator.generate();
    }

    void generate() throws Exception {
        prepareCardFiles();

        final String filename = MAIN_DIR_PATH + "mindbug_deck.pdf";
        Document document = null;

        try {
            document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(103);
            for (File file : OUTPUT_CARDS_DIR.listFiles()) {
                System.out.println("Add file:" + file.getAbsolutePath());
                addTableCell(table, file.getAbsolutePath(), "planet");
            }
            table.completeRow();
            document.add(table);
        } finally {
            if (document != null) {
                document.close();
            }
        }
    }

    private void prepareCardFiles() throws Exception {
        final BufferedImage mainImage = ImageIO.read(MindbugDeckGenerator.class.getResourceAsStream(GRAPHICS_FILENAME));

        if (OUTPUT_CARDS_DIR.exists()) {
            OUTPUT_CARDS_DIR.delete();
        }
        OUTPUT_CARDS_DIR.mkdirs();
        int x = 0, y = 0;
        int cardNo = 1;
        for (int line = 0; line < 7; line++) {
            for (int column = 0; column < 10; column++) {
                extractCardImage(mainImage, x, y, cardNo);
                cardNo++;
                x = x + CARD_WIDTH;
            }
            x = 0;
            y = y + CARD_HEIGHT;
        }
        System.out.println("Deck generated");
    }

    private void extractCardImage(BufferedImage mainImage, int x, int y, int cardNo) throws IOException {
        BufferedImage card = cropImage(mainImage, x, y, CARD_WIDTH, CARD_HEIGHT);
        File outputFile = new File(OUTPUT_CARDS_DIR.getAbsolutePath() + "/card_" + cardNo + ".png");
        ImageIO.write(card, "png", outputFile);
    }


    private void addTableCell(PdfPTable table, String cardFile, String cardType) throws Exception {
        Path path = Paths.get(cardFile);
        Image img = Image.getInstance(path.toAbsolutePath().toString());
        img.scaleAbsolute(179.5f, 250.0f);
        PdfPCell imageCell = new PdfPCell(img);
        imageCell.setPadding(0);
        table.addCell(imageCell);
    }

    public BufferedImage cropImage(BufferedImage bufferedImage, int x, int y, int width, int height) {
        BufferedImage croppedImage = bufferedImage.getSubimage(x, y, width, height);
        return croppedImage;
    }

}

