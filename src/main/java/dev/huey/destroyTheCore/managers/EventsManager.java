package dev.huey.destroyTheCore.managers;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.itemGens.ProjItemGen;
import dev.huey.destroyTheCore.items.gadgets.GrenadeGen;
import dev.huey.destroyTheCore.roles.*;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.List;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EventsManager implements Listener {
  
  boolean checkPaused(Cancellable ev) {
    if (DestroyTheCore.game.paused) {
      ev.setCancelled(true);
      return true;
    }
    
    return false;
  }
  
  boolean checkPaused(Cancellable ev, Entity origin) {
    if (origin instanceof Player pl && !PlayerUtils.shouldHandle(pl))
      return false;
    return checkPaused(ev);
  }
  
  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent ev) {
    if (checkPaused(ev, ev.getPlayer())) return;
    
    DestroyTheCore.game.handleInteract(ev);
    DestroyTheCore.itemsManager.onPlayerInteract(ev);
    DestroyTheCore.toolsManager.onPlayerInteract(ev);
  }
  
  @EventHandler
  public void onPlayerInteractEntity(PlayerInteractEntityEvent ev) {
    if (checkPaused(ev, ev.getPlayer())) return;
    
    DestroyTheCore.game.handleInteractEntity(ev);
  }
  
  @EventHandler
  public void onBlockPlace(BlockPlaceEvent ev) {
    if (checkPaused(ev, ev.getPlayer())) return;
    
    DestroyTheCore.game.handleBlockPlace(ev);
  }
  
  @EventHandler
  public void onBlockBreak(BlockBreakEvent ev) {
    if (checkPaused(ev, ev.getPlayer())) return;
    
    Player pl = ev.getPlayer();
    Block block = ev.getBlock();
    
    ConstructorRole.onBlockBreak(pl, block);
    MoleRole.onBlockBreak(pl, block, ev);
    
    if (ev.isCancelled()) return;
    
    DestroyTheCore.toolsManager.onBlockBreak(ev);
    
    DestroyTheCore.game.handleBlockBreak(ev);
  }
  
  @EventHandler
  public void onBlockChange(BlockFormEvent ev) {
    DestroyTheCore.game.handleBlockForm(ev);
  }
  
  @EventHandler
  public void onBlockFromTo(BlockFromToEvent ev) {
    if (
      List.of(Material.WATER, Material.LAVA).contains(ev.getBlock().getType())
    ) {
      DestroyTheCore.game.handleLiquidFlow(ev);
    }
  }
  
  @EventHandler
  public void onPlayerBucketEmpty(PlayerBucketEmptyEvent ev) {
    if (checkPaused(ev, ev.getPlayer())) return;
    
    DestroyTheCore.game.handlePourLiquid(ev);
  }
  
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent ev) {
    DestroyTheCore.game.handleJoinedPlayer(ev.getPlayer());
    DestroyTheCore.boardsManager.onPlayerJoin(ev);
  }
  
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent ev) {
    DestroyTheCore.game.handleQuitedPlayer(ev.getPlayer());
    DestroyTheCore.boardsManager.onPlayerQuit(ev);
    DestroyTheCore.guiManager.onPlayerLeave(ev.getPlayer());
  }
  
  @EventHandler
  public void onPlayerTeleport(PlayerTeleportEvent ev) {
    if (checkPaused(ev, ev.getPlayer())) return;
    
    if (!LocUtils.isSameWorld(ev.getFrom(), ev.getTo())) {
      DestroyTheCore.worldsManager.onPlayerChangeWorld(
        ev.getPlayer(),
        ev.getTo().getWorld()
      );
    }
  }
  
  @EventHandler
  public void onVehicleEnter(VehicleEnterEvent ev) {
    if (!(ev.getEntered() instanceof Player pl)) return;
    
    DestroyTheCore.game.handlePlayerRide(pl, ev.getVehicle(), ev);
  }
  
  @EventHandler
  public void onPlayerDropItem(PlayerDropItemEvent ev) {
    if (checkPaused(ev, ev.getPlayer())) return;
    
    Player pl = ev.getPlayer();
    ItemStack item = ev.getItemDrop().getItemStack();
    
    DestroyTheCore.itemsManager.onPlayerDropItem(ev);
    
    DestroyTheCore.game.handleDropItem(pl, item, ev);
  }
  
  @EventHandler
  public void onItemSpawn(ItemSpawnEvent ev) {
    Item entity = ev.getEntity();
    
    DestroyTheCore.game.handleItemSpawn(entity, entity.getItemStack(), ev);
  }
  
  @EventHandler
  public void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent ev) {
    if (checkPaused(ev, ev.getPlayer())) return;
    
    DestroyTheCore.game.handlePickupItem(ev);
  }
  
  @EventHandler
  public void onPlayerPickUpArrow(PlayerPickupArrowEvent ev) {
    if (checkPaused(ev, ev.getPlayer())) return;
    
    DestroyTheCore.game.handlePickupArrow(ev);
  }
  
  @EventHandler
  public void onPlayerItemConsume(PlayerItemConsumeEvent ev) {
    DestroyTheCore.game.handleItemUsed(ev.getPlayer(), ev.getItem(), ev);
  }
  
  @EventHandler
  public void onFoodLevelChange(FoodLevelChangeEvent ev) {
    if (checkPaused(ev, ev.getEntity())) return;
    
    DestroyTheCore.game.handleFoodLevelChange(ev);
  }
  
  @EventHandler
  public void onInventoryClick(InventoryClickEvent ev) {
    if (!(ev.getWhoClicked() instanceof Player pl)) return;
    
    if (checkPaused(ev, pl)) return;
    
    Inventory inv = ev.getClickedInventory();
    if (inv == null) return;
    
    ItemStack item = ev.getCurrentItem();
    if (item == null) return;
    
    DestroyTheCore.game.handleInventoryClick(
      inv,
      pl,
      item,
      ev.getClick(),
      ev.getAction(),
      ev
    );
  }
  
  @EventHandler
  public void onInventoryOpen(InventoryOpenEvent ev) {
    DestroyTheCore.game.handleInventoryOpen(ev);
  }
  
  @EventHandler
  public void onInventoryClose(InventoryCloseEvent ev) {
    DestroyTheCore.game.handleInventoryClose(ev);
  }
  
  @EventHandler
  public void onPrepareItemCraft(PrepareItemCraftEvent ev) {
    DestroyTheCore.game.handleCrafting(ev);
  }
  
  @EventHandler
  public void onPrepareAnvil(PrepareAnvilEvent ev) {
    DestroyTheCore.game.handleRepair(ev);
  }
  
  @EventHandler
  public void onPrepareGrindstone(PrepareGrindstoneEvent ev) {
    DestroyTheCore.game.handleGrinding(ev);
  }
  
  @EventHandler
  public void onPrepareItemEnchant(EnchantItemEvent ev) {
    GuardRole.onEnchant(ev.getEnchanter());
  }
  
  @EventHandler
  public void onPlayerMove(PlayerMoveEvent ev) {
    if (ev.hasChangedPosition()) {
      if (checkPaused(ev, ev.getPlayer())) return;
      
      RangerRole.onPlayerMove(ev.getPlayer());
      AssassinRole.onPlayerMove(ev.getPlayer());
      DestroyTheCore.game.handlePlayerMove(ev.getPlayer());
    }
  }
  
  @EventHandler
  public void onPlayerJump(PlayerJumpEvent ev) {
    MoleRole.onPlayerJump(ev.getPlayer());
  }
  
  @EventHandler
  public void onVehicleDamage(VehicleDamageEvent ev) {
    DestroyTheCore.game.handleVehicleDamage(ev);
  }
  
  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent ev) {
    if (checkPaused(ev, ev.getDamager())) return;
    
    DestroyTheCore.game.handleEntityDamage(ev);
  }
  
  @EventHandler
  public void onEntityExplode(EntityExplodeEvent ev) {
    DestroyTheCore.game.handleExplosion(ev);
  }
  
  @EventHandler
  public void onBlockPistonExtend(BlockPistonExtendEvent ev) {
    DestroyTheCore.game.handlePistonExtend(ev);
  }
  
  @EventHandler
  public void onBlockPistonRetract(BlockPistonRetractEvent ev) {
    DestroyTheCore.game.handlePistonRetract(ev);
  }
  
  @EventHandler
  public void onEntityShootBow(EntityShootBowEvent ev) {
    if (checkPaused(ev, ev.getEntity())) return;
    
    for (ProjItemGen g : DestroyTheCore.itemsManager.projGens.values())
      g.outerOnEntityShootBow(
        ev
      );
  }
  
  @EventHandler
  public void onProjectileHit(ProjectileHitEvent ev) {
    WandererRole.onProjectileHit(ev);
    GrenadeGen.onProjectileHit(ev);
    
    for (ProjItemGen g : DestroyTheCore.itemsManager.projGens.values()) {
      g.outerOnProjectileHit(
        ev
      );
    }
  }
  
  @EventHandler
  public void onEntityDeath(EntityDeathEvent ev) {
    KekkaiMasterRole.onEntityDeath(ev);
    DestroyTheCore.game.handleEntityDeath(ev);
  }
  
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent ev) {
    DestroyTheCore.glowManager.onPlayerDeath(ev);
    DestroyTheCore.game.handlePlayerDeath(ev);
  }
  
  @EventHandler
  public void onAsyncChat(AsyncChatEvent ev) {
    if (
      ev.message() instanceof TextComponent tc
    ) DestroyTheCore.quizManager.onPlayerChat(ev.getPlayer(), tc.content());
    DestroyTheCore.game.handleChat(ev);
  }
  
  @EventHandler
  public void onBlockPhysics(BlockPhysicsEvent ev) {
    if (
      LocUtils.isSameWorld(
        ev.getBlock().getWorld(),
        DestroyTheCore.worldsManager.lobby
      )
    ) ev.setCancelled(true);
  }
}
