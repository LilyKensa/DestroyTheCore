package dev.huey.destroyTheCore.bases;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.managers.InventoriesManager;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ItemGen {
  
  /** Used to distinguish item-gens, stored data is the name of {@link #id} */
  public static final NamespacedKey dataNamespace = new NamespacedKey(
    DestroyTheCore.instance,
    "custom-item"
  );
  
  public ItemsManager.ItemKey id;
  public Material iconType;
  public String translationName;
  
  public Component name;
  public List<Component> lore = new ArrayList<>();
  
  public ItemGen(ItemsManager.ItemKey id, Material iconType) {
    this.id = id;
    this.iconType = iconType;
    
    translationName = id.name().toLowerCase().replace('_', '-');
    
    name = TextUtils.$("items.%s.name".formatted(translationName));
    
    String key;
    for (int i = 1; true; ++i) {
      key = "items.%s.desc".formatted(translationName) + "-" + i;
      
      if (DestroyTheCore.translationsManager.has(key)) lore.add(
        TextUtils.$(key)
      );
      else break;
    }
  }
  
  public ItemStack getItem(int count) {
    if (iconType.isAir()) return ItemStack.empty();
    
    ItemStack item = new ItemStack(iconType, count);
    item.editMeta(meta -> {
      meta.displayName(name.decoration(TextDecoration.ITALIC, false));
      meta.lore(lore);
      
      if (item.getType().getMaxDurability() > 0) meta.setUnbreakable(true);
      meta.setEnchantmentGlintOverride(true);
      
      computeMeta(meta);
      
      meta.getPersistentDataContainer().set(dataNamespace,
        PersistentDataType.STRING,
        id.name());
    });
    
    return item;
  }
  
  public ItemStack getItem() {
    return getItem(1);
  }
  
  /**
   * For custom metadata other than name and descriptions
   * 
   * @apiNote Optional
   */
  public void computeMeta(ItemMeta meta) {
  }
  
  /** Check if an item stack is an instance of this item-gen */
  public boolean checkItem(ItemStack item) {
    if (item == null) return false;
    
    ItemMeta meta = item.getItemMeta();
    if (meta == null) return false;
    
    PersistentDataContainer container = meta.getPersistentDataContainer();
    if (!container.has(dataNamespace)) return false;
    return this.id.name().equals(container.get(dataNamespace,
      PersistentDataType.STRING));
  }
  
  /**
   * If {@code true}, this item will vanish when being replaced
   * Otherwise they'll be added back or dropped
   */
  boolean trash = false;
  
  public void setTrash(boolean trash) {
    this.trash = trash;
  }
  
  public void setTrash() {
    setTrash(true);
  }
  
  public boolean isTrash() {
    return trash;
  }
  
  /**
   * If {@code true}, players can't drop this item
   * <p>
   * Used in {@link ItemsManager#onPlayerDropItem}
   */
  boolean bound = false;
  
  public void setBound(boolean bound) {
    this.bound = bound;
  }
  
  public void setBound() {
    setBound(true);
  }
  
  public boolean isBound() {
    return bound;
  }
  
  /**
   * If {@code true}, players will always drop this item on death
   * <p>
   * Used in {@link InventoriesManager#dropSome}
   */
  boolean vanish = false;
  
  public void setVanish(boolean state) {
    vanish = state;
  }
  
  public void setVanish() {
    setVanish(true);
  }
  
  public boolean willVanish() {
    return vanish;
  }
  
  /**
   * If {@code true}, players will never drop this item on death
   * <p>
   * Used in {@link InventoriesManager#dropSome}
   */
  boolean neverDrop = false;
  
  public void setNeverDrop(boolean state) {
    neverDrop = state;
  }
  
  public void setNeverDrop() {
    setNeverDrop(true);
  }
  
  public boolean willNeverDrop() {
    return neverDrop;
  }
}
