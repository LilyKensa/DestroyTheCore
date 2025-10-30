package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.utils.LocationUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CargoMission extends Mission implements Listener {
  public static final NamespacedKey dataNamespace
    = new NamespacedKey(DestroyTheCore.instance, "cargo-mission-package");
  
  public static ItemStack getItem() {
    ItemStack item = new ItemStack(Material.VAULT);
    
    item.editMeta(meta -> {
      meta.displayName(TextUtils.$("missions.cargo.item"));
      
      meta.getPersistentDataContainer()
        .set(dataNamespace, PersistentDataType.BOOLEAN, true);
    });
    
    return item;
  }
  
  public static boolean hasItem(Player pl) {
    for (ItemStack item : pl.getInventory().getContents()) {
      if (item == null || item.isEmpty()) continue;
      if (!item.hasItemMeta()) continue;
      
      if (item.getItemMeta().getPersistentDataContainer().has(dataNamespace))
        return true;
    }
    
    return false;
  }
  
  public CargoMission() {
    super("cargo");
  }
  
  boolean draw = true;
  
  public Player randomPlayer(Game.Side side) {
    return RandomUtils.pick(PlayerUtils.getTeammates(side));
  }
  
  @Override
  public void start() {
    Player a = randomPlayer(Game.Side.RED), b = randomPlayer(Game.Side.GREEN);
    if (a == null || b == null) return;
    
    for (Player pl : new Player[] {a, b}) {
      if (pl == null) continue;
      
      pl.give(getItem());
    }
  }
  
  @Override
  public void tick() {
    if (DestroyTheCore.ticksManager.isUpdateTick()) {
      for (Player p : PlayerUtils.allGaming()) {
        if (!hasItem(p)) continue;
        
        p.addPotionEffect(new PotionEffect(
          PotionEffectType.SLOWNESS,
          20,
          9,
          true,
          false
        ));
        p.addPotionEffect(new PotionEffect(
          PotionEffectType.GLOWING,
          20,
          0,
          true,
          false
        ));
        
        if (LocationUtils.near(p.getLocation(), LocationUtils.live(
          DestroyTheCore.game.map.mission
        ), 5)) {
          draw = false;
          declareWinner(p);
          end();
          return;
        }
      }
    }
  }
  
  @Override
  public void finish() {
    if (draw)
      declareDraw();
    
    for (Player p : PlayerUtils.allGaming()) {
      p.getInventory().remove(Material.VAULT);
    }
  }
}
