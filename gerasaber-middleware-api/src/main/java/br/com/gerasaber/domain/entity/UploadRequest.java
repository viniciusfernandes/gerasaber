package br.com.gerasaber.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Domain entity representing an upload request.
 * Contains all necessary information for processing file uploads.
 */
@Data
@Builder
public class UploadRequest {
    
    private final List<FilePart> files;
    private final String promptDescription;
    private final LocalDateTime timestamp;
    private final String requestId;
    
    /**
     * Value object representing a file part in the upload request.
     */
    @Data
    @Builder
    public static class FilePart {
        private final String originalFilename;
        private final String contentType;
        private final byte[] content;
        private final long size;
    }
}
