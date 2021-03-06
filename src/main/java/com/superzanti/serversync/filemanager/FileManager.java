package com.superzanti.serversync.filemanager;

import com.superzanti.serversync.server.Function;
import com.superzanti.serversync.util.FileHash;
import com.superzanti.serversync.util.Logger;
import com.superzanti.serversync.util.PathBuilder;
import com.superzanti.serversync.util.PathUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileManager {
    public static final String clientOnlyFilesDirectoryName = "clientmods";

    public final Path rootPath;
    public final Path clientOnlyFilesDirectory;
    public final Path logsDirectory;

    public FileManager() {
        String root = PathUtils.getMinecraftDirectory();

        if (root == null) {
            root = "";
        }
        Logger.debug(String.format("root dir: %s", Paths.get(root).toAbsolutePath().toString()));
        rootPath = Paths.get(root);

        clientOnlyFilesDirectory = new PathBuilder(root).add(FileManager.clientOnlyFilesDirectoryName).buildPath();
        logsDirectory = new PathBuilder(root).add("logs").buildPath();
    }

    // New version of sync process

    /**
     * Get all of the **files** present in the list of directories as a diffable map for file comparison.
     *
     * @param includedDirectories The list of directories to search
     * @return The files contained in the directories
     * @throws IOException when a configured directory is not a directory or does not exist.
     */
    public Map<String, String> getDiffableFilesFromDirectories(List<String> includedDirectories) throws IOException {
        // Check for invalid directory configuration
        List<Path> dirs = new ArrayList<>();
        for (String includedDirectory : includedDirectories) {
            Path dir = Paths.get(includedDirectory);
            if (!Files.exists(dir)) {
                Logger.debug(String.format("Configured directory: %s does not exist.", dir));
                throw new IOException("File does not exist");
            }

            if (!Files.isDirectory(dir)) {
                Logger.debug(String.format("Configured directory: %s is not a directory.", dir));
                throw new IOException("File is not a directory");
            }

            // Might as well build the list during this check
            dirs.add(dir);
        }

        List<Path> allFiles = dirs.stream().flatMap(dir -> {
            try {
                return Files.walk(dir).filter(dirPath -> !Files.isDirectory(dirPath));
            } catch (IOException e) {
                Logger.debug(String.format("Failed to access files in the directory: %s", dir));
                Logger.debug(e);
            }
            return null;
        }).collect(Collectors.toList());

        if (allFiles.stream().anyMatch(Objects::isNull)) {
            throw new IOException("Some files could not be accessed");
        }

        //TODO add file size to this map
        return allFiles.stream().collect(Collectors.toMap(Path::toString, FileHash::hashFile));
    }

    public static void removeEmptyDirectories(List<Path> directories, Function<Path> emptyDirectoryCallback) {
        directories.forEach(path -> {
            try {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        if (exc == null) {
                            try {
                                Files.delete(dir);

                                if (emptyDirectoryCallback != null) {
                                    emptyDirectoryCallback.f(dir);
                                }
                            } catch (DirectoryNotEmptyException dne) {
                                // expected
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                Logger.debug(e);
            }
        });
    }
}
