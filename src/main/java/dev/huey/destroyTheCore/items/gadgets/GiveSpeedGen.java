package dev.huey.destroyTheCore.items.gadgets;

import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GiveSpeedGen extends UsableItemGen {
  
  public GiveSpeedGen() {
    super(ItemsManager.ItemKey.GIVE_SPEED, Material.FEATHER);
  }
  
  public static final List<ItemsManager.ItemKey> group = List.of(
    ItemsManager.ItemKey.GIVE_SPEED,
    ItemsManager.ItemKey.GIVE_JUMP_BOOST,
    ItemsManager.ItemKey.GIVE_STRENGTH
  );
  public static final int cooldown = 180 * 20;
  
  @Override
  public void use(Player pl, Block block) {
    if (!PlayerUtils.checkGroupCooldown(pl, group)) return;
    PlayerUtils.setGroupCooldown(pl, group, cooldown);
    
    PlayerUtils.takeOneItemFromHand(pl);
    
    pl.addPotionEffect(
      new PotionEffect(PotionEffectType.SPEED, 6 * 20, 9, false, true)
    );
    
    PlayerUtils.broadcast(
      TextUtils.$(
        "items.give-speed.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.component("item", getItem().effectiveName())
        )
      )
    );
  }
}
