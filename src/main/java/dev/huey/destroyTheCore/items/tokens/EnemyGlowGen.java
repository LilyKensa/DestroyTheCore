package dev.huey.destroyTheCore.items.tokens;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
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

public class EnemyGlowGen extends UsableItemGen {
  
  public EnemyGlowGen() {
    super(ItemsManager.ItemKey.ENEMY_GLOW, Material.BIRCH_SIGN, true);
  }
  
  @Override
  public void use(Player pl, Block block) {
    Game.Side side = DestroyTheCore.game.getPlayerData(pl).side;
    if (side.equals(Game.Side.SPECTATOR)) return;
    
    PlayerUtils.takeOneItemFromHand(pl);
    
    for (Player p : PlayerUtils.getEnemies(side)) {
      PlayerUtils.delayAssign(
        pl,
        p,
        Particle.WAX_OFF,
        () -> {
          p.addPotionEffect(
            new PotionEffect(
              PotionEffectType.GLOWING,
              10 * 60 * 20,
              0,
              true,
              false
            )
          );
        }
      );
    }
    
    PlayerUtils.broadcast(
      TextUtils.$(
        "items.enemy-glow.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.component("item", getItem().effectiveName())
        )
      )
    );
  }
}
