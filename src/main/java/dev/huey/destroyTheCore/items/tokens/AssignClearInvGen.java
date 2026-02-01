package dev.huey.destroyTheCore.items.tokens;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.records.SideData;
import dev.huey.destroyTheCore.utils.*;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Allay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class AssignClearInvGen extends UsableItemGen {
  
  public AssignClearInvGen() {
    super(ItemsManager.ItemKey.ASSIGN_CLEAR_INV, Material.BAMBOO_SIGN, true);
  }
  
  public Allay summonAllayWithItem(Location location, ItemStack item) {
    Allay allay = (Allay) location.getWorld().spawnEntity(
      location,
      EntityType.ALLAY
    );
    
    allay.customName(TextUtils.$("items.assign-clear-inv.allay"));
    
    allay.getAttribute(Attribute.SCALE).setBaseValue(1.2);
    allay.getAttribute(Attribute.MAX_HEALTH).setBaseValue(1);
    allay.setHealth(1);
    
    allay.getEquipment().setItemInMainHand(item);
    allay.getEquipment().setItemInMainHandDropChance(1);
    
    return allay;
  }
  
  @Override
  public void use(Player pl, Block block) {
    Game.Side side = DestroyTheCore.game.getPlayerData(pl).side;
    SideData sideData = DestroyTheCore.game.getSideData(side);
    if (side.equals(Game.Side.SPECTATOR)) return;
    
    if (sideData.clearInvCooldown > 0) {
      pl.sendActionBar(
        TextUtils.$(
          "items.assign-clear-inv.cooldown",
          List.of(
            Placeholder.unparsed(
              "cooldown",
              CoreUtils.toFixed(sideData.clearInvCooldown / 20D, 1)
            )
          )
        )
      );
      return;
    }
    
    Player target = RandomUtils.pick(PlayerUtils.getEnemies(side));
    if (target == null) {
      pl.sendActionBar(TextUtils.$("items.assign-clear-inv.not-found"));
      return;
    }
    
//    PlayerUtils.takeOneItemFromHand(pl);
    
    sideData.clearInvCooldown = 10 * 60 * 20;
    
    PlayerUtils.delayAssign(
      pl,
      target,
      Particle.ENCHANTED_HIT,
      () -> {
        PlayerInventory inv = target.getInventory();
        
        ItemStack[] items = inv.getContents();
        inv.clear();
        
        new BukkitRunnable() {
          int index = 0;
          
          @Override
          public void run() {
            for (
                 int max = index + RandomUtils.range(3, 6); index < max; ++index
            ) {
              if (index >= items.length) {
                cancel();
                return;
              }
              
              summonAllayWithItem(
                LocUtils.hitboxCenter(target),
                items[index]
              );
            }
          }
        }.runTaskTimer(DestroyTheCore.instance, 0, 2);
        
        for (Player p : Bukkit.getOnlinePlayers()) p.playSound(
          p.getLocation(),
          Sound.ENTITY_WITHER_SPAWN,
          1, // Volume
          1 // Pitch
        );
        
        ParticleUtils.ring(
          PlayerUtils.all(),
          LocUtils.hitboxCenter(target),
          1.2,
          Color.ORANGE
        );
        PlayerUtils.broadcast(
          TextUtils.$(
            "items.assign-clear-inv.announce",
            List.of(
              Placeholder.component("player", PlayerUtils.getName(pl)),
              Placeholder.component("item", getItem().effectiveName()),
              Placeholder.component("target", PlayerUtils.getName(target))
            )
          )
        );
        PlayerUtils.broadcast(
          TextUtils.$(
            "chat.format",
            List.of(
              Placeholder.component("player", PlayerUtils.getName(target)),
              Placeholder.component(
                "message",
                TextUtils.$("items.assign-clear-inv.post-announce").color(null)
              )
            )
          )
        );
      }
    );
  }
}
