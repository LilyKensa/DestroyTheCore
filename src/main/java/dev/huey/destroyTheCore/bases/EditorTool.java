package dev.huey.destroyTheCore.bases;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.managers.TicksManager;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class EditorTool {
  
  static final Component toolPrefix = TextUtils.$("tool.prefix");
  
  /** Used to distinguish tools, stored data is {@link #id} */
  static final NamespacedKey dataNamespace = new NamespacedKey(
    DestroyTheCore.instance,
    "editor-tool"
  );
  
  public String id;
  public Material iconType;
  
  /** @param id Used for translation, all lowercase */
  public EditorTool(String id, Material iconType) {
    this.id = id;
    this.iconType = iconType;
  }
  
  public ItemStack getItem() {
    if (iconType.isAir()) return ItemStack.empty();
    
    ItemStack item = new ItemStack(iconType);
    ItemMeta meta = item.getItemMeta();
    
    meta.displayName(
      toolPrefix.append(
        TextUtils.$("tools." + id).color(
          NamedTextColor.GOLD
        )
      ).decoration(TextDecoration.ITALIC, false)
    );
    meta.setEnchantmentGlintOverride(true);
    
    meta.getPersistentDataContainer().set(
      dataNamespace,
      PersistentDataType.STRING,
      id
    );
    
    item.setItemMeta(meta);
    return item;
  }
  
  /** Check if an item stack is an instance of this tool */
  public boolean checkItem(ItemStack item) {
    if (item == null) return false;
    
    ItemMeta meta = item.getItemMeta();
    if (meta == null) return false;
    
    PersistentDataContainer container = meta.getPersistentDataContainer();
    if (!container.has(dataNamespace)) return false;
    return this.id.equals(
      container.get(dataNamespace, PersistentDataType.STRING)
    );
  }
  
  /**
   * Used to refresh the tool when reloading data from config files
   * 
   * @implNote Optional
   */
  public void refresh() {
  }
  
  /**
   * Used in {@link TicksManager.TicksRunnable}
   * 
   * @implNote Optional
   */
  public void onParticleTick(Player pl) {
  }
  
  /** @implNote Optional */
  public void onRightClickAir(Player pl) {
  }
  
  /** @implNote Optional */
  public void onRightClickBlock(Player pl, Block block) {
  }
  
  /** @implNote Optional */
  public void onBreakBlock(Player pl, Block block) {
  }
}
