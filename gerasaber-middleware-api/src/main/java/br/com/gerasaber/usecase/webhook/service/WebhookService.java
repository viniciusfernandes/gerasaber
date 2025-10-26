package br.com.gerasaber.usecase.webhook.service;

import br.com.gerasaber.domain.entity.WebhookResponse;
import br.com.gerasaber.domain.port.IFileStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service responsible for handling webhook responses from n8n.
 * Processes incoming PDF files and stores them using the file storage port.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService implements IWebhookService {
    
    private final IFileStoragePort fileStoragePort;
    
    @Override
    public WebhookResponse processWebhookResponse(byte[] pdfContent, String filename, String requestId) {
        log.info("Processing webhook response for request: {}", requestId);
        
        try {
            // Generate unique filename if not provided
            String finalFilename = filename != null ? filename : "generated-summary-" + UUID.randomUUID() + ".pdf";
            
            // Save file with timestamp-based directory structure
            Path savedPath = fileStoragePort.saveFileWithTimestamp(finalFilename, pdfContent);
            
            WebhookResponse response = WebhookResponse.builder()
                    .requestId(requestId)
                    .pdfContent(pdfContent)
                    .filename(finalFilename)
                    .contentType("application/pdf")
                    .timestamp(LocalDateTime.now())
                    .savedPath(savedPath)
                    .build();
            
            log.info("Successfully processed webhook response. File saved at: {}", savedPath);
            return response;
            
        } catch (Exception e) {
            log.error("Failed to process webhook response for request: {}", requestId, e);
            throw new RuntimeException("Failed to process webhook response", e);
        }
    }
}
