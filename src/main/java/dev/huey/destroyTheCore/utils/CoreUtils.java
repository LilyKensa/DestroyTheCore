package dev.huey.destroyTheCore.utils;

import dev.huey.destroyTheCore.DestroyTheCore;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
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
  public static <T> T def(Object value, T defValue) {
    return value == null ? defValue : (T) value;
  }
  
  /** Fix numbers to specific after point */
  public static String toFixed(double value, int pow) {
    return String.format("%." + pow + "f", value);
  }
  
  public static String toFixed(double value) {
    return toFixed(value, 2);
  }
  
  /** Snap an angle to its nearest n-th slice */
  public static double snapAngle(double angle, int slices) {
    double sliceSize = 360D / slices;
    double normalized = ((angle % 360) + 360) % 360;
    double nearestSlice = Math.round(normalized / sliceSize) * sliceSize;
    return nearestSlice % 360;
  }
  
  public static double snapAngle(double angle) {
    return snapAngle(angle, 8);
  }
  
  public static float snapAngle(float angle, int slices) {
    return (float) snapAngle((double) angle, slices);
  }
  
  public static float snapAngle(float angle) {
    return (float) snapAngle((double) angle);
  }
  
  public static String capitalize(String str) {
    return (str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase());
  }
  
  /**
   * This uses legacy color codes, for components, use {@link #formatTimeComp}
   */
  public static String formatTime(int totalSeconds, String color) {
    int minutes = totalSeconds / 60;
    int seconds = totalSeconds % 60;
    return "%s%d§7:%s%02d§r".formatted(color, minutes, color, seconds);
  }
  
  /** Remove legacy color codes from a string */
  public static String stripColor(String text) {
    return text.replaceAll("§[0-9a-fklmnor]", "");
  }
  
  /** Randomly add drops count */
  public static int applyFortune(int levels) {
    if (levels <= 0) return 1;
    return (Math.floorMod(RandomUtils.nextInt(), Math.min(levels + 1, 64)) + 1);
  }
  
  public static Component formatTimeComp(int totalSeconds, TextColor color) {
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
  public static ItemStack emptyGuiItem() {
    ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
    item.editMeta(meta -> {
      meta.setHideTooltip(true);
    });
    return item;
  }
  
  @SuppressWarnings("unchecked")
  public static <T> Function<Object, List<T>> listLoader(Class<T> elementType) {
    return unknown -> {
      List<T> result = new ArrayList<>();
      
      if (unknown == null) return result;
      if (!(unknown instanceof List<?> list)) return result;
      
      for (Object element : list) {
        if (elementType.isInstance(element)) result.add((T) element);
      }
      
      return result;
    };
  }
  
  public static void setTickOut(Runnable task, int ticks) {
    Bukkit.getScheduler().runTaskLater(DestroyTheCore.instance, task, ticks);
  }
  
  public static void setTickOut(Runnable task) {
    setTickOut(task, 1);
  }
  
  public static void log(String text) {
    DestroyTheCore.instance.getLogger().info(text);
  }
  
  public static void log(Object any) {
    log(any == null ? "null" : any.toString());
  }
  
  public static void error(String text) {
    DestroyTheCore.instance.getLogger().severe(text);
  }
  
  /** Pure math, by Gemini */
  public static Vector calculateBounce(
                                       Location center, Location pos, Vector speed, double restitution
  ) {
    Vector normal = pos.toVector().subtract(center.toVector());
    Vector unitNormal = normal.normalize();
    double dotProduct = speed.dot(unitNormal);
    if (dotProduct >= 0) return speed.clone().multiply(restitution);
    Vector reflectionFactor = unitNormal.clone().multiply(2 * dotProduct);
    Vector reflectedSpeed = speed.clone().subtract(reflectionFactor);
    return reflectedSpeed.multiply(restitution);
  }
  
  public static Vector calculateBounce(
                                       Location center, Location pos, Vector speed
  ) {
    return calculateBounce(center, pos, speed, 0.6);
  }
}
