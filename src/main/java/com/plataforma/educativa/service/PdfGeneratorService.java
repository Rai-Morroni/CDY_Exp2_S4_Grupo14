package com.plataforma.educativa.service;

import com.plataforma.educativa.model.Inscripcion;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class PdfGeneratorService {

    public byte[] generarPdf(Inscripcion inscripcion) {
        try (com.itextpdf.kernel.pdf.PdfWriter writer =
                     new com.itextpdf.kernel.pdf.PdfWriter(
                             new java.io.ByteArrayOutputStream())) {

            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            com.itextpdf.kernel.pdf.PdfWriter w = new com.itextpdf.kernel.pdf.PdfWriter(baos);
            com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(w);
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc);

            // Título
            com.itextpdf.layout.element.Paragraph titulo =
                    new com.itextpdf.layout.element.Paragraph("RESUMEN DE INSCRIPCIÓN")
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER);
            document.add(titulo);

            document.add(new com.itextpdf.layout.element.Paragraph("\nN° de Resumen: " + inscripcion.getId()).setBold());
            document.add(new com.itextpdf.layout.element.Paragraph("Fecha: " + inscripcion.getFechaInscripcion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            document.add(new com.itextpdf.layout.element.Paragraph("Estudiante: " + inscripcion.getNombreEstudiante()));
            document.add(new com.itextpdf.layout.element.Paragraph("Email: " + inscripcion.getEmailEstudiante()));
            
            document.add(new com.itextpdf.layout.element.Paragraph("\nCursos Seleccionados:").setBold());
            for(String curso : inscripcion.getCursosSeleccionados()) {
                document.add(new com.itextpdf.layout.element.Paragraph("- " + curso));
            }

            document.add(new com.itextpdf.layout.element.Paragraph("\nTotal a Pagar: $" + inscripcion.getCostoTotal()).setBold().setFontSize(14));

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de inscripción " + inscripcion.getId(), e);
        }
    }
}