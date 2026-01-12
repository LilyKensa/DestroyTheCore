package dev.huey.destroyTheCore.items.tokens;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.*;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public class AssignMoreRespawnTimeGen extends UsableItemGen {
  public AssignMoreRespawnTimeGen() {
    super(
      ItemsManager.ItemKey.ASSIGN_MORE_RESPAWN_TIME,
      Material.JUNGLE_SIGN,
      true
    );
  }
  
  @Override
  public void use(Player pl, Block block) {
    Game.Side side = DestroyTheCore.game.getPlayerData(pl).side;
    if (side.equals(Game.Side.SPECTATOR)) return;
    
    Player target = RandomUtils.pick(PlayerUtils.getEnemies(side));
    if (target == null) {
      pl.sendActionBar(TextUtils.$("items.assign-more-respawn-time.not-found"));
      return;
    }
    
    PlayerUtils.takeOneItemFromHand(pl);
    
    PlayerUtils.delayAssign(
      pl,
      target,
      Particle.SOUL_FIRE_FLAME,
      () -> {
        DestroyTheCore.game.getPlayerData(target).addRespawnTime(60);
        
        DestroyTheCore.boardsManager.refresh(target);
        
        ParticleUtils.ring(
          PlayerUtils.all(),
          LocationUtils.hitboxCenter(target),
          1.2,
          Color.AQUA
        );
        PlayerUtils.broadcast(
          TextUtils.$("items.assign-more-respawn-time.announce", List.of(
            Placeholder.component("player", PlayerUtils.getName(pl)),
            Placeholder.component("item", getItem().effectiveName()),
            Placeholder.component("target", PlayerUtils.getName(target))
          ))
        );
      }
    );
  }
}
