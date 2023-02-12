package io.github.racoondog.tokyo.utils;

import meteordevelopment.meteorclient.MeteorClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class FileUtils {
    public static final Path TOKYO_FOLDER_LOCAL = MeteorClient.FOLDER.toPath().resolve("tokyo-client");
    public static final Path TOKYO_FOLDER_GLOBAL = Path.of(System.getProperty("user.home")).resolve("tokyo-client");

    public static Path getTokyoFile(String fileName) {
        return TOKYO_FOLDER_LOCAL.resolve(fileName);
    }

    public static List<String> getLines(String fileName) {
        return getLines(getTokyoFile(fileName));
    }

    public static List<String> getLines(Path path) {
        try {
            return Files.readAllLines(path);
        } catch (IOException e) {
            return List.of();
        }
    }

    public static void ensureDirectoryExists(Path filePath) {
        Path directoryPath = filePath.getParent();
        try {
            Files.createDirectories(directoryPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
