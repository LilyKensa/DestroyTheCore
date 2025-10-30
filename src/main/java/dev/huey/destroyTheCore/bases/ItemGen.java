package dev.huey.destroyTheCore.bases;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ItemGen {
  static public final NamespacedKey dataNamespace = new NamespacedKey(
    DestroyTheCore.instance, "custom-item");
  
  public ItemsManager.ItemKey id;
  public Material iconType;
  public String translationName;
  
  public Component name;
  public List<Component> lore;
  
  Component $(String translateRoot) {
    return TextUtils.$(translateRoot.formatted(translationName));
  }
  
  List<Component> $a(String translateRoot) {
    List<Component> list = new ArrayList<>();
    
    String key;
    for (int i = 1; true; ++i) {
      key = translateRoot.formatted(translationName) + "-" + i;
      
      if (DestroyTheCore.translationsManager.has(key))
        list.add(TextUtils.$(key));
      else
        break;
    }
    
    return list;
  }
  
  public ItemGen(ItemsManager.ItemKey id, Material iconType) {
    this.id = id;
    this.iconType = iconType;
    
    translationName = id.name().toLowerCase().replace('_', '-');
    name = $("items.%s.name");
    lore = $a("items.%s.desc");
  }
  
  public ItemStack getItem(int count) {
    if (iconType.isAir()) return ItemStack.empty();
    
    ItemStack item = new ItemStack(iconType, count);
    item.editMeta((meta) -> {
      meta.displayName(name.decoration(TextDecoration.ITALIC, false));
      meta.lore(lore);
      
      if (item.getType().getMaxDurability() > 0)
        meta.setUnbreakable(true);
      meta.setEnchantmentGlintOverride(true);
      
      computeMeta(meta);
      
      meta.getPersistentDataContainer().set(
        dataNamespace,
        PersistentDataType.STRING,
        id.name()
      );
    });
    
    return item;
  }
  public ItemStack getItem() {
    return getItem(1);
  }
  
  public void computeMeta(ItemMeta meta) {
  
  }
  
  public boolean checkItem(ItemStack item) {
    if (item == null) return false;
    
    ItemMeta meta = item.getItemMeta();
    if (meta == null) return false;
    
    PersistentDataContainer container = meta.getPersistentDataContainer();
    if (!container.has(dataNamespace)) return false;
    return this.id.name().equals(container.get(dataNamespace, PersistentDataType.STRING));
  }
  
  boolean important = true, bound = false, vanish = false, neverDrop = false;
  
  public void setImportant(boolean important) {
    this.important = important;
  }
  public void setNotImportant() {
    setImportant(false);
  }
  
  public void setBound(boolean bound) {
    this.bound = bound;
  }
  public void setBound() {
    setBound(true);
  }
  
  public void setVanish(boolean state) {
    vanish = state;
  }
  public void setVanish() {
    setVanish(true);
  }
  
  public void setNeverDrop(boolean state) {
    neverDrop = state;
  }
  public void setNeverDrop() {
    setNeverDrop(true);
  }
  
  public boolean isBound() {
    return bound;
  }
  public boolean isImportant() {
    return important;
  }
  public boolean disappearOnDeath() {
    return vanish;
  }
  public boolean ignoreOnDeath() {
    return neverDrop;
  }
}
