package com.plataforma.educativa.controller;

import com.plataforma.educativa.model.Inscripcion;
import com.plataforma.educativa.service.InscripcionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @PostMapping
    public ResponseEntity<?> crearInscripcion(@RequestBody Map<String, Object> body) {
        try {
            String nombre = (String) body.get("nombreEstudiante");
            String email = (String) body.get("emailEstudiante");
            List<String> cursos = (List<String>) body.get("cursosSeleccionados");
            double costo = Double.parseDouble(body.get("costoTotal").toString());

            Inscripcion inscripcion = inscripcionService.crearInscripcion(nombre, email, cursos, costo);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Resumen generado y subido a S3 exitosamente",
                    "resumenId", inscripcion.getId(),
                    "rutaS3", inscripcion.getRutaS3()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/descargar")
    public ResponseEntity<?> descargarResumen(@PathVariable String id) {
        try {
            byte[] pdfBytes = inscripcionService.descargarResumen(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"resumen_" + id + ".pdf\"")
                    .body(pdfBytes);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarInscripcion(@PathVariable String id, @RequestBody Map<String, Object> body) {
        try {
            List<String> cursos = (List<String>) body.get("cursosSeleccionados");
            double costo = Double.parseDouble(body.get("costoTotal").toString());

            Inscripcion inscripcion = inscripcionService.actualizarInscripcion(id, cursos, costo);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Resumen modificado y actualizado en S3",
                    "resumenId", inscripcion.getId()
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarInscripcion(@PathVariable String id) {
        try {
            inscripcionService.eliminarInscripcion(id);
            return ResponseEntity.ok(Map.of("mensaje", "Resumen " + id + " eliminado de S3 y del sistema"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}