package dev.huey.destroyTheCore.items.gadgets;

import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class GiveJumpBoostGen extends UsableItemGen {
  
  public GiveJumpBoostGen() {
    super(ItemsManager.ItemKey.GIVE_JUMP_BOOST, Material.SLIME_BALL);
  }
  
  @Override
  public void use(Player pl, Block block) {
    if (!PlayerUtils.checkGroupCooldown(pl, GiveSpeedGen.group)) return;
    PlayerUtils.setGroupCooldown(pl, GiveSpeedGen.group, GiveSpeedGen.cooldown);
    
    PlayerUtils.takeOneItemFromHand(pl);
    
    pl.addPotionEffect(
      new PotionEffect(PotionEffectType.JUMP_BOOST, 6 * 20, 9, false, true)
    );
    
    PlayerUtils.broadcast(
      TextUtils.$(
        "items.give-jump-boost.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.component("item", getItem().effectiveName())
        )
      )
    );
  }
}
