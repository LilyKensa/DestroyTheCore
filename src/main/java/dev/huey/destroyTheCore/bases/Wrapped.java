package dev.huey.destroyTheCore.bases;

/**
 * !!! EXPERIMENT IDEA !!!<br>
 * Wrapped all the things, so the syntax will be cleaner
 * <p>
 * For example:
 * <pre>
 * Player pl = somewhere.getPlayer();
 * PlayerData data = DTC.game.getPlayerData(pl);
 * somewhere.doThings(data.side);</pre>
 * can be changed to:
 * <pre>
 * WPlayer pl = somewhere.getPlayer();
 * somewhere.doThings(pl.data.side);</pre>
 * This requires changing almost everything, so it won't be done in any recent point
 */
public abstract class Wrapped<W> {
  public W w;
}
