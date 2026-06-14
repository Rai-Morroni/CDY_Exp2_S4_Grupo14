package com.plataforma.educativa.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Construye la clave S3 con la estructura requerida por el caso de la Semana 4:
     * /{numero_resumen}/resumen_{numero_resumen}.pdf
     */
    public String construirClaveS3(String inscripcionId) {
        return inscripcionId + "/resumen_" + inscripcionId + ".pdf";
    }

    public String subirResumen(String inscripcionId, byte[] contenido) {
        String claveS3 = construirClaveS3(inscripcionId);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(claveS3)
                .contentType("application/pdf")
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(contenido));
        return claveS3;
    }

    public byte[] descargarResumen(String claveS3) throws IOException {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(claveS3)
                .build();

        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request);
        return response.readAllBytes();
    }

    public String actualizarResumen(String inscripcionId, byte[] nuevoContenido) {
        // S3 usa put idempotente, sobreescribe el objeto existente
        return subirResumen(inscripcionId, nuevoContenido);
    }

    public void eliminarResumen(String claveS3) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(claveS3)
                .build();
        s3Client.deleteObject(request);
    }

    public boolean existeEnS3(String claveS3) {
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(claveS3)
                    .build();
            s3Client.headObject(request);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }
}