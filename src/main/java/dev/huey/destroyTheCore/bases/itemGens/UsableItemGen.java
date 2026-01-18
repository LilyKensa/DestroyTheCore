package dev.huey.destroyTheCore.bases.itemGens;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class UsableItemGen extends ItemGen {
  
  boolean instantUse;
  
  public UsableItemGen(
                       ItemsManager.ItemKey id, Material iconType, boolean instantUse
  ) {
    super(id, iconType);
    this.instantUse = instantUse;
    addLore();
  }
  
  public UsableItemGen(ItemsManager.ItemKey id, Material iconType) {
    this(id, iconType, false);
  }
  
  public boolean isInstantUse() {
    return instantUse;
  }
  
  void addLore() {
    if (
      !lore.isEmpty() && (lore.getLast() instanceof TextComponent lastLore) && !lastLore.content().startsWith(
        "-")
    ) lore.add(Component.empty());
    lore.add(
      TextUtils.$(
        "item-gen." + (instantUse ? "instant-use" : "right-click-to-use")
      )
    );
  }
  
  /** @implNote Required - The functionality when right clicked */
  public void use(Player pl, Block block) {
    PlayerUtils.prefixedSend(
      pl,
      "This item's usage isn't implemented yet!",
      NamedTextColor.RED
    );
  }
}
