package nl.rutgerkok.doughworldgenerator.generator;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList.Preset;
import nl.rutgerkok.doughworldgenerator.config.Formula;
import nl.rutgerkok.doughworldgenerator.config.WorldConfig;
import org.bukkit.Server;
import org.bukkit.craftbukkit.CraftServer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BiomeSourcePresetInjector {

    /**
     * Minecraft provides two ways to modify the biome parameter points: via datapacks, or by selecting a preset.
     * Unfortunately, the datapack way no longer works, as it needs to modify over 6000 parameter points, which results
     * in a world_gen_settings.dat file larger than the maximum size. So we need to modify the preset instead. This
     * is a quite hacky unfortunately.
     * @param server The server instance. Make sure to call the method before there are worlds loaded.
     * @param config The modifications we need to make.
     */
    public void inject(Server server, WorldConfig config) {
        DedicatedServer dedicatedServer = ((CraftServer) server).getServer();

        // Get hold of the overwold biome source preset
        ResourceKey<MultiNoiseBiomeSourceParameterList> overworldBiomePresetResource =
                ResourceKey.create(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, Identifier.withDefaultNamespace("overworld"));
        MultiNoiseBiomeSourceParameterList overworldBiomePreset = dedicatedServer.registryAccess().getOrThrow(overworldBiomePresetResource).value();

        // Recreate the biome location parameters with modified values
        List<Pair<Climate.ParameterPoint, Holder<Biome>>> modifiedBiomes = new ArrayList<>();
        for (Pair<Climate.ParameterPoint, Holder<Biome>> oldPointPair : overworldBiomePreset.parameters().values()) {
            Climate.ParameterPoint oldPoint = oldPointPair.getFirst();
            Holder<Biome> biome = oldPointPair.getSecond();

            Climate.ParameterPoint newPoint = new Climate.ParameterPoint(
                    modify(oldPoint.temperature(), config.temperature),
                    modify(oldPoint.humidity(), config.humidity),
                    modify(oldPoint.continentalness(), config.continentalness),
                    modify(oldPoint.erosion(), config.erosion),
                    oldPoint.depth(),
                    modify(oldPoint.weirdness(), config.weirdness),
                    oldPoint.offset()
            );
            modifiedBiomes.add(Pair.of(newPoint, biome));
        }
        Climate.ParameterList<Holder<Biome>> modifiedParameterList = new Climate.ParameterList<>(modifiedBiomes);

        // Inject the modified parameter list back into the preset using reflection
        try {
            Field parametersField = MultiNoiseBiomeSourceParameterList.class.getDeclaredField("parameters");
            parametersField.setAccessible(true);
            parametersField.set(overworldBiomePreset, modifiedParameterList);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Climate.Parameter modify(Climate.Parameter parameter, Formula formula) {
        float minCoord = Climate.unquantizeCoord(parameter.min());
        float maxCoord = Climate.unquantizeCoord(parameter.max());

        float modifiedMinCoord = formula.evaluate(minCoord);
        float modifiedMaxCoord = formula.evaluate(maxCoord);

        return new Climate.Parameter(Climate.quantizeCoord(modifiedMinCoord), Climate.quantizeCoord(modifiedMaxCoord));
    }


}
