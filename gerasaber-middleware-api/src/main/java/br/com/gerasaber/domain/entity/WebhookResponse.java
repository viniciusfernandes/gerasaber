package br.com.gerasaber.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * Domain entity representing a webhook response from n8n.
 * Contains the processed PDF file and metadata.
 */
@Data
@Builder
public class WebhookResponse {
    
    private final String requestId;
    private final byte[] pdfContent;
    private final String filename;
    private final String contentType;
    private final LocalDateTime timestamp;
    private final Path savedPath;
}
