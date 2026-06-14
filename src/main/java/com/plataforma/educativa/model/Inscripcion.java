package com.plataforma.educativa.model;

import java.time.LocalDate;
import java.util.List;

public class Inscripcion {

    private String id; // Número del resumen
    private String nombreEstudiante;
    private String emailEstudiante;
    private LocalDate fechaInscripcion;
    private List<String> cursosSeleccionados;
    private double costoTotal;
    private String rutaS3;
    private String rutaEfs;

    public Inscripcion() {}

    public Inscripcion(String id, String nombreEstudiante, String emailEstudiante, 
                       LocalDate fechaInscripcion, List<String> cursosSeleccionados, double costoTotal) {
        this.id = id;
        this.nombreEstudiante = nombreEstudiante;
        this.emailEstudiante = emailEstudiante;
        this.fechaInscripcion = fechaInscripcion;
        this.cursosSeleccionados = cursosSeleccionados;
        this.costoTotal = costoTotal;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }

    public String getEmailEstudiante() { return emailEstudiante; }
    public void setEmailEstudiante(String emailEstudiante) { this.emailEstudiante = emailEstudiante; }

    public LocalDate getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDate fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }

    public List<String> getCursosSeleccionados() { return cursosSeleccionados; }
    public void setCursosSeleccionados(List<String> cursosSeleccionados) { this.cursosSeleccionados = cursosSeleccionados; }

    public double getCostoTotal() { return costoTotal; }
    public void setCostoTotal(double costoTotal) { this.costoTotal = costoTotal; }

    public String getRutaS3() { return rutaS3; }
    public void setRutaS3(String rutaS3) { this.rutaS3 = rutaS3; }

    public String getRutaEfs() { return rutaEfs; }
    public void setRutaEfs(String rutaEfs) { this.rutaEfs = rutaEfs; }
}