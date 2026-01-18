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

public class GiveStrengthGen extends UsableItemGen {
  
  public GiveStrengthGen() {
    super(ItemsManager.ItemKey.GIVE_STRENGTH, Material.FIREWORK_STAR);
  }
  
  @Override
  public void use(Player pl, Block block) {
    if (!PlayerUtils.checkGroupCooldown(pl, GiveSpeedGen.group)) return;
    PlayerUtils.setGroupCooldown(pl, GiveSpeedGen.group, GiveSpeedGen.cooldown);
    
    PlayerUtils.takeOneItemFromHand(pl);
    
    pl.addPotionEffect(
      new PotionEffect(PotionEffectType.STRENGTH, 6 * 20, 2, false, true)
    );
    
    PlayerUtils.broadcast(
      TextUtils.$(
        "items.give-strength.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.component("item", getItem().effectiveName())
        )
      )
    );
  }
}
