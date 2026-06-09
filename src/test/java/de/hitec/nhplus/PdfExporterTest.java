package de.hitec.nhplus;

import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Treatment;
import de.hitec.nhplus.utils.PdfExporter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PdfExporterTest {

    @Test
    public void exportPatientCreatesNonEmptyPdf() throws Exception {
        Patient p = new Patient(1, "Max", "Mustermann", LocalDate.of(1980,1,2), "3", "101", "1000");
        File tmp = Files.createTempFile("patient-test", ".pdf").toFile();
        tmp.deleteOnExit();

        PdfExporter.exportPatientToPdf(p, tmp);

        assertTrue(tmp.exists());
        assertTrue(tmp.length() > 0);
    }

    @Test
    public void exportContainsTreatmentData() throws Exception {
        Patient p = new Patient(2, "Erika", "Musterfrau", LocalDate.of(1975, 3, 4), "2", "202", "500");
        Treatment treatment = new Treatment(
                12,
                p.getPid(),
                LocalDate.of(2026, 6, 9),
                LocalTime.of(8, 0),
                LocalTime.of(8, 30),
                "Waschen",
                "Patientin wurde vollstaendig gewaschen"
        );

        File tmp = Files.createTempFile("patient-treatment-test", ".pdf").toFile();
        tmp.deleteOnExit();

        PdfExporter.exportPatientToPdf(p, List.of(treatment), "tester", tmp);

        try (PDDocument document = PDDocument.load(tmp)) {
            String text = new PDFTextStripper().getText(document);
            assertTrue(text.contains("Erika"));
            assertTrue(text.contains("Exportiert von: tester"));
            assertTrue(text.contains("Behandlungen"));
            assertTrue(text.contains("Waschen"));
            assertTrue(text.contains("vollstaendig gewaschen"));
        }
    }
}

