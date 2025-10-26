package br.com.gerasaber.usecase.webhook.mapping;

import br.com.gerasaber.domain.entity.WebhookResponse;
import br.com.gerasaber.usecase.webhook.contract.WebhookResponseDto;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between domain objects and DTOs in the webhook use case.
 */
@Component
public class WebhookResponseMapper {
    
    public WebhookResponseDto toResponseDto(WebhookResponse webhookResponse) {
        return WebhookResponseDto.builder()
                .message("PDF successfully stored")
                .path(webhookResponse.getSavedPath().toString())
                .filename(webhookResponse.getFilename())
                .build();
    }
}
