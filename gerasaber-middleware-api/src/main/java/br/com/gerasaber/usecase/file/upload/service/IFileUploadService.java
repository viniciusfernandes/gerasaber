package br.com.gerasaber.usecase.file.upload.service;

import br.com.gerasaber.domain.entity.UploadRequest;

/**
 * Interface for file upload service following Clean Architecture principles.
 */
public interface IFileUploadService {
    
    /**
     * Processes an upload request by forwarding it to the n8n workflow.
     * 
     * @param uploadRequest the upload request containing files and prompt description
     */
    void processUploadRequest(UploadRequest uploadRequest);
}
