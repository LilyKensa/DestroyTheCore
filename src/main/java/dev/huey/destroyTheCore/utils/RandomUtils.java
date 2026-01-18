package dev.huey.destroyTheCore.utils;

import java.util.*;

public class RandomUtils {
  
  public static final Random random = new Random();
  
  public static int nextInt() {
    return random.nextInt();
  }
  
  public static int range(int l, int r) {
    return l + Math.floorMod(nextInt(), r - l);
  }
  
  public static int range(int r) {
    return range(0, r);
  }
  
  public static double nextDouble() {
    return random.nextDouble();
  }
  
  public static double aroundZero(double off) {
    return nextDouble() * (off * 2) - off;
  }
  
  public static boolean hit(double chance) {
    return nextDouble() < chance;
  }
  
  public static <T> T pick(List<T> list) {
    if (list.isEmpty()) return null;
    
    return list.get(Math.floorMod(nextInt(), list.size()));
  }
  
  public static <T> T pick(Set<T> set) {
    return pick(new ArrayList<>(set));
  }
  
  public static <T> T pick(T... array) {
    return pick(Arrays.asList(array));
  }
}
