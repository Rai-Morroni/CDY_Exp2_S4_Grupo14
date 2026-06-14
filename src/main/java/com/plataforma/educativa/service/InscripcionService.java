package com.plataforma.educativa.service;

import com.plataforma.educativa.model.Inscripcion;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InscripcionService {

    private final EfsService efsService; // Asumiendo que conservas tu EfsService
    private final S3Service s3Service;
    private final PdfGeneratorService pdfGeneratorService;

    private final Map<String, Inscripcion> repositorio = new ConcurrentHashMap<>();

    public InscripcionService(EfsService efsService, S3Service s3Service, PdfGeneratorService pdfGeneratorService) {
        this.efsService = efsService;
        this.s3Service = s3Service;
        this.pdfGeneratorService = pdfGeneratorService;
    }

    public Inscripcion crearInscripcion(String nombreEstudiante, String emailEstudiante, 
                                        List<String> cursos, double costoTotal) throws IOException {

        String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDate fecha = LocalDate.now();

        Inscripcion inscripcion = new Inscripcion(id, nombreEstudiante, emailEstudiante, fecha, cursos, costoTotal);

        byte[] pdfBytes = pdfGeneratorService.generarPdf(inscripcion);

        // Guardar temporalmente en EFS (Ajusta los parámetros según tu EfsService actual)
        String rutaEfs = efsService.guardarGuiaEnEfs(id, nombreEstudiante, fecha, pdfBytes); 
        inscripcion.setRutaEfs(rutaEfs);

        // Subir a S3
        String claveS3 = s3Service.subirResumen(id, pdfBytes);
        inscripcion.setRutaS3(claveS3);

        // Limpiar EFS
        efsService.eliminarGuiaDeEfs(rutaEfs);

        repositorio.put(id, inscripcion);
        return inscripcion;
    }

    public byte[] descargarResumen(String inscripcionId) throws IOException {
        Inscripcion inscripcion = obtenerPorId(inscripcionId);

        if (!s3Service.existeEnS3(inscripcion.getRutaS3())) {
            throw new IllegalStateException("El resumen " + inscripcionId + " no se encuentra en S3.");
        }
        return s3Service.descargarResumen(inscripcion.getRutaS3());
    }

    public Inscripcion actualizarInscripcion(String inscripcionId, List<String> nuevosCursos, double nuevoCosto) throws IOException {
        Inscripcion inscripcion = obtenerPorId(inscripcionId);
        inscripcion.setCursosSeleccionados(nuevosCursos);
        inscripcion.setCostoTotal(nuevoCosto);

        byte[] pdfActualizado = pdfGeneratorService.generarPdf(inscripcion);

        String nuevaClaveS3 = s3Service.actualizarResumen(inscripcionId, pdfActualizado);
        inscripcion.setRutaS3(nuevaClaveS3);

        repositorio.put(inscripcionId, inscripcion);
        return inscripcion;
    }

    public void eliminarInscripcion(String inscripcionId) {
        Inscripcion inscripcion = obtenerPorId(inscripcionId);
        s3Service.eliminarResumen(inscripcion.getRutaS3());
        repositorio.remove(inscripcionId);
    }

    public Inscripcion obtenerPorId(String inscripcionId) {
        Inscripcion inscripcion = repositorio.get(inscripcionId);
        if (inscripcion == null) {
            throw new NoSuchElementException("Inscripción no encontrada: " + inscripcionId);
        }
        return inscripcion;
    }
}