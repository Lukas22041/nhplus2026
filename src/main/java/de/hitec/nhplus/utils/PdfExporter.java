package de.hitec.nhplus.utils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import de.hitec.nhplus.model.Patient;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * Simple PDF exporter using Apache PDFBox. Exports a single patient's basic data to a PDF file.
 */
public class PdfExporter {

    private static final DateTimeFormatter OUTPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static void exportPatientToPdf(Patient patient, File file) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 18);
                cs.newLineAtOffset(50, 760);
                cs.showText("Patientendaten");
                cs.endText();

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(50, 730);

                writeLine(cs, "ID: ", String.valueOf(patient.getPid()));
                writeLine(cs, "Vorname: ", safe(patient.getFirstName()));
                writeLine(cs, "Nachname: ", safe(patient.getSurname()));

                String dob = safe(patient.getDateOfBirth());
                try {
                    LocalDate parsed = LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    dob = parsed.format(OUTPUT_DATE_FORMAT);
                } catch (Exception ignored) {
                }
                writeLine(cs, "Geburtstag: ", dob);
                writeLine(cs, "Pflegegrad: ", safe(patient.getCareLevel()));
                writeLine(cs, "Raum: ", safe(patient.getRoomNumber()));
                writeLine(cs, "Vermögensstand: ", safe(patient.getAssets()));

                cs.endText();
            }

            doc.save(file);
        }
    }

    private static void writeLine(PDPageContentStream cs, String label, String value) throws IOException {
        cs.showText(label + value);
        cs.newLineAtOffset(0, -18);
    }

    private static String safe(String in) {
        return in == null ? "" : in;
    }
}

