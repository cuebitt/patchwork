package cuebitt.trowelkey.client;

/**
 * How the block filter in {@link TrowelConfig} is applied to random placement.
 *
 * <p>{@code NONE} disables filtering, {@code BLACKLIST} excludes the listed blocks, and
 * {@code WHITELIST} restricts placement to only the listed blocks.
 */
public enum BlockFilterMode {
    NONE,
    BLACKLIST,
    WHITELIST
}
