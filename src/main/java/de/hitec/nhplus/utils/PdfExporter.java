package de.hitec.nhplus.utils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Treatment;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * Simple PDF exporter using Apache PDFBox. Exports a single patient's basic data to a PDF file.
 */
public class PdfExporter {

    private static final DateTimeFormatter OUTPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter OUTPUT_TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final float MARGIN = 50f;
    private static final float ROW_HEIGHT = 16f;
    private static final PDFont FONT_NORMAL = PDType1Font.HELVETICA;
    private static final PDFont FONT_BOLD = PDType1Font.HELVETICA_BOLD;
    private static final float FONT_SIZE = 11f;

    private PdfExporter() {
    }

    public static void exportPatientToPdf(Patient patient, File file) throws IOException {
        exportPatientToPdf(patient, Collections.emptyList(), "Unbekannt", file);
    }

    public static void exportPatientToPdf(Patient patient, List<Treatment> treatments, File file) throws IOException {
        exportPatientToPdf(patient, treatments, "Unbekannt", file);
    }

    public static void exportPatientToPdf(Patient patient, List<Treatment> treatments, String exportedBy, File file) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PdfWriter writer = new PdfWriter(doc);
            writer.writeTitle("Patientendaten");
            writer.writeKeyValue("Exportiert am", LocalDateTime.now().format(OUTPUT_TIMESTAMP_FORMAT));
            writer.writeKeyValue("Exportiert von", safe(exportedBy));
            writer.addVerticalSpace(4f);

            writer.writeKeyValue("ID", String.valueOf(patient.getPid()));
            writer.writeKeyValue("Vorname", safe(patient.getFirstName()));
            writer.writeKeyValue("Nachname", safe(patient.getSurname()));
            writer.writeKeyValue("Geburtstag", formatDate(safe(patient.getDateOfBirth())));
            writer.writeKeyValue("Pflegegrad", safe(patient.getCareLevel()));
            writer.writeKeyValue("Raum", safe(patient.getRoomNumber()));
            writer.writeKeyValue("Vermoegensstand", safe(patient.getAssets()));

            writer.writeSectionHeader("Behandlungen");
            if (treatments == null || treatments.isEmpty()) {
                writer.writeParagraph("Keine Behandlungen vorhanden.");
            } else {
                int index = 1;
                for (Treatment treatment : treatments) {
                    writer.writeParagraph(index + ". " + treatment.getDate() + " " + treatment.getBegin() + " - " + treatment.getEnd());
                    writer.writeKeyValue("Kurzbeschreibung", safe(treatment.getDescription()));
                    writer.writeKeyValue("Langbeschreibung", safe(treatment.getRemarks()));
                    writer.addVerticalSpace(4f);
                    index++;
                }
            }

            writer.close();

            doc.save(file);
        }
    }

    private static String formatDate(String value) {
        try {
            LocalDate parsed = LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return parsed.format(OUTPUT_DATE_FORMAT);
        } catch (Exception ignored) {
            return value;
        }
    }

    private static String safe(String in) {
        return in == null ? "" : in;
    }

    private static final class PdfWriter {
        private final PDDocument document;
        private PDPage page;
        private PDPageContentStream stream;
        private float y;

        private PdfWriter(PDDocument document) throws IOException {
            this.document = document;
            newPage();
        }

        private void writeTitle(String title) throws IOException {
            writeWrappedLine(title, FONT_BOLD, 18f);
            addVerticalSpace(8f);
        }

        private void writeSectionHeader(String title) throws IOException {
            addVerticalSpace(8f);
            stream.setNonStrokingColor(33, 70, 132);
            writeWrappedLine(title, FONT_BOLD, 14f);
            stream.setNonStrokingColor(0, 0, 0);
            addVerticalSpace(2f);
        }

        private void writeKeyValue(String key, String value) throws IOException {
            writeParagraph(key + ": " + value);
        }

        private void writeParagraph(String text) throws IOException {
            for (String line : wrap(text, FONT_NORMAL, FONT_SIZE, page.getMediaBox().getWidth() - (2 * MARGIN))) {
                writeWrappedLine(line, FONT_NORMAL, FONT_SIZE);
            }
        }

        private void writeWrappedLine(String line, PDFont font, float size) throws IOException {
            ensureSpace(ROW_HEIGHT);
            stream.beginText();
            stream.setFont(font, size);
            stream.newLineAtOffset(MARGIN, y);
            stream.showText(line);
            stream.endText();
            y -= ROW_HEIGHT;
        }

        private void addVerticalSpace(float space) {
            y -= space;
        }

        private void ensureSpace(float needed) throws IOException {
            if (y - needed < MARGIN) {
                newPage();
            }
        }

        private void newPage() throws IOException {
            if (stream != null) {
                stream.close();
            }
            page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            stream = new PDPageContentStream(document, page);
            y = page.getMediaBox().getHeight() - MARGIN;
        }

        private void close() throws IOException {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private static List<String> wrap(String text, PDFont font, float size, float maxWidth) throws IOException {
        if (text == null || text.isBlank()) {
            return List.of("");
        }

        List<String> lines = new java.util.ArrayList<>();
        String[] inputLines = text.replace("\r", "").split("\n", -1);
        for (String inputLine : inputLines) {
            StringBuilder current = new StringBuilder();
            String[] words = inputLine.split("\\s+");
            for (String word : words) {
                String candidate = current.length() == 0 ? word : current + " " + word;
                float candidateWidth = font.getStringWidth(candidate) / 1000f * size;
                if (candidateWidth <= maxWidth || current.length() == 0) {
                    current = new StringBuilder(candidate);
                } else {
                    lines.add(current.toString());
                    current = new StringBuilder(word);
                }
            }
            lines.add(current.toString());
        }
        return lines;
    }
}

