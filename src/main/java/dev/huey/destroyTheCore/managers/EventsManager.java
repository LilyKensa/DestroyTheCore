package dev.huey.destroyTheCore.managers;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import dev.huey.destroyTheCore.DTC;
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
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EventsManager implements Listener {
  
  boolean checkPaused(Cancellable ev) {
    if (DTC.game.paused) {
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
    
    DTC.game.handleInteract(ev);
    DTC.itemsManager.onPlayerInteract(ev);
    DTC.toolsManager.onPlayerInteract(ev);
  }
  
  @EventHandler
  public void onPlayerInteractEntity(PlayerInteractEntityEvent ev) {
    if (checkPaused(ev, ev.getPlayer())) return;
    
    DTC.game.handleInteractEntity(ev);
  }
  
  @EventHandler
  public void onBlockPlace(BlockPlaceEvent ev) {
    if (checkPaused(ev, ev.getPlayer())) return;
    
    DTC.game.handleBlockPlace(ev);
  }
  
  @EventHandler
  public void onBlockBreak(BlockBreakEvent ev) {
    if (checkPaused(ev, ev.getPlayer())) return;
    
    Player pl = ev.getPlayer();
    Block block = ev.getBlock();
    
    ConstructorRole.onBlockBreak(pl, block);
    MoleRole.onBlockBreak(pl, block, ev);
    
    if (ev.isCancelled()) return;
    
    DTC.toolsManager.onBlockBreak(ev);
    
    DTC.game.handleBlockBreak(ev);
  }
  
  @EventHandler
  public void onEntityChangeBlock(EntityChangeBlockEvent ev) {
    if (ev.getEntity() instanceof FallingBlock fb) {
      DTC.game.handleFallenBlock(fb, ev.getBlock(), ev);
    }
  }
  
  @EventHandler
  public void onBlockForm(BlockFormEvent ev) {
    DTC.game.handleBlockForm(ev);
  }
  
  @EventHandler
  public void onBlockFromTo(BlockFromToEvent ev) {
    if (
      List.of(Material.WATER, Material.LAVA).contains(ev.getBlock().getType())
    ) {
      DTC.game.handleLiquidFlow(ev);
    }
  }
  
  @EventHandler
  public void onPlayerBucketEmpty(PlayerBucketEmptyEvent ev) {
    if (checkPaused(ev, ev.getPlayer())) return;
    
    DTC.game.handlePourLiquid(ev);
  }
  
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent ev) {
    DTC.game.handleJoinedPlayer(ev.getPlayer());
    
    DTC.boardsManager.onPlayerJoin(ev);
    DTC.advancementsManager.onPlayerJoin(ev);
  }
  
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent ev) {
    DTC.game.handleQuitedPlayer(ev.getPlayer());
    
    DTC.boardsManager.onPlayerQuit(ev);
    DTC.guiManager.onPlayerLeave(ev.getPlayer());
  }
  
  @EventHandler
  public void onPlayerTeleport(PlayerTeleportEvent ev) {
    if (checkPaused(ev, ev.getPlayer())) return;
    
    if (!LocUtils.isSameWorld(ev.getFrom(), ev.getTo())) {
      DTC.worldsManager.onPlayerChangeWorld(
        ev.getPlayer(),
        ev.getTo().getWorld()
      );
    }
  }
  
  @EventHandler
  public void onVehicleEnter(VehicleEnterEvent ev) {
    if (ev.getEntered() instanceof Player pl) {
      DTC.game.handlePlayerRide(pl, ev.getVehicle(), ev);
    }
  }
  
  @EventHandler
  public void onPlayerDropItem(PlayerDropItemEvent ev) {
    if (checkPaused(ev, ev.getPlayer())) return;
    
    Player pl = ev.getPlayer();
    ItemStack item = ev.getItemDrop().getItemStack();
    
    DTC.itemsManager.onPlayerDropItem(ev);
    
    DTC.game.handleDropItem(pl, item, ev);
  }
  
  @EventHandler
  public void onItemSpawn(ItemSpawnEvent ev) {
    Item entity = ev.getEntity();
    
    DTC.game.handleItemSpawn(entity, entity.getItemStack(), ev);
  }
  
  @EventHandler
  public void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent ev) {
    if (checkPaused(ev, ev.getPlayer())) return;
    
    DTC.game.handlePickupItem(ev);
  }
  
  @EventHandler
  public void onPlayerPickUpArrow(PlayerPickupArrowEvent ev) {
    if (checkPaused(ev, ev.getPlayer())) return;
    
    DTC.game.handlePickupArrow(ev);
  }
  
  @EventHandler
  public void onPlayerItemConsume(PlayerItemConsumeEvent ev) {
    DTC.game.handleItemUsed(ev.getPlayer(), ev.getItem(), ev);
  }
  
  @EventHandler
  public void onFoodLevelChange(FoodLevelChangeEvent ev) {
    if (checkPaused(ev, ev.getEntity())) return;
    
    DTC.game.handleFoodLevelChange(ev);
  }
  
  @EventHandler
  public void onInventoryClick(InventoryClickEvent ev) {
    if (!(ev.getWhoClicked() instanceof Player pl)) return;
    
    if (checkPaused(ev, pl)) return;
    
    Inventory inv = ev.getClickedInventory();
    if (inv == null) return;
    
    ItemStack item = ev.getCurrentItem();
    if (item == null) return;
    
    DTC.game.handleInventoryClick(
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
    DTC.game.handleInventoryOpen(ev);
  }
  
  @EventHandler
  public void onInventoryClose(InventoryCloseEvent ev) {
    DTC.game.handleInventoryClose(ev);
  }
  
  @EventHandler
  public void onPrepareItemCraft(PrepareItemCraftEvent ev) {
    DTC.game.handleCrafting(ev);
  }
  
  @EventHandler
  public void onPrepareAnvil(PrepareAnvilEvent ev) {
    DTC.game.handleRepair(ev);
  }
  
  @EventHandler
  public void onPrepareGrindstone(PrepareGrindstoneEvent ev) {
    DTC.game.handleGrinding(ev);
  }
  
  @EventHandler
  public void onPrepareItemEnchant(PrepareItemEnchantEvent ev) {
    DTC.game.handleEnchantingTableGenerate(ev);
  }
  
  @EventHandler
  public void onEnchantItem(EnchantItemEvent ev) {
    GuardRole.onEnchant(ev.getEnchanter());
    
    DTC.game.handleEnchant(ev);
  }
  
  @EventHandler
  public void onPlayerMove(PlayerMoveEvent ev) {
    if (ev.hasChangedPosition()) {
      if (checkPaused(ev, ev.getPlayer())) return;
      
      RangerRole.onPlayerMove(ev.getPlayer());
      AssassinRole.onPlayerMove(ev.getPlayer());
      
      DTC.game.handlePlayerMove(ev.getPlayer());
    }
  }
  
  @EventHandler
  public void onPlayerJump(PlayerJumpEvent ev) {
    MoleRole.onPlayerJump(ev.getPlayer());
  }
  
  @EventHandler
  public void onVehicleDamage(VehicleDamageEvent ev) {
    DTC.game.handleVehicleDamage(ev);
  }
  
  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent ev) {
    if (checkPaused(ev, ev.getDamager())) return;
    
    DTC.game.handleEntityDamage(ev);
  }
  
  @EventHandler
  public void onEntityExplode(EntityExplodeEvent ev) {
    DTC.game.handleExplosion(ev);
  }
  
  @EventHandler
  public void onBlockPistonExtend(BlockPistonExtendEvent ev) {
    DTC.game.handlePistonExtend(ev);
  }
  
  @EventHandler
  public void onBlockPistonRetract(BlockPistonRetractEvent ev) {
    DTC.game.handlePistonRetract(ev);
  }
  
  @EventHandler
  public void onEntityShootBow(EntityShootBowEvent ev) {
    if (checkPaused(ev, ev.getEntity())) return;
    
    for (ProjItemGen g : DTC.itemsManager.projGens.values()) {
      g.outerOnEntityShootBow(ev);
    }
    
    if (ev.getEntity() instanceof Player pl) {
      AssassinRole.onPlayerShootBow(pl, ev);
    }
  }
  
  @EventHandler
  public void onProjectileHit(ProjectileHitEvent ev) {
    WandererRole.onProjectileHit(ev);
    GrenadeGen.onProjectileHit(ev);
    
    for (ProjItemGen g : DTC.itemsManager.projGens.values()) {
      g.outerOnProjectileHit(
        ev
      );
    }
  }
  
  @EventHandler
  public void onEntityDeath(EntityDeathEvent ev) {
    KekkaiMasterRole.onEntityDeath(ev);
    DTC.game.handleEntityDeath(ev);
  }
  
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent ev) {
    DTC.glowManager.onPlayerDeath(ev);
    DTC.game.handlePlayerDeath(ev);
  }
  
  @EventHandler
  public void onAsyncChat(AsyncChatEvent ev) {
    if (
      ev.message() instanceof TextComponent tc
    ) DTC.quizManager.onPlayerChat(ev.getPlayer(), tc.content());
    DTC.game.handleChat(ev);
  }
  
  @EventHandler
  public void onBlockPhysics(BlockPhysicsEvent ev) {
    if (
      LocUtils.isSameWorld(
        ev.getBlock().getWorld(),
        DTC.worldsManager.lobby
      )
    ) ev.setCancelled(true);
  }
}
