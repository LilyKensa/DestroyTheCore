package dev.huey.destroyTheCore.items.gadgets;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.LocationUtils;
import dev.huey.destroyTheCore.utils.ParticleUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Ladder;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BridgeHelperGen extends UsableItemGen {
  
  public BridgeHelperGen() {
    super(ItemsManager.ItemKey.BRIDGE_HELPER, Material.BRICK);
  }
  
  public static boolean growPillar(Location loc) {
    Block centerBlock = loc.getBlock();
    
    Set<BlockFace> faces = Set.of(
      BlockFace.EAST,
      BlockFace.NORTH,
      BlockFace.WEST,
      BlockFace.SOUTH
    );
    
    if (
      !centerBlock.getType().isAir() || centerBlock.getY() < centerBlock.getWorld().getMinHeight() || centerBlock.getY() > centerBlock.getWorld().getMaxHeight()
    ) {
      return false;
    }
    
    centerBlock.setType(Material.BRICKS);
    
    for (BlockFace face : faces) {
      Block block = centerBlock.getRelative(face);
      
      if (block.isCollidable()) continue;
      
      block.setType(Material.LADDER);
      
      if (block.getBlockData() instanceof Ladder ladderData) {
        ladderData.setFacing(face);
        block.setBlockData(ladderData);
      }
    }
    
    return true;
  }
  
  @Override
  public void use(Player pl, Block block) {
    if (block == null) {
      pl.sendActionBar(TextUtils.$("items.bridge-helper.no-block"));
      return;
    }
    
    PlayerUtils.takeOneItemFromHand(pl);
    
    new BukkitRunnable() {
      int duration = 15;
      final Location loc = block.getLocation();
      
      @Override
      public void run() {
        loc.add(0, 1, 0);
        
        boolean success = growPillar(loc);
        if (!success) {
          cancel();
          return;
        }
        
        ParticleUtils.cloud(
          PlayerUtils.all(),
          LocationUtils.toBlockCenter(loc).add(0, -0.5, 0)
        );
        
        duration--;
        if (duration <= 0) cancel();
      }
    }.runTaskTimer(DestroyTheCore.instance, 0, 2);
  }
}
