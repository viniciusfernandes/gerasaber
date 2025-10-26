package br.com.gerasaber.usecase.file.upload.mapping;

import br.com.gerasaber.domain.entity.UploadRequest;
import br.com.gerasaber.usecase.file.upload.contract.UploadResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Mapper for converting between domain objects and DTOs in the upload use case.
 */
@Component
public class UploadRequestMapper {
    
    public UploadRequest toDomain(List<MultipartFile> files, String promptDescription) {
        List<UploadRequest.FilePart> fileParts = new ArrayList<>();
        
        for (MultipartFile file : files) {
            try {
                UploadRequest.FilePart filePart = UploadRequest.FilePart.builder()
                        .originalFilename(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .content(file.getBytes())
                        .size(file.getSize())
                        .build();
                fileParts.add(filePart);
            } catch (Exception e) {
                throw new RuntimeException("Failed to process file: " + file.getOriginalFilename(), e);
            }
        }
        
        return UploadRequest.builder()
                .files(fileParts)
                .promptDescription(promptDescription)
                .timestamp(LocalDateTime.now())
                .requestId(UUID.randomUUID().toString())
                .build();
    }
    
    public UploadResponseDto toResponseDto(UploadRequest uploadRequest) {
        return UploadResponseDto.builder()
                .message("Request accepted for processing")
                .timestamp(uploadRequest.getTimestamp())
                .requestId(uploadRequest.getRequestId())
                .build();
    }
}
