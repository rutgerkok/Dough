package nl.rutgerkok.doughworldgenerator;

import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Extracts the vanilla datapack by invoking Minecraft's data generator.
 */
final class VanillaDatapackExtractor {

    private final PluginLogger logger;
    private final Path targetFolder;

    public VanillaDatapackExtractor(PluginLogger logger, Path targetFolder) {
        this.logger = logger;
        this.targetFolder = targetFolder;
    }

    /**
     * Extracts the vanilla datapack if necessary.
     *
     * @return True if the datapack is present or was successfully extracted, false if extraction failed.
     */
    public boolean extractIfNecessary() {
        if (!Files.exists(targetFolder)) {
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
        Files.createDirectories(this.targetFolder);
        String[] args = new String[]{
                "--server",
                "--reports",
                "--output", this.targetFolder.toAbsolutePath().toString()
        };
        main.invoke(null, (Object) args);

        cleanupGeneratedFolder();

        logger.info("Generated vanilla datapack at " + this.targetFolder + ". Unfortunately, we will now have to shut" +
                " down the server, as we can't generate chunks in this state. Please restart!");
        // You would get a crash in ChunkGeneratorStructureState for whatever reason
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            // Ignore
        }
        Runtime.getRuntime().halt(0);
    }

    private void cleanupGeneratedFolder() throws IOException {
        // Delete all folders from the data/minecraft subfolder except for the "worldgen" folder
        Path minecraftDataFolder = this.targetFolder.resolve("data").resolve("minecraft");
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
        MoreFiles.deleteRecursively(this.targetFolder.resolve(".cache"), RecursiveDeleteOption.ALLOW_INSECURE);

        // From the reports folder, delete all loose files
        Path reportsFolder = this.targetFolder.resolve("reports");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(reportsFolder)) {
            for (Path subpath : stream) {
                if (Files.isDirectory(subpath)) {
                    continue;
                }
                Files.delete(subpath);
            }
        }
    }


}
