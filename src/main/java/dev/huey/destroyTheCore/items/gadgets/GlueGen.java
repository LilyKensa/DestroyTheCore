package dev.huey.destroyTheCore.items.gadgets;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class GlueGen extends UsableItemGen {
  
  static final double maxBlocks = 24;
  static final double maxRadius = 4.6;
  static final Material blockType = Material.HONEY_BLOCK;
  static public final Particle particle = Particle.WAX_ON;
  
  static final double maxRadiusSq = Math.pow(maxRadius, 2);
  
  static public Set<Location> blockSet = new HashSet<>();
  
  static Set<Projectile> projs = new HashSet<>();
  
  static public boolean checkProj(Projectile proj) {
    return projs.contains(proj);
  }
  
  public static void onProjectileHit(ProjectileHitEvent ev) {
    Projectile proj = ev.getEntity();
    if (!checkProj(proj)) return;
    
    Block center;
    if (ev.getHitBlock() != null && ev.getHitBlockFace() != null) {
      center = ev.getHitBlock().getRelative(ev.getHitBlockFace());
    }
    else {
      center = proj.getLocation().getBlock();
    }
    
    proj.remove();
    
    new BukkitRunnable() {
      private final Queue<Block> queue = new ArrayDeque<>();
      private final Set<Block> visited = new HashSet<>();
      private int placedCount = 0;
      private boolean initialized = false;
      
      private final BlockFace[] faces = {
        BlockFace.UP, BlockFace.DOWN,
        BlockFace.NORTH, BlockFace.SOUTH,
        BlockFace.EAST, BlockFace.WEST
      };
      
      @Override
      public void run() {
        if (!initialized) {
          if (center.isPassable()) {
            queue.add(center);
            visited.add(center);
          }
          else {
            this.cancel();
            return;
          }
          initialized = true;
        }
        
        if (queue.isEmpty() || placedCount >= maxBlocks) {
          this.cancel();
          return;
        }
        
        int currentLayerSize = queue.size();
        for (int i = 0; i < currentLayerSize; i++) {
          if (placedCount >= maxBlocks) {
            this.cancel();
            return;
          }
          
          Block current = queue.poll();
          
          if (
            current.getLocation().distanceSquared(
              center.getLocation()
            ) >= maxRadiusSq
          ) {
            this.cancel();
            return;
          }
          
          if (current.isPassable()) {
            current.setType(blockType);
            blockSet.add(current.getLocation());
            placedCount++;
            
            for (BlockFace face : faces) {
              Block neighbor = current.getRelative(face);
              if (!visited.contains(neighbor) && neighbor.isPassable()) {
                visited.add(neighbor);
                queue.add(neighbor);
              }
            }
          }
        }
      }
    }.runTaskTimer(DTC.instance, 0L, 1L);
  }
  
  public static void onBlockDropItems(BlockDropItemEvent ev) {
    Block block = ev.getBlock();
    Location loc = block.getLocation();
    
    boolean contained = blockSet.remove(loc);
    if (contained) {
      ev.getItems().removeIf(
        itemEntity -> itemEntity.getItemStack().getType() == blockType
      );
    }
  }
  
  public static void onTick() {
    projs.removeIf((proj) -> {
      if (!proj.isValid()) return true;
      
      if (DTC.ticksManager.ticksCount % 2 == 0) {
        new ParticleBuilder(particle)
          .allPlayers()
          .location(proj.getLocation())
          .offset(0, 0, 0)
          .count(1)
          .extra(0)
          .spawn();
      }
      
      return false;
    });
  }
  
  public GlueGen() {
    super(ItemsManager.ItemKey.GLUE, Material.HONEY_BOTTLE);
  }
  
  @Override
  public void computeMeta(ItemMeta meta) {
    meta.setMaxStackSize(16);
  }
  
  @Override
  public void use(Player pl, Block block) {
    if (!PlayerUtils.checkHandCooldown(pl)) return;
    PlayerUtils.setHandCooldown(pl, 2 * 20);
    
    PlayerUtils.takeOneItemFromHand(pl);
    
    ThrowableProjectile proj = pl.launchProjectile(
      Snowball.class,
      pl.getLocation().getDirection()
    );
    proj.setItem(ItemStack.empty());
    projs.add(proj);
  }
}
