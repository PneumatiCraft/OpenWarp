package com.lithium3141.OpenWarp.util;

import org.bukkit.Location;

/**
 * General utilities for working with Location instances.
 */
public class LocationUtil {
    /**
     * Protected constructor so as not to instantiate utility classes.
     */
    protected LocationUtil() {
        super();
    }

    /**
     * Format a location into a human-readable, precision-limited string.
     *
     * @param loc The Location instance to format.
     * @param precision The number of decimal places to include in the formatted string.
     * @return A human-readable String containing information about the given Location.
     */
    public static String getHumanReadableString(Location loc, int precision) {
        String fmtString = String.format("(%%.%df, %%.%df, %%.%df) in world '%%s'", precision, precision, precision);
        return String.format(fmtString, loc.getX(), loc.getY(), loc.getZ(), loc.getWorld().getName());
    }
}
