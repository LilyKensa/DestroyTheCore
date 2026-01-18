package dev.huey.destroyTheCore.items.wands;

import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class LeviStickGen extends UsableItemGen {
  
  public LeviStickGen() {
    super(ItemsManager.ItemKey.LEVI_STICK, Material.BREEZE_ROD);
  }
  
  @Override
  public void use(Player pl, Block block) {
    Player target = PlayerUtils.getTargetPlayer(pl, 4.5);
    if (target == null) {
      pl.sendActionBar(TextUtils.$("items.levi-stick.not-found"));
      return;
    }
    
    PlayerUtils.delayAssign(
      pl,
      target,
      Particle.WHITE_SMOKE,
      () -> {
        target.addPotionEffect(
          new PotionEffect(PotionEffectType.LEVITATION, 20, 4, false, true)
        );
        
        pl.sendActionBar(
          TextUtils.$(
            "items.levi-stick.success",
            List.of(Placeholder.unparsed("target", target.getName()))
          )
        );
      }
    );
  }
}
