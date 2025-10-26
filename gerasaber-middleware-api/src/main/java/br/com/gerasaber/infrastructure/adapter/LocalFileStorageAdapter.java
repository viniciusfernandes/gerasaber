package br.com.gerasaber.infrastructure.adapter;

import br.com.gerasaber.domain.port.IFileStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

/**
 * Local file system implementation of the file storage port.
 * Stores files in the configured local directory with timestamp-based organization.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LocalFileStorageAdapter implements IFileStoragePort {
    
    @Value("${storage.local-path:/var/app/files}")
    private String baseStoragePath;
    
    @Value("${storage.temp-path:/tmp/middleware/files}")
    private String tempStoragePath;
    
    @Override
    public Path saveFile(String filename, byte[] content) {
        try {
            Path basePath = Paths.get(baseStoragePath);
            ensureDirectoryExists(basePath);
            
            Path filePath = basePath.resolve(filename);
            Files.write(filePath, content);
            
            log.info("File saved successfully: {}", filePath);
            return filePath;
        } catch (IOException e) {
            log.error("Failed to save file: {}", filename, e);
            throw new RuntimeException("Failed to save file: " + filename, e);
        }
    }
    
    @Override
    public Optional<Path> findFile(String filename) {
        try {
            Path basePath = Paths.get(baseStoragePath);
            Path filePath = basePath.resolve(filename);
            
            if (Files.exists(filePath)) {
                return Optional.of(filePath);
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("Failed to find file: {}", filename, e);
            return Optional.empty();
        }
    }
    
    @Override
    public Path saveFileWithTimestamp(String filename, byte[] content) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Path basePath = Paths.get(baseStoragePath, timestamp);
            ensureDirectoryExists(basePath);
            
            // Generate unique filename to avoid conflicts
            String uniqueFilename = generateUniqueFilename(filename);
            Path filePath = basePath.resolve(uniqueFilename);
            
            Files.write(filePath, content);
            
            log.info("File saved with timestamp: {}", filePath);
            return filePath;
        } catch (IOException e) {
            log.error("Failed to save file with timestamp: {}", filename, e);
            throw new RuntimeException("Failed to save file with timestamp: " + filename, e);
        }
    }
    
    private void ensureDirectoryExists(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            log.debug("Created directory: {}", path);
        }
    }
    
    private String generateUniqueFilename(String originalFilename) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            String name = originalFilename.substring(0, lastDotIndex);
            String extension = originalFilename.substring(lastDotIndex);
            return String.format("%s-%s-%s%s", name, timestamp, uuid, extension);
        } else {
            return String.format("%s-%s-%s", originalFilename, timestamp, uuid);
        }
    }
}
