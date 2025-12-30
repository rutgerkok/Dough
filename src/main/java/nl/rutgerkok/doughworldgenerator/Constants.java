package nl.rutgerkok.doughworldgenerator;

import net.kyori.adventure.text.format.TextColor;

import java.util.Objects;

public class Constants {

    public static final String GENERATED_DATAPACK_NAME = "generated_datapack_do_not_edit";
    public static final String VANILLA_DATAPACKS_FOLDER = "vanilla_datapacks_do_not_edit";

    public static final String WORLD_CONFIG_FILE_NAME = "world_config.yml";
    public static final String INTERNAL_CONFIG_FILE_NAME = "internal_config_do_not_edit.yml";

    /**
     * Color used for error messages in the chat.
     */
    public static final TextColor ERROR_COLOR = Objects.requireNonNull(TextColor.fromHexString("#6c5ce7"));

    /**
     * Color used for success messages in the chat.
     */
    public static final TextColor SUCCESS_COLOR = Objects.requireNonNull(TextColor.fromHexString("#55efc4"));
}
