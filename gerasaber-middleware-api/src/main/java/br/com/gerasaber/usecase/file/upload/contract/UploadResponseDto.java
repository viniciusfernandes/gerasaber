package br.com.gerasaber.usecase.file.upload.contract;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for upload response following the standard API response format.
 */
@Data
@Builder
public class UploadResponseDto {
    
    private String message;
    private LocalDateTime timestamp;
    private String requestId;
}
