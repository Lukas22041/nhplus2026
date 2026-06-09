package de.hitec.nhplus;

import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.utils.PdfExporter;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;

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
}

