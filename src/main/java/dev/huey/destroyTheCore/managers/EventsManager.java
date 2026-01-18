package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.itemGens.ProjItemGen;
import dev.huey.destroyTheCore.items.gadgets.GrenadeGen;
import dev.huey.destroyTheCore.roles.*;
import dev.huey.destroyTheCore.utils.LocationUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.List;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EventsManager implements Listener {
  
  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent ev) {
    DestroyTheCore.game.handleInteract(ev);
    DestroyTheCore.itemsManager.onPlayerInteract(ev);
    DestroyTheCore.toolsManager.onPlayerInteract(ev);
  }
  
  @EventHandler
  public void onPlayerInteractEntity(PlayerInteractEntityEvent ev) {
    DestroyTheCore.game.handleInteractEntity(ev);
  }
  
  @EventHandler
  public void onBlockPlace(BlockPlaceEvent ev) {
    DestroyTheCore.game.handleBlockPlace(ev);
  }
  
  @EventHandler
  public void onBlockBreak(BlockBreakEvent ev) {
    ConstructorRole.onBlockBreak(ev.getPlayer(), ev.getBlock());
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
    if (!LocationUtils.isSameWorld(ev.getFrom(), ev.getTo())) {
      DestroyTheCore.worldsManager.onPlayerChangeWorld(
        ev.getPlayer(),
        ev.getTo().getWorld()
      );
    }
  }
  
  @EventHandler
  public void onPlayerDropItem(PlayerDropItemEvent ev) {
    DestroyTheCore.itemsManager.onPlayerDropItem(ev);
  }
  
  @EventHandler
  public void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent ev) {
    DestroyTheCore.game.handlePickupItem(ev);
  }
  
  @EventHandler
  public void onPlayerPickUpArrow(PlayerPickupArrowEvent ev) {
    DestroyTheCore.game.handlePickupArrow(ev);
  }
  
  @EventHandler
  public void onFoodLevelChange(FoodLevelChangeEvent ev) {
    DestroyTheCore.game.handleHungry(ev);
  }
  
  @EventHandler
  public void onInventoryClick(InventoryClickEvent ev) {
    Inventory inv = ev.getClickedInventory();
    if (inv == null) return;
    
    ItemStack item = ev.getCurrentItem();
    if (item == null) return;
    
    ClickType click = ev.getClick();
    if (!(ev.getWhoClicked() instanceof Player pl)) return;
    
    DestroyTheCore.game.handleInventoryClick(inv, pl, item, click, ev);
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
  public void onPrepareItemEnchant(EnchantItemEvent ev) {
    GuardRole.onEnchant(ev.getEnchanter());
  }
  
  @EventHandler
  public void onPlayerMove(PlayerMoveEvent ev) {
    if (ev.hasChangedPosition()) {
      RangerRole.onPlayerMove(ev.getPlayer());
      AssassinRole.onPlayerMove(ev.getPlayer());
      DestroyTheCore.game.handlePlayerMove(ev.getPlayer());
    }
  }
  
  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent ev) {
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
    for (ProjItemGen g : DestroyTheCore.itemsManager.projGens.values())
      g.outerOnEntityShootBow(
        ev
      );
    if (ev.getEntity() instanceof Player pl) AssassinRole.onPlayerShootBow(
      pl,
      ev
    );
  }
  
  @EventHandler
  public void onProjectileHit(ProjectileHitEvent ev) {
    WandererRole.onProjectileHit(ev);
    GrenadeGen.onProjectileHit(ev);
    
    for (ProjItemGen g : DestroyTheCore.itemsManager.projGens.values())
      g.outerOnProjectileHit(
        ev
      );
  }
  
  @EventHandler
  public void onEntityDeath(EntityDeathEvent ev) {
    KekkaiMasterRole.onEntityDeath(ev);
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
      LocationUtils.isSameWorld(
        ev.getBlock().getWorld(),
        DestroyTheCore.worldsManager.lobby
      )
    ) ev.setCancelled(true);
  }
}
