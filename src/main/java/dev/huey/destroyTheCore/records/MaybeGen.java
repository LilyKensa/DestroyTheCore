package dev.huey.destroyTheCore.records;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class MaybeGen implements ConfigurationSerializable {
  
  ItemsManager.ItemKey key;
  int amount = 1;
  ItemStack stack;
  
  public static MaybeGen fromItem(ItemStack item) {
    MaybeGen mg = new MaybeGen();
    
    if (DestroyTheCore.itemsManager.isGen(item)) {
      mg.key = DestroyTheCore.itemsManager.getGen(item).id;
      mg.amount = item.getAmount();
    }
    else {
      mg.stack = item;
    }
    
    return mg;
  }
  
  public ItemStack get() {
    if (key != null) return DestroyTheCore.itemsManager.gens.get(key).getItem(
      amount);
    
    return stack;
  }
  
  @Override
  public Map<String, Object> serialize() {
    Map<String, Object> map = new HashMap<>();
    
    if (key != null) {
      map.put("gen", key.name());
      map.put("count", amount);
    }
    else if (stack != null) {
      map.put("item", stack);
    }
    
    return map;
  }
  
  public static MaybeGen deserialize(Map<String, Object> map) {
    MaybeGen mg = new MaybeGen();
    
    if (map.containsKey("gen") && map.containsKey("count")) {
      mg.key = ItemsManager.ItemKey.valueOf((String) map.get("gen"));
      mg.amount = (int) map.get("count");
    }
    else if (map.containsKey("item")) {
      mg.stack = (ItemStack) map.get("item");
    }
    
    return mg;
  }
}
