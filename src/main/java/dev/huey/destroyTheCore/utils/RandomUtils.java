package dev.huey.destroyTheCore.utils;

import java.util.*;

public class RandomUtils {
  
  static public final Random random = new Random();
  
  static public boolean nextBool() {
    return random.nextBoolean();
  }
  
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
  
  static <T> T generalPick(
    Collection<T> collection, boolean remove
  ) {
    if (collection.isEmpty()) return null;
    
    int targetIndex = Math.floorMod(nextInt(), collection.size());
    
    Iterator<T> it = collection.iterator();
    T result = null;
    
    for (int i = 0; i <= targetIndex; ++i) {
      result = it.next();
      if (i == targetIndex) {
        if (remove) it.remove();
        break;
      }
    }
    
    return result;
  }
  
  static public <T> T pick(List<T> list) {
    return generalPick(list, false);
  }
  
  @SafeVarargs
  static public <T> T pick(T... array) {
    return pick(Arrays.asList(array));
  }
  
  static public <T> T pick(Set<T> set) {
    return generalPick(set, false);
  }
  
  static public <T> T pickPop(Set<T> set) {
    return generalPick(set, true);
  }
}
