package cuebitt.patchwork.client;

/**
 * Placement source for a trowel.
 *
 * <p>{@code HOTBAR} restricts random block selection to the nine hotbar slots, while {@code
 * INVENTORY} draws from the entire player inventory. The mode is toggled per-trowel with Shift +
 * Right Click.
 */
public enum TrowelMode {
  HOTBAR,
  INVENTORY
}
