package br.com.gerasaber.usecase.file.upload.endpoint;

import br.com.gerasaber.domain.entity.UploadRequest;
import br.com.gerasaber.usecase.file.upload.contract.UploadResponseDto;
import br.com.gerasaber.usecase.file.upload.mapping.UploadRequestMapper;
import br.com.gerasaber.usecase.file.upload.service.IFileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * REST endpoint for handling file upload requests.
 * Accepts multipart form data and forwards it to the n8n workflow.
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UploadFileEndpoint {
    
    private final IFileUploadService fileUploadService;
    private final UploadRequestMapper uploadRequestMapper;
    
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFiles(
            @RequestParam(value = "files") List<MultipartFile> files,
            @RequestParam("promptDescription") String promptDescription) {
        
        log.info("Received upload request with {} files", files.size());
        
        try {
            // Validate input
            if (files == null || files.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("errors", List.of("At least one file is required")));
            }
            
            if (promptDescription == null || promptDescription.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("errors", List.of("Prompt description is required")));
            }
            
            // Convert to domain object
            UploadRequest uploadRequest = uploadRequestMapper.toDomain(files, promptDescription);
            
            // Process the request asynchronously
            fileUploadService.processUploadRequest(uploadRequest);
            
            // Return acknowledgment response
            UploadResponseDto responseDto = uploadRequestMapper.toResponseDto(uploadRequest);
            
            log.info("Upload request processed successfully for request: {}", uploadRequest.getRequestId());
            
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(Map.of("data", responseDto));
                    
        } catch (Exception e) {
            log.error("Failed to process upload request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("errors", List.of("Failed to process upload request: " + e.getMessage())));
        }
    }
}
