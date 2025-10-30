package dev.huey.destroyTheCore.items.tokens;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.records.SideData;
import dev.huey.destroyTheCore.utils.*;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public class AssignClearInvGen extends UsableItemGen {
  public AssignClearInvGen() {
    super(
      ItemsManager.ItemKey.ASSIGN_CLEAR_INV,
      Material.BAMBOO_SIGN
    );
  }
  
  @Override
  public void use(Player pl, Block block) {
    Game.Side side = DestroyTheCore.game.getPlayerData(pl).side;
    SideData sideData = DestroyTheCore.game.getSideData(side);
    if (side.equals(Game.Side.SPECTATOR)) return;
    
    if (sideData.clearInvCooldown > 0) {
      pl.sendActionBar(TextUtils.$("items.assign-clear-inv.cooldown", List.of(
        Placeholder.unparsed(
          "cooldown",
          CoreUtils.toFixed(sideData.clearInvCooldown / 20D, 1)
        )
      )));
      return;
    }
    
    Player target = RandomUtils.pick(PlayerUtils.getEnemies(side));
    if (target == null) {
      pl.sendActionBar(TextUtils.$("items.assign-clear-inv.not-found"));
      return;
    }
    
    PlayerUtils.takeOneItemFromHand(pl);
    
    sideData.clearInvCooldown = 10 * 60 * 20;
    
    PlayerUtils.delayAssign(
      pl,
      target,
      Particle.ENCHANTED_HIT,
      () -> {
        target.getInventory().clear();
        
        DestroyTheCore.boardsManager.refresh(target);
        
        for (Player p : Bukkit.getOnlinePlayers())
          p.playSound(
            p.getLocation(),
            Sound.ENTITY_WITHER_SPAWN,
            1, // Volume
            1 // Pitch
          );
        
        ParticleUtils.ring(
          PlayerUtils.all(),
          LocationUtils.hitboxCenter(target),
          1.2,
          Color.ORANGE
        );
        PlayerUtils.broadcast(
          TextUtils.$("items.assign-clear-inv.announce", List.of(
            Placeholder.component("player", PlayerUtils.getName(pl)),
            Placeholder.component("item", getItem().effectiveName()),
            Placeholder.component("target", PlayerUtils.getName(target))
          ))
        );
        PlayerUtils.broadcast(
          TextUtils.$("chat.format", List.of(
            Placeholder.component("player", PlayerUtils.getName(target)),
            Placeholder.component(
              "message",
              TextUtils.$("items.assign-clear-inv.post-announce")
                .color(null)
            )
          ))
        );
      }
    );
  }
}
