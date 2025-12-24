# Dough

Allows you to change the shape of your world in Minecraft. Change where and how much oceans, mountains, and valleys are generated.

The plugin works by using a YAML configuration file. From that configuration file, together with Minecraft's default world generation datapack (which this plugin automatically extracts), it generates a custom world generation datapack. This datapack is then enabled automatically.


## Modifying biome locations
Minecraft runs five noise functions to determine biome locations: temperature, humidity, continentalness, erosion, and weirdness. Each location in the world is assigned a value for each of these five noise functions, depending on the world seed. Mojang also assigned each biome in the game a preferred noise value (so for temperature, humidity, etc.). For each location in the world, the closest-matching biome is chosen to be generated there.

In theory, you can edit the "data/minecraft/worldgen/world_preset/normal.json" file in a Minecraft datapack to change where biomes are located.
However, the first issue is that the file normally just contains `"preset": "minecraft:overworld"`. To edit this preset, you need to run the [Minecraft Data Generators](https://minecraft.wiki/w/Minecraft_Wiki:Projects/wiki.vg_merge/Data_Generators), extract the generated biome locations, and insert them into the file. A second issue is that biomes have more than one set of preferred noise values. At the time of writing, there are 7593 different preferred locations defined for the 54 overworld biomes in the game. Editing all of these by hand is impractical.

This plugin takes a different approach: you specify how much you want to shift the noise functions, and the plugin automatically adjusts all the preferred biome locations accordingly. For example, if you increase the "continentalness" value, oceans will require more negative continentalness to generate, and thus oceans will shrink in size. All the steps of extracting the default biome locations, modifying them, and inserting them back into the datapack are handled automatically by the plugin.

Modifying the preferred biome locations is not enough though, as the heightmap of the world would still be the same. Thus, the plugin also modifies the so-called density functions to change the heightmap of the world to match the new biome locations.
