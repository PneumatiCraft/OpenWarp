package com.lithium3141.OpenWarp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.lithium3141.OpenWarp.util.LocationUtil;

/**
 * Representation of a single warp, either public or private. Maintains an
 * internal record of its name, player owner name, and target location.
 * <p>
 * Warps can be instantiated directly using one of two methods: either by
 * specifying all relevant information in the constructor or by providing
 * a warp name and ConfigurationNode object from which to pull location and
 * owner data.
 * <p>
 * Warps receive a designation as public or private by inclusion in the
 * relevant warp map in the plugin passed in the warp's constructor; as such,
 * it is possible for a warp to be neither private nor public (or both).
 * <p>
 * A warp can be specified as a Multiverse destination using the syntax
 * <code>ow:NAME</code>.
 */
public class Warp {
    /**
     * The plugin instance against which this Warp queries for public/private status. The
     * plugin is also used to locate World instances and other Bukkit entities that may
     * be necessary.
     */
    protected OpenWarp plugin;

    /**
     * The name of this Warp.
     */
    protected String name;

    /**
     * The target Location of this Warp.
     */
    protected Location location;

    /**
     * The name of the owner of this Warp. Stored as a String since the owning Player
     * may not be logged in at any given time; this is especially a concern for public
     * warps.
     */
    protected String owner;

    /**
     * The names of players invited to this Warp. Only truly applicable for private
     * warps.
     */
    protected List<String> invitees;

    /**
     * Configuration key for the World component of this Warp's Location.
     */
    public static final String WORLD_KEY = "world";

    /**
     * Configuration key for the x-component of this Warp's Location.
     */
    public static final String X_KEY = "x";

    /**
     * Configuration key for the y-component of this Warp's Location.
     */
    public static final String Y_KEY = "y";

    /**
     * Configuration key for the z-component of this Warp's Location.
     */
    public static final String Z_KEY = "z";

    /**
     * Configuration key for the pitch component of this Warp's Location.
     */
    public static final String PITCH_KEY = "pitch";

    /**
     * Configuration key for the yaw component of this Warp's Location.
     */
    public static final String YAW_KEY = "yaw";

    /**
     * Configuration key for the owner name of this Warp.
     */
    public static final String OWNER_KEY = "owner";

    /**
     * Configuration key for the invitee list of this Warp.
     */
    public static final String INVITEES_KEY = "invitees";

    /**
     * Create a new Warp with the given name, deriving Location data from the given
     * ConfigurationNode. Will read configuration information using the static keys
     * defined in this class from the node; expects to find a complete Location as
     * well as an owner string.
     *
     * @param ow The OpenWarp instance to use in public/private queries.
     * @param warpName The name of this Warp.
     * @param node The ConfigurationNode from which to pull Location information.
     */
    public Warp(OpenWarp ow, String warpName, ConfigurationNode node) {
        this.plugin = ow;
        this.name = warpName;

        this.parseConfiguration(node);
    }

    /**
     * Create a new warp with the given name, Location, and owner. Does no validation
     * on whether the given Location or owner string are valid.
     *
     * @param ow The OpenWarp instance to use in public/private queries.
     * @param warpName The name of this Warp.
     * @param warpLoc The Location destination of this Warp.
     * @param warpOwner The name of the Player owner of this Warp.
     */
    public Warp(OpenWarp ow, String warpName, Location warpLoc, String warpOwner) {
        this.plugin = ow;
        this.name = warpName;
        this.location = warpLoc;
        this.owner = warpOwner;
        this.invitees = new ArrayList<String>();
    }

    /**
     * Read Location and owner information from the given ConfigurationNode. Does basic
     * validation on the World named in the node. Uses the static keys defined in this
     * class for information retrieval from the node.
     *
     * On completion, this Warp will be populated with the information retrieved from
     * the node, if that information is valid.
     *
     * @param node The ConfigurationNode from which to read information.
     */
    private void parseConfiguration(ConfigurationNode node) {
        String worldName = node.getString(WORLD_KEY);
        if (worldName == null) {
            OpenWarp.LOG.severe(OpenWarp.LOG_PREFIX + "Malformed warp in configuration: no world for warp " + this.name);
        }

        double x = node.getDouble(X_KEY, 0.0);
        double y = node.getDouble(Y_KEY, 0.0);
        double z = node.getDouble(Z_KEY, 0.0);
        float pitch = (float) node.getDouble(PITCH_KEY, 0.0);
        float yaw = (float) node.getDouble(YAW_KEY, 0.0);

        World world = null;
        if (worldName != null) {
            world = this.plugin.getServer().getWorld(worldName);
            if (world == null) {
                OpenWarp.LOG.severe(OpenWarp.LOG_PREFIX + "Couldn't locate world named '" + worldName + "'; this is likely a problem");
            }
        }
        this.location = new Location(world, x, y, z, yaw, pitch);

        this.owner = node.getString(OWNER_KEY, "");
        this.invitees = node.getStringList(INVITEES_KEY, new ArrayList<String>());
    }

    /**
     * Get the name of this Warp.
     *
     * @return The name of this Warp.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the destination Location of this Warp.
     *
     * @return The destination Location of this Warp.
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * Check if this Warp is a public warp. A public warp is available to all
     * players with the relevant permissions currently on the server. A Warp is
     * considered public if it exists in the value set of the OpenWarp plugin
     * instance's map of public warps.
     *
     * @return True if this warp is public; false otherwise.
     */
    public boolean isPublic() {
        return this.plugin.getPublicWarps().values().contains(this);
    }

    /**
     * Check if this Warp is a private warp. A private warp is only available to
     * its owner (the Player with the same name as this Warp's owner string). A Warp
     * is considered private if it exists in the value set of the OpenWarp plugin
     * instance's map of private warps for its owner string.
     *
     * @return True if this warp is private; false otherwise.
     */
    public boolean isPrivate() {
        return this.plugin.getPrivateWarps().get(this.getOwner()).values().contains(this);
    }

    /**
     * Get information about this Warp in a readable format.
     *
     * @return A String detailing this Warp's information.
     */
    public String getDetailString() {
        return LocationUtil.getHumanReadableString(this.location, 2);
    }

    /**
     * Get the name of the Player owner of this Warp.
     *
     * @return The name of this Warp's owner.
     */
    public String getOwner() {
        return this.owner;
    }

    /**
     * Get the list of people invited to this Warp. Only applicable for private warps;
     * regardless of return value, a public warp will be accessible to players.
     *
     * @return A List of player names who may access this Warp through an invite.
     */
    public List<String> getInvitees() {
        return this.invitees;
    }

    /**
     * Check whether the given Player is invited to this Warp. Calls #isInvited(String)
     * internally. Only applicable for private warps; regardless of return value, a
     * public warp will be accessible to the provided Player.
     *
     * @param player The Player to check for invite status.
     * @return True if the Player is invited to this Warp; false otherwise.
     */
    public boolean isInvited(Player player) {
        return this.isInvited(player.getName());
    }

    /**
     * Check whether the given player is invited to this Warp. Only applicable for
     * private warps; regardless of return value, a public warp will be accessible to
     * the provided player.
     *
     * @param playerName The player to check for invite status.
     * @return True if the player is invited to this Warp; false otherwise.
     */
    public boolean isInvited(String playerName) {
        return this.invitees.contains(playerName);
    }

    /**
     * Invite the given Player to this Warp. Only applicable for private warps. Calls
     * #addInvitee(String) internally.
     *
     * @param player The Player to invite to this Warp.
     */
    public void addInvitee(Player player) {
        this.addInvitee(player.getName());
    }

    /**
     * Invite the given player to this Warp. Only applicable for private warps.
     *
     * @param playerName The player to invite to this Warp.
     */
    public void addInvitee(String playerName) {
        if (!this.invitees.contains(playerName)) {
            this.invitees.add(playerName);
        }
    }

    /**
     * Uninvite the given Player from this Warp. Only applicable for private warps.
     * Calls #removeInvitee(String) internally.
     *
     * @param player The Player to uninvite from this Warp.
     */
    public void removeInvitee(Player player) {
        this.removeInvitee(player.getName());
    }

    /**
     * Uninvite the given player from this Warp. Only applicable for private warps.
     *
     * @param playerName The player to uninvite from this Warp.
     */
    public void removeInvitee(String playerName) {
        this.invitees.remove(playerName);
    }

    /**
     * Get a Map suitable for writing to a configuration file that represents
     * this Warp. The map is populated using the static keys defined in this
     * class, and all values are cast to Object so that the map may be passed
     * to the Configuration set of classes and written to YAML directly.
     *
     * @return A Map containing the details of this Warp in a format suitable
     * for writing to a Configuration YAML file.
     */
    public Map<String, Object> getConfigurationMap() {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(X_KEY, this.location.getX());
        result.put(Y_KEY, this.location.getY());
        result.put(Z_KEY, this.location.getZ());
        result.put(PITCH_KEY, this.location.getPitch());
        result.put(YAW_KEY, this.location.getYaw());

        if (this.location.getWorld() == null) {
            OpenWarp.LOG.severe(OpenWarp.LOG_PREFIX + "Saving warp with no loaded target world! Please check your configuration.");
            // Used to be marked as bug #22 (see https://github.com/PneumatiCraft/OpenWarp/issues/22)
            // In fixes for #47, removing bug marker to handle null-world condition
        } else {
            result.put(WORLD_KEY, this.location.getWorld().getName());
        }

        result.put(OWNER_KEY, this.owner);
        result.put(INVITEES_KEY, this.invitees);

        return result;
    }

    /**
     * Check whether to use the Multiverse safe teleporter when moving to this
     * Warp as a Multiverse destination.
     *
     * @return True.
     */
    public boolean useSafeTeleporter() {
        // TODO Add methods to set this to false when necessary.
        return true;
    }
}
