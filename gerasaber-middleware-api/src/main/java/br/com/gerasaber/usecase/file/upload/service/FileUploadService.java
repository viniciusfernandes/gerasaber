package br.com.gerasaber.usecase.file.upload.service;

import br.com.gerasaber.domain.entity.UploadRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service responsible for handling file upload requests and forwarding them to n8n.
 * Implements the business logic for the upload use case.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService implements IFileUploadService {
    
    private final RestTemplate restTemplate;
    
    @Value("${n8n.base-url}")
    private String n8nBaseUrl;
    
    @Value("${n8n.upload-endpoint}")
    private String n8nUploadEndpoint;
    
    @Value("${n8n.auth-token:}")
    private String n8nAuthToken;
    
    @Override
    public void processUploadRequest(UploadRequest uploadRequest) {
        log.info("Processing upload request with {} files", uploadRequest.getFiles().size());
        
        try {
            MultiValueMap<String, Object> formData = buildFormData(uploadRequest);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            // Add authorization header if token is provided
            if (!n8nAuthToken.isEmpty()) {
                headers.setBearerAuth(n8nAuthToken);
            }
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, headers);
            
            String url = n8nBaseUrl + n8nUploadEndpoint;
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            
            log.info("Successfully forwarded request to n8n. Response status: {}, Body: {}", 
                    response.getStatusCode(), response.getBody());
            
        } catch (Exception e) {
            log.error("Failed to forward request to n8n", e);
            throw new RuntimeException("Failed to process upload request", e);
        }
    }
    
    private MultiValueMap<String, Object> buildFormData(UploadRequest uploadRequest) {
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        
        // Add files
        for (UploadRequest.FilePart file : uploadRequest.getFiles()) {
            MultipartFile multipartFile = new MultipartFile() {
                @Override
                public String getName() {
                    return "files";
                }
                
                @Override
                public String getOriginalFilename() {
                    return file.getOriginalFilename();
                }
                
                @Override
                public String getContentType() {
                    return file.getContentType();
                }
                
                @Override
                public boolean isEmpty() {
                    return file.getContent().length == 0;
                }
                
                @Override
                public long getSize() {
                    return file.getSize();
                }
                
                @Override
                public byte[] getBytes() {
                    return file.getContent();
                }
                
                @Override
                public java.io.InputStream getInputStream() {
                    return new java.io.ByteArrayInputStream(file.getContent());
                }
                
                @Override
                public void transferTo(java.io.File dest) throws java.io.IOException, IllegalStateException {
                    throw new UnsupportedOperationException("Transfer not supported");
                }
            };
            formData.add("files", multipartFile);
        }
        
        // Add prompt description
        formData.add("promptDescription", uploadRequest.getPromptDescription());
        
        // Add metadata
        formData.add("requestId", uploadRequest.getRequestId());
        formData.add("timestamp", uploadRequest.getTimestamp().toString());
        
        return formData;
    }
}
