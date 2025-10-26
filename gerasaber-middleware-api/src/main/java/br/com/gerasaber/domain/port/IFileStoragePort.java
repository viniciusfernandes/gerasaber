package br.com.gerasaber.domain.port;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Port interface for file storage operations following Clean Architecture principles.
 * This abstraction allows the application to be independent of storage implementation details.
 */
public interface IFileStoragePort {
    
    /**
     * Saves a file with the given filename and content.
     * 
     * @param filename the name of the file to save
     * @param content the file content as byte array
     * @return the path where the file was saved
     */
    Path saveFile(String filename, byte[] content);
    
    /**
     * Finds a file by its filename.
     * 
     * @param filename the name of the file to find
     * @return Optional containing the file path if found, empty otherwise
     */
    Optional<Path> findFile(String filename);
    
    /**
     * Saves a file with timestamp-based directory structure.
     * 
     * @param filename the name of the file to save
     * @param content the file content as byte array
     * @return the path where the file was saved
     */
    Path saveFileWithTimestamp(String filename, byte[] content);
}
