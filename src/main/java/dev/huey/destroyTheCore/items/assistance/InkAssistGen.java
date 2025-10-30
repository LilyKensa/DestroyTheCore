package dev.huey.destroyTheCore.items.assistance;

import dev.huey.destroyTheCore.bases.itemGens.AssistItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InkAssistGen extends AssistItemGen {
  public InkAssistGen() {
    super(
      ItemsManager.ItemKey.INK_ASSIST,
      Material.INK_SAC
    );
  }
  
  @Override
  public void onAttack(Player victim, Player attacker) {
    PlayerUtils.delayAssign(
      victim,
      attacker,
      Particle.SQUID_INK,
      () -> {
        attacker.addPotionEffect(new PotionEffect(
          PotionEffectType.BLINDNESS,
          5 * 20,
          0,
          false,
          true
        ));
      }
    );
  }
}
