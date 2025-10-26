package br.com.gerasaber.usecase.webhook.contract;

import lombok.Builder;
import lombok.Data;

/**
 * DTO for webhook response following the standard API response format.
 */
@Data
@Builder
public class WebhookResponseDto {
    
    private String message;
    private String path;
    private String filename;
}
