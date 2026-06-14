package com.plataforma.educativa.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Servicio EFS: gestiona el almacenamiento temporal de guías
 * en el sistema de archivos montado en /app/efs dentro del contenedor.
 */
@Service
public class EfsService {

    @Value("${efs.mount.path:/app/efs}")
    private String efsMountPath;

    /**
     * Guarda el contenido de una guía en EFS de forma temporal.
     * @return ruta completa del archivo guardado en EFS
     */
    public String guardarGuiaEnEfs(String guiaId, String transportista,
                                   LocalDate fecha, byte[] contenido) throws IOException {
        // Crear directorio organizado por fecha y transportista
        String fechaStr = fecha.format(DateTimeFormatter.BASIC_ISO_DATE);
        Path dirPath = Paths.get(efsMountPath, fechaStr, transportista);
        Files.createDirectories(dirPath);

        String nombreArchivo = "guia_" + guiaId + ".pdf";
        Path archivoPath = dirPath.resolve(nombreArchivo);

        Files.write(archivoPath, contenido, StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);

        return archivoPath.toString();
    }

    /**
     * Lee el contenido de una guía almacenada en EFS.
     */
    public byte[] leerGuiaDeEfs(String rutaEfs) throws IOException {
        Path archivoPath = Paths.get(rutaEfs);
        if (!Files.exists(archivoPath)) {
            throw new FileNotFoundException("Guía no encontrada en EFS: " + rutaEfs);
        }
        return Files.readAllBytes(archivoPath);
    }

    /**
     * Elimina el archivo temporal de EFS luego de subir a S3.
     */
    public void eliminarGuiaDeEfs(String rutaEfs) throws IOException {
        Path archivoPath = Paths.get(rutaEfs);
        Files.deleteIfExists(archivoPath);
    }

    /**
     * Verifica si el EFS está correctamente montado.
     */
    public boolean efsDisponible() {
        Path efsPath = Paths.get(efsMountPath);
        return Files.exists(efsPath) && Files.isWritable(efsPath);
    }
}
