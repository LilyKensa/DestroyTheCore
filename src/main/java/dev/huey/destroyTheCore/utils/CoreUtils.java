package dev.huey.destroyTheCore.utils;

import dev.huey.destroyTheCore.DestroyTheCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class CoreUtils {
  /** if {@code value} is null, return {@code defValue} */
  static public <T> T def(Object value, T defValue) {
    return value == null ? defValue : (T) value;
  }
  
  /** Fix numbers to specific after point */
  static public String toFixed(double value, int pow) {
    return String.format("%." + pow + "f", value);
  }
  static public String toFixed(double value) {
    return toFixed(value, 2);
  }
  
  /** Snap an angle to its nearest n-th slice */
  static public double snapAngle(double angle, int slices) {
    double sliceSize = 360D / slices;
    double normalized = ((angle % 360) + 360) % 360;
    double nearestSlice = Math.round(normalized / sliceSize) * sliceSize;
    return nearestSlice % 360;
  }
  static public double snapAngle(double angle) {
    return snapAngle(angle, 8);
  }
  static public float snapAngle(float angle, int slices) {
    return (float) snapAngle((double) angle, slices);
  }
  static public float snapAngle(float angle) {
    return (float) snapAngle((double) angle);
  }
  
  /** This uses legacy color codes, for components, use {@link #formatTimeComp} */
  static public String formatTime(int totalSeconds, String color) {
    int minutes = totalSeconds / 60;
    int seconds = totalSeconds % 60;
    return "%s%d§7:%s%02d§r".formatted(color, minutes, color, seconds);
  }
  
  /** Remove legacy color codes from a string */
  static public String stripColor(String text) {
    return text.replaceAll("§[0-9a-fklmnor]", "");
  }
  
  /** Randomly add drops count */
  static public int applyFortune(int levels) {
    if (levels <= 0) return 1;
    return Math.floorMod(RandomUtils.nextInt(), Math.min(levels + 1, 64)) + 1;
  }
  
  static public Component formatTimeComp(int totalSeconds, TextColor color) {
    int minutes = totalSeconds / 60;
    int seconds = totalSeconds % 60;
    return Component.join(
      JoinConfiguration.noSeparators(),
      Component.text("%d".formatted(minutes)).color(color),
      Component.text(":").color(NamedTextColor.GRAY),
      Component.text("%02d".formatted(seconds)).color(color)
    );
  }
  
  /** Empty item for GUIs */
  static public ItemStack emptyItem() {
    ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    item.editMeta(meta -> {
      meta.setHideTooltip(true);
    });
    return item;
  }
  
  static public void setTickOut(Runnable task, int ticks) {
    Bukkit.getScheduler().runTaskLater(DestroyTheCore.instance, task, ticks);
  }
  static public void setTickOut(Runnable task) {
    setTickOut(task, 1);
  }
  
  static public void log(String text) {
    DestroyTheCore.instance.getLogger().info(text);
  }
  static public void log(Object any) {
    log(any == null ? "null" : any.toString());
  }
  
  static public void error(String text) {
    DestroyTheCore.instance.getLogger().severe(text);
  }
  
  /** Pure math, by Gemini */
  public static Vector calculateBounce(
    Location center,
    Location pos,
    Vector speed,
    double restitution
  ) {
    Vector normal = pos.toVector().subtract(center.toVector());
    Vector unitNormal = normal.normalize();
    double dotProduct = speed.dot(unitNormal);
    if (dotProduct >= 0) return speed.clone().multiply(restitution);
    Vector reflectionFactor = unitNormal.clone().multiply(2 * dotProduct);
    Vector reflectedSpeed = speed.clone().subtract(reflectionFactor);
    return reflectedSpeed.multiply(restitution);
  }
  public static Vector calculateBounce(Location center, Location pos, Vector speed) {
    return calculateBounce(center, pos, speed, 0.6);
  }
}
