package br.com.gerasaber.usecase.webhook.endpoint;

import br.com.gerasaber.domain.entity.WebhookResponse;
import br.com.gerasaber.usecase.webhook.contract.WebhookResponseDto;
import br.com.gerasaber.usecase.webhook.mapping.WebhookResponseMapper;
import br.com.gerasaber.usecase.webhook.service.IWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * REST endpoint for handling webhook responses from n8n.
 * Receives processed PDF files and stores them locally.
 */
@Slf4j
@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class HandleWebhookEndpoint {
    
    private final IWebhookService webhookService;
    private final WebhookResponseMapper webhookResponseMapper;
    
    @PostMapping("/n8n-response")
    public ResponseEntity<Map<String, Object>> handleWebhookResponse(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "requestId", required = false) String requestId,
            @RequestParam(value = "filename", required = false) String filename) {
        
        log.info("Received webhook response for request: {}", requestId);
        
        try {
            byte[] pdfContent;
            String finalFilename = filename;
            
            if (file != null && !file.isEmpty()) {
                // Handle multipart file upload
                pdfContent = file.getBytes();
                if (finalFilename == null) {
                    finalFilename = file.getOriginalFilename();
                }
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("errors", List.of("No file provided in webhook response")));
            }
            
            // Process the webhook response
            WebhookResponse webhookResponse = webhookService.processWebhookResponse(
                    pdfContent, finalFilename, requestId);
            
            // Convert to response DTO
            WebhookResponseDto responseDto = webhookResponseMapper.toResponseDto(webhookResponse);
            
            log.info("Webhook response processed successfully. File saved at: {}", 
                    webhookResponse.getSavedPath());
            
            return ResponseEntity.ok(Map.of("data", responseDto));
            
        } catch (Exception e) {
            log.error("Failed to process webhook response for request: {}", requestId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("errors", List.of("Failed to process webhook response: " + e.getMessage())));
        }
    }
}
