package dev.huey.destroyTheCore.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class LocationUtils {
  public static boolean isSameWorld(World world1, World world2) {
    return world1.equals(world2);
  }
  public static boolean isSameWorld(Location loc1, Location loc2) {
    return isSameWorld(loc1.getWorld(), loc2.getWorld());
  }
  public static boolean isSameWorld(Entity e1, Entity e2) {
    return e1.getWorld().equals(e2.getWorld());
  }
  
  public static boolean isSameBlock(Location loc1, Location loc2) {
    return
      isSameWorld(loc1, loc2) &&
      loc1.getBlockX() == loc2.getBlockX() &&
      loc1.getBlockY() == loc2.getBlockY() &&
      loc1.getBlockZ() == loc2.getBlockZ();
  }
  
  static Location compareEachAxis(
    Location loc1, Location loc2,
    BiFunction<Integer, Integer, Integer> func
  ) {
    return new Location(
      loc1.getWorld(),
      func.apply(loc1.getBlockX(), loc2.getBlockX()),
      func.apply(loc1.getBlockY(), loc2.getBlockY()),
      func.apply(loc1.getBlockZ(), loc2.getBlockZ())
    );
  }
  public static Location min(Location loc1, Location loc2) {
    return compareEachAxis(loc1, loc2, Math::min);
  }
  public static Location max(Location loc1, Location loc2) {
    return compareEachAxis(loc1, loc2, Math::max);
  }
  
  public static Location toBlockCenter(Location loc) {
    return new Location(
      loc.getWorld(),
      loc.getBlockX() + 0.5f,
      loc.getBlockY() + 0.5f,
      loc.getBlockZ() + 0.5f,
      loc.getYaw(),
      loc.getPitch()
    );
  }
  
  /** {@link #toBlockCenter}, but it's 0.25 up from the ground, instead of 0.5 */
  public static Location toSpawnPoint(Location loc) {
    loc = toBlockCenter(loc).add(0, -0.25, 0);
    loc.setYaw(CoreUtils.snapAngle(loc.getYaw()));
    loc.setPitch(5);
    return loc;
  }
  
  public static boolean closeEnough(Location target, Location source) {
    source = toBlockCenter(source);
    return
      isSameWorld(target, source) &&
      Math.abs(target.getX() - source.getX()) < 0.6 &&
      Math.abs(target.getY() - source.getY()) < 0.6 &&
      Math.abs(target.getZ() - source.getZ()) < 0.6;
  }
  
  public static Location hitboxCenter(Entity e) {
    return e.getBoundingBox().clone().getCenter().toLocation(e.getWorld());
  }
  
  /** Ender chest animation, as we use custom ender chests */
  public static void playChestAnimation(Location loc, boolean open) {
    Block block = loc.getBlock();
    
    PacketContainer packet = ProtocolLibrary.getProtocolManager()
      .createPacket(PacketType.Play.Server.BLOCK_ACTION);
    
    // 1. Block Position (Location)
    packet.getBlockPositionModifier()
      .write(0, new BlockPosition(
        loc.getBlockX(),
        loc.getBlockY(),
        loc.getBlockZ()
      ));
    
    // 2. Action ID (1 for chest, door, etc.)
    packet.getIntegers().write(0, 1);
    
    // 3. Action Parameter (1 for open, 0 for close)
    packet.getIntegers().write(1, open ? 1 : 0);
    
    // 4. Block Type (Material enum) - ProtocolLib handles conversion to NMS Block
    packet.getBlocks().write(0, block.getType());
    
    // Send the packet to all players in the world who are near the block
    for (Player p : loc.getWorld().getPlayers()) {
      if (near(p.getLocation(), loc, 64)) {
        ProtocolLibrary.getProtocolManager().sendServerPacket(p, packet);
        p.playSound(
          loc,
          open ? Sound.BLOCK_ENDER_CHEST_OPEN : Sound.BLOCK_ENDER_CHEST_CLOSE,
          SoundCategory.BLOCKS,
          1, // Volume
          1 // Pitch
        );
      }
    }
  }
  
  /** Do something several times in a circle */
  public static void ring(Location centerLoc, double radius, int count, Consumer<Location> task) {
    double step = (2 * Math.PI) / count;
    
    for (int i = 0; i < count; i++) {
      double angle = centerLoc.getYaw() * Math.PI / 180 + i * step;
      
      double x = Math.cos(angle) * radius;
      double z = Math.sin(angle) * radius;
      
      Location loc = centerLoc.clone().add(x, 0, z);
      task.accept(loc);
    }
  }
  public static void ring(Location centerLoc, double radius, Consumer<Location> task) {
    ring(centerLoc, radius, 16, task);
  }
  
  /** Set a block at specific location, but in live world */
  public static void setLiveBlock(Location loc, Material type) {
    LocationUtils.live(loc).getBlock().setType(type);
    
    ParticleUtils.cloud(
      PlayerUtils.all(),
      LocationUtils.live(LocationUtils.toBlockCenter(loc))
    );
  }
  
  public static boolean near(Location loc1, Location loc2, double dist) {
    return
      isSameWorld(loc1, loc2) &&
      LocationUtils.toBlockCenter(loc1)
        .distanceSquared(LocationUtils.toBlockCenter(loc2)) <= dist * dist;
  }
  public static boolean near(Entity e1, Entity e2, double dist) {
    return near(e1.getLocation(), e2.getLocation(), dist);
  }
  
  public static boolean nearAnyCore(Location loc, int dist) {
    Location redCoreLoc = DestroyTheCore.game.map.core;
    if (redCoreLoc == null) return false;
    
    for (Location coreLoc : new Location[]{redCoreLoc, LocationUtils.flip(redCoreLoc)}) {
      if (near(loc, live(coreLoc), dist)) return true;
    }
    
    return false;
  }
  
  public static boolean nearSpawn(Location blockLoc) {
    for (Location point : DestroyTheCore.game.map.spawnpoints) {
      for (Location spawnLoc : new Location[]{
        point,
        LocationUtils.flip(point)
      }) {
        int
          sx = blockLoc.getBlockX(),
          sy = blockLoc.getBlockY(),
          sz = blockLoc.getBlockZ(),
          tx = spawnLoc.getBlockX(),
          ty = spawnLoc.getBlockY(),
          tz = spawnLoc.getBlockZ();
        
        if (
          sx >= tx - 1 && sx <= tx + 1 &&
            sy >= ty && sy <= ty + 2 &&
            sz >= tz - 1 && sz <= tz + 1
        ) {
          return true;
        }
      }
    }
    
    return false;
  }
  
  /** Flip X & Z, useful for red / green locations conversion */
  public static Location flip(Location originalLoc, boolean flip) {
    Location loc = originalLoc.clone();
    boolean hasFloatX = loc.getX() - loc.getBlockX() >= 0.1,
            hasFloatZ = loc.getZ() - loc.getBlockZ() >= 0.1;
    
    if (flip) {
      loc.setYaw(loc.getYaw() + 180);
      loc.setX(-loc.getX() + (hasFloatX ? 1 : 0));
      loc.setZ(-loc.getZ() + (hasFloatZ ? 1 : 0));
    }
    
    return loc;
  }
  public static Location flip(Location loc) {
    return flip(loc, true);
  }
  
  /** Flip if {@code side} is green */
  public static Location selfSide(Location loc, Game.Side side) {
    return flip(
      loc,
      side.equals(Game.Side.GREEN)
    );
  }
  public static Location selfSide(Location loc, Player pl) {
    return selfSide(
      loc,
      DestroyTheCore.game.getPlayerData(pl).side
    );
  }
  
  /** Flip if {@code side} is red */
  public static Location enemySide(Location loc, Game.Side side) {
    return flip(
      loc,
      !side.equals(Game.Side.GREEN)
    );
  }
  public static Location enemySide(Location loc, Player pl) {
    return enemySide(
      loc,
      DestroyTheCore.game.getPlayerData(pl).side
    );
  }
  
  /** A location's live world version */
  public static Location live(Location originalLoc) {
    if (DestroyTheCore.worldsManager.live == null) return originalLoc;
    
    Location loc = originalLoc.clone();
    loc.setWorld(DestroyTheCore.worldsManager.live);
    
    return loc;
  }
}

