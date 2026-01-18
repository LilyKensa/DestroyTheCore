package dev.huey.destroyTheCore.utils;

import java.util.*;

public class RandomUtils {
  static public final Random random = new Random();
  
  static public int nextInt() {
    return random.nextInt();
  }
  
  static public int range(int l, int r) {
    return l + Math.floorMod(nextInt(), r - l);
  }
  static public int range(int r) {
    return range(0, r);
  }
  
  static public double nextDouble() {
    return random.nextDouble();
  }
  
  static public double aroundZero(double off) {
    return nextDouble() * (off * 2) - off;
  }
  
  static public boolean hit(double chance) {
    return nextDouble() < chance;
  }
  
  static public <T> T pick(List<T> list) {
    if (list.isEmpty()) return null;
    
    return list.get(Math.floorMod(nextInt(), list.size()));
  }
  static public <T> T pick(Set<T> set) {
    return pick(new ArrayList<>(set));
  }
  static public <T> T pick(T... array) {
    return pick(Arrays.asList(array));
  }
}
