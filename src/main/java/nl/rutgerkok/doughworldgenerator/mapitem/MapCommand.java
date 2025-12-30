package nl.rutgerkok.doughworldgenerator.mapitem;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MapId;
import nl.rutgerkok.doughworldgenerator.Constants;
import nl.rutgerkok.doughworldgenerator.DoughMain;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Predicate;

import static net.kyori.adventure.text.Component.text;

public final class MapCommand implements Command<CommandSourceStack>, Predicate<CommandSourceStack> {


    public static ArgumentBuilder<CommandSourceStack, ?> command() {
        MapCommand mapCommand = new MapCommand();
        return Commands.literal("map").executes(mapCommand).requires(mapCommand);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> commandContext) {
        CommandSourceStack sourceStack = commandContext.getSource();
        CommandSender source = sourceStack.getSender();
        if (!source.hasPermission("dough.map")) {
            source.sendMessage(text("You do not have permission to use this command.", Constants.ERROR_COLOR));
            return 0;
        }
        if (!(sourceStack.getExecutor() instanceof Player player)) {
            source.sendMessage(text("This command can only be run by a player.", Constants.ERROR_COLOR));
            return 0;
        }

        // Not very nice to use a static method here, but since commands need to be registered before the plugin
        // exists, there is no better way currently. We might want to build a little command framework later.
        DoughMain doughMain = JavaPlugin.getPlugin(DoughMain.class);
        MapView map = doughMain.getAutoUpdateableMap();

        giveMapAsItem(player, map);
        player.sendMessage(text("Given you a biome map!", Constants.SUCCESS_COLOR));

        return Command.SINGLE_SUCCESS;
    }


    private static void giveMapAsItem(Player player, MapView map) {
        ItemStack mapItem = ItemStack.of(Material.FILLED_MAP);
        mapItem.setData(DataComponentTypes.MAP_ID, MapId.mapId(map.getId()));
        for (ItemStack overflow : player.getInventory().addItem(mapItem).values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), overflow);
        }
    }

    @Override
    public boolean test(CommandSourceStack commandSourceStack) {
        return true;//commandSourceStack.getSender().hasPermission("dough.map");
    }
}
