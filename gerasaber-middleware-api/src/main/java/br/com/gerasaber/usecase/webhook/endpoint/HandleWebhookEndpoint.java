package br.com.gerasaber.usecase.webhook.endpoint;

import br.com.gerasaber.domain.entity.WebhookResponse;
import br.com.gerasaber.usecase.webhook.contract.WebhookResponseDto;
import br.com.gerasaber.usecase.webhook.mapping.WebhookResponseMapper;
import br.com.gerasaber.usecase.webhook.service.IWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
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

    @PostMapping(value = "/n8n-response")
    public ResponseEntity<Map<String, Object>> handleWebhookResponseMultipart(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "requestId", required = false) String requestId,
            @RequestParam(value = "filename", required = false) String filename) {

        log.info("Received multipart webhook response for request: {}", requestId);

        try {
            byte[] pdfContent = null;
            String finalFilename = filename;

            if (file != null && !file.isEmpty()) {
                pdfContent = file.getBytes();
                if (finalFilename == null) {
                    finalFilename = file.getOriginalFilename();
                }
                log.info("Received multipart file: {}", finalFilename);
            } else {
                log.warn("No file provided in multipart webhook response");
                return ResponseEntity.badRequest()
                        .body(Map.of("errors", List.of("No file provided in webhook response")));
            }

            if (pdfContent == null || pdfContent.length == 0) {
                log.warn("No valid PDF content found in webhook response");
                return ResponseEntity.badRequest()
                        .body(Map.of("errors", List.of("No valid PDF content found")));
            }

            // Process the webhook response
            WebhookResponse webhookResponse = webhookService.processWebhookResponse(
                    pdfContent, finalFilename, requestId);

            // Convert to response DTO
            WebhookResponseDto responseDto = webhookResponseMapper.toResponseDto(webhookResponse);

            log.info("Multipart webhook response processed successfully. File saved at: {}",
                    webhookResponse.getSavedPath());

            return ResponseEntity.ok(Map.of("data", responseDto));

        } catch (Exception e) {
            log.error("Failed to process multipart webhook response for request: {}", requestId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("errors", List.of("Failed to process webhook response: " + e.getMessage())));
        }
    }

    @PostMapping(value = "/n8n-response", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> handleWebhookResponseJson(
            @RequestBody Map<String, Object> jsonPayload) {

        log.info("Received JSON webhook response");

        try {
            byte[] pdfContent = extractPdfFromJsonPayload(jsonPayload);
            String finalFilename = (String) jsonPayload.getOrDefault("filename", "generated-summary.pdf");
            String requestId = (String) jsonPayload.get("requestId");

            if (pdfContent == null || pdfContent.length == 0) {
                log.warn("No valid PDF content found in JSON webhook response");
                return ResponseEntity.badRequest()
                        .body(Map.of("errors", List.of("No valid PDF content found in JSON payload")));
            }

            // Process the webhook response
            WebhookResponse webhookResponse = webhookService.processWebhookResponse(
                    pdfContent, finalFilename, requestId);

            // Convert to response DTO
            WebhookResponseDto responseDto = webhookResponseMapper.toResponseDto(webhookResponse);

            log.info("JSON webhook response processed successfully. File saved at: {}",
                    webhookResponse.getSavedPath());

            return ResponseEntity.ok(Map.of("data", responseDto));

        } catch (Exception e) {
            log.error("Failed to process JSON webhook response", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("errors", List.of("Failed to process webhook response: " + e.getMessage())));
        }
    }

    private byte[] extractPdfFromJsonPayload(Map<String, Object> jsonPayload) {
        try {
            // Try to get PDF content from various possible fields
            String pdfData = null;

            if (jsonPayload.containsKey("pdfContent")) {
                pdfData = (String) jsonPayload.get("pdfContent");
            } else if (jsonPayload.containsKey("pdf")) {
                pdfData = (String) jsonPayload.get("pdf");
            } else if (jsonPayload.containsKey("file")) {
                pdfData = (String) jsonPayload.get("file");
            } else if (jsonPayload.containsKey("data")) {
                pdfData = (String) jsonPayload.get("data");
            }

            if (pdfData != null && !pdfData.isEmpty()) {
                // Decode Base64 encoded PDF
                return Base64.getDecoder().decode(pdfData);
            }

            return null;
        } catch (Exception e) {
            log.error("Failed to extract PDF from JSON payload", e);
            return null;
        }
    }
}
