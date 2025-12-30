package nl.rutgerkok.doughworldgenerator.mapitem;

import org.bukkit.map.MapView;

public interface MapViewProvider {

    /**
     * Gets a map view for use by the plugin. If no map view has been created before, a new one
     * should be created, and stored for future calls.
     * @return The map view.
     */
    MapView getMapView();

    /**
     * Checks whether the given map ID belongs to a map created by this plugin.
     * @param id The map ID.
     * @return True if the map ID belongs to a map created by this plugin.
     */
    boolean isOurMapId(int id);
}
