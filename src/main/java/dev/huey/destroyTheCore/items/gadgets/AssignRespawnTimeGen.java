package dev.huey.destroyTheCore.items.gadgets;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.*;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class AssignRespawnTimeGen extends UsableItemGen {
  
  public AssignRespawnTimeGen() {
    super(ItemsManager.ItemKey.ASSIGN_RESPAWN_TIME, Material.ENDER_EYE);
  }
  
  @Override
  public void use(Player pl, Block block) {
    Game.Side side = DestroyTheCore.game.getPlayerData(pl).side;
    if (side.equals(Game.Side.SPECTATOR)) return;
    
    Player target = RandomUtils.pick(PlayerUtils.getEnemies(side));
    if (target == null) {
      pl.sendActionBar(TextUtils.$("items.assign-respawn-time.not-found"));
      return;
    }
    
    PlayerUtils.takeOneItemFromHand(pl);
    
    PlayerUtils.delayAssign(
      pl,
      target,
      Particle.FLAME,
      () -> {
        DestroyTheCore.game.getPlayerData(target).addRespawnTime(30);
        
        DestroyTheCore.boardsManager.refresh(target);
        
        ParticleUtils.ring(
          PlayerUtils.all(),
          LocUtils.hitboxCenter(target),
          1.2,
          Color.ORANGE
        );
        PlayerUtils.broadcast(
          TextUtils.$(
            "items.assign-respawn-time.announce",
            List.of(
              Placeholder.component("player", PlayerUtils.getName(pl)),
              Placeholder.component("item", getItem().effectiveName()),
              Placeholder.component("target", PlayerUtils.getName(target))
            )
          )
        );
      }
    );
  }
}
