package io.github.racoondog.tokyo.utils;

import io.github.racoondog.tokyo.Tokyo;
import meteordevelopment.meteorclient.utils.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public final class ExportUtils {
    public static final Path EXPORT_FOLDER = FileUtils.TOKYO_FOLDER_LOCAL.resolve("export");
    public static final Supplier<String> WORLDNAME = Utils::getWorldName;
    public static final Supplier<String> DATETIME = Util::getFormattedCurrentTime;

    public static Path computePath(String fileName, @Nullable String fileExtension) {
        return computePath(fileName, fileExtension, Mode.OVERWRITE);
    }

    public static Path computePath(String fileName, @Nullable String fileExtension, Mode mode) {
        if (fileExtension == null) fileExtension = "";

        Path p = EXPORT_FOLDER;

        String[] tokens = splitSeparator(fileName);
        if (tokens != null) {
            for (int i = 0; i < tokens.length - 1; i++) {
                p = p.resolve(tokens[i]);
            }
            fileName = tokens[tokens.length - 1];
        }

        Path file = p.resolve(fileName + fileExtension);

        if (mode == Mode.KEEP) {
            int i = 0;
            while (Files.exists(file)) {
                i++;
                file = p.resolve(fileName + "_" + i + fileExtension);
            }
        }

        return file;
    }

    public static void export(String content, String fileName, String fileExtension) {
        export(content, fileName, fileExtension, Mode.OVERWRITE);
    }

    public static void export(String content, String fileName, String fileExtension, Mode mode) {
        if (content.isEmpty()) Tokyo.LOG.warn("ExportUtils.export file content is empty!");
        if (fileName.isEmpty()) Tokyo.LOG.warn("ExportUtils.export file name is empty!");

        Path file = computePath(fileName, fileExtension, mode);
        Path dir = file.getParent();

        try {
            if (!Files.isDirectory(dir)) Files.createDirectories(dir);

            Files.writeString(file, content, StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private static String[] splitSeparator(String fileName) {
        if (fileName.contains("/")) return fileName.split("/");
        if (fileName.contains("\\\\")) return fileName.split("\\\\");
        if (fileName.contains(File.separator)) return fileName.split(File.separator);
        return null;
    }

    public enum Mode {
        OVERWRITE,
        KEEP
    }
}
