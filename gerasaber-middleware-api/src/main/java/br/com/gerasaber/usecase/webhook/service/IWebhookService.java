package br.com.gerasaber.usecase.webhook.service;

import br.com.gerasaber.domain.entity.WebhookResponse;

/**
 * Interface for webhook service following Clean Architecture principles.
 */
public interface IWebhookService {
    
    /**
     * Processes a webhook response from n8n containing a generated PDF file.
     * 
     * @param pdfContent the PDF file content
     * @param filename the filename of the PDF
     * @param requestId the original request ID
     * @return the processed webhook response with storage information
     */
    WebhookResponse processWebhookResponse(byte[] pdfContent, String filename, String requestId);
}
