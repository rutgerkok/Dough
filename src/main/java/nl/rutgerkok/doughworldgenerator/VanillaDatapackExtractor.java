package nl.rutgerkok.doughworldgenerator;

import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Extracts the vanilla datapack by invoking Minecraft's data generator.
 */
final class VanillaDatapackExtractor {

    private static final String VERSION_INFO_YML = "version-info.yml";

    private final PluginLogger logger;
    private final Path targetFolder;
    private final Path versionSpecificFolder;
    private final String minecraftVersion;

    public static @Nullable Path getVersionSpecificFolder(Path targetFolder) {
        File configFile = targetFolder.resolve(VERSION_INFO_YML).toFile();
        YamlConfiguration storageForMinecraftVersion = YamlConfiguration.loadConfiguration(configFile);
        String minecraftVersion = storageForMinecraftVersion.getString("minecraft_version");
        if (minecraftVersion == null) {
            return null;
        }
        return targetFolder.resolve(minecraftVersion);
    }

    public VanillaDatapackExtractor(PluginLogger logger, Path targetFolder, String minecraftVersion) {
        this.logger = logger;
        this.targetFolder = targetFolder;
        this.minecraftVersion = minecraftVersion;
        this.versionSpecificFolder = targetFolder.resolve(minecraftVersion);
    }

    private void cleanupGeneratedFolder() throws IOException {
        // Delete all folders from the data/minecraft subfolder except for the "worldgen" folder
        Path minecraftDataFolder = this.versionSpecificFolder.resolve("data").resolve("minecraft");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(minecraftDataFolder)) {
            for (Path subfolder : stream) {
                if (!subfolder.getFileName().toString().equals("worldgen")) {
                    MoreFiles.deleteRecursively(subfolder, RecursiveDeleteOption.ALLOW_INSECURE);
                    // We allow insecure deletes (= not checking symlinks to outside) because:
                    // - we just created these files ourselves, so we know there are no symlinks in there
                    // - otherwise deletion fails on Windows, since we cannot always query symlink status there
                }
            }
        }

        // Also delete the .cache folder, contains nothing useful for us
        MoreFiles.deleteRecursively(this.versionSpecificFolder.resolve(".cache"), RecursiveDeleteOption.ALLOW_INSECURE);

        // From the reports folder, delete all loose files
        Path reportsFolder = this.versionSpecificFolder.resolve("reports");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(reportsFolder)) {
            for (Path subpath : stream) {
                if (Files.isDirectory(subpath)) {
                    continue;
                }
                Files.delete(subpath);
            }
        }
    }

    /**
     * Extracts the vanilla datapack if necessary.
     *
     * @return True if the datapack is present or was successfully extracted, false if extraction failed.
     */
    public boolean extractIfNecessary() {
        // Make sure this is always up to date with what the plugin is actually trying to read
        storeMinecraftVersion(logger, minecraftVersion);

        if (!Files.exists(versionSpecificFolder)) {
            try {
                generateVanillaDatapack();
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException | IOException | RuntimeException e) {
                logger.severe("Could not generate vanilla datapack:", e);
            }
            return false;
        }
        return true;
    }

    private void generateVanillaDatapack() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        logger.info("Generating vanilla datapack, hold on...");
        Class<?> minecraftDataClass = Class.forName("net.minecraft.data.Main");
        Method main = minecraftDataClass.getMethod("main", String[].class);
        Files.createDirectories(this.versionSpecificFolder);
        String[] args = new String[]{
                "--server",
                "--reports",
                "--output", this.versionSpecificFolder.toAbsolutePath().toString()
        };
        main.invoke(null, (Object) args);

        cleanupGeneratedFolder();

        logger.info("Generated vanilla datapack at " + this.versionSpecificFolder + ". Unfortunately, we will now have to shut" +
                " down the server, as we can't generate chunks in this state. Please restart!");
        // You would get a crash in ChunkGeneratorStructureState for whatever reason
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            // Ignore
        }
        Runtime.getRuntime().halt(0);
    }

    private void storeMinecraftVersion(PluginLogger logger, String minecraftVersion) {
        YamlConfiguration storageForMinecraftVersion = new YamlConfiguration();
        storageForMinecraftVersion.set("minecraft_version", minecraftVersion);
        storageForMinecraftVersion.setComments("minecraft_version", List.of(
                "The Minecraft version for which the vanilla datapack was generated. ",
                "The plugin cannot determine the Minecraft version at startup, so it reads it from here. ",
                "Later in the startup process, this version is updated to the correct version.")
        );
        try {
            Files.createDirectories(this.targetFolder);
            storageForMinecraftVersion.save(this.targetFolder.resolve(VERSION_INFO_YML).toFile());
        } catch (IOException e) {
            logger.severe("Could not save version info for vanilla datapack:", e);
        }
    }


}
