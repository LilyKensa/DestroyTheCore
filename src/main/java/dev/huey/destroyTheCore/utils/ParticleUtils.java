package dev.huey.destroyTheCore.utils;

import com.destroystokyo.paper.ParticleBuilder;
import java.util.List;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ParticleUtils {
  
  public static void dust(List<Player> players, Location loc, Color color) {
    new ParticleBuilder(Particle.DUST).receivers(players).location(loc).offset(
      0,
      0,
      0).count(1).color(color).spawn();
  }
  
  public static void cloud(List<Player> players, Location loc) {
    new ParticleBuilder(Particle.CLOUD).receivers(players).location(loc).offset(
      0.6,
      0.6,
      0.6).count(15).extra(0.05).spawn();
  }
  
  public static void simpleRegion(
                                  List<Player> players, Location loc1, Location loc2, Color color
  ) {
    if (!loc1.getWorld().getName().equals(loc2.getWorld().getName())) return;
    World world = loc1.getWorld();
    
    if (loc1.distanceSquared(loc2) > 750000) return;
    
    Location min = new Location(
      world,
      Math.min(loc1.getBlockX(), loc2.getBlockX()),
      Math.min(loc1.getBlockY(), loc2.getBlockY()),
      Math.min(loc1.getBlockZ(), loc2.getBlockZ())
    ), max = new Location(
      world,
      Math.max(loc1.getBlockX(), loc2.getBlockX()),
      Math.max(loc1.getBlockY(), loc2.getBlockY()),
      Math.max(loc1.getBlockZ(), loc2.getBlockZ())
    ).add(1, 1, 1);
    
    double step = 0.5;
    double xMin = min.getX(), xMax = max.getX();
    double yMin = min.getY(), yMax = max.getY();
    double zMin = min.getZ(), zMax = max.getZ();
    
    TriConsumer<Double, Double, Double> emitter = (x, y, z) -> {
      Location loc = new Location(world, x, y, z);
      dust(players, loc, color);
    };
    
    for (double y : new double[]{yMin, yMax}) {
      for (double z : new double[]{zMin, zMax}) {
        for (double x = xMin; x <= xMax; x += step) {
          emitter.accept(x, y, z);
        }
      }
    }
    
    for (double x : new double[]{xMin, xMax}) {
      for (double z : new double[]{zMin, zMax}) {
        for (double y = yMin; y <= yMax; y += step) {
          emitter.accept(x, y, z);
        }
      }
    }
    
    for (double x : new double[]{xMin, xMax}) {
      for (double y : new double[]{yMin, yMax}) {
        for (double z = zMin; z <= zMax; z += step) {
          emitter.accept(x, y, z);
        }
      }
    }
  }
  
  public static void block(List<Player> players, Location loc, Color color) {
    simpleRegion(players, loc, loc, color);
  }
  
  /** Region with 2 corners */
  public static void region(
                            List<Player> players, Location loc1, Location loc2, Color color0, Color color1, Color color2
  ) {
    if (!LocationUtils.isSameWorld(loc1, loc2)) return;
    World world = loc1.getWorld();
    
    if (loc1.distanceSquared(loc2) > 750000) return;
    
    block(players, loc1, color1);
    block(players, loc2, color2);
    
    Location min = LocationUtils.min(loc1, loc2), max = LocationUtils.max(loc1,
      loc2).add(1, 1, 1);
    
    double step = 0.5;
    double xMin = min.getX(), xMax = max.getX();
    double yMin = min.getY(), yMax = max.getY();
    double zMin = min.getZ(), zMax = max.getZ();
    
    TriConsumer<Double, Double, Double> emitter = (x, y, z) -> {
      Location loc = new Location(world, x, y, z);
      if (
        LocationUtils.closeEnough(loc, loc1) || LocationUtils.closeEnough(loc,
          loc2)
      ) return;
      
      dust(players, loc, color0);
    };
    
    for (double y : new double[]{yMin, yMax}) {
      for (double z : new double[]{zMin, zMax}) {
        for (double x = xMin; x <= xMax; x += step) {
          emitter.accept(x, y, z);
        }
      }
    }
    
    for (double x : new double[]{xMin, xMax}) {
      for (double z : new double[]{zMin, zMax}) {
        for (double y = yMin; y <= yMax; y += step) {
          emitter.accept(x, y, z);
        }
      }
    }
    
    for (double x : new double[]{xMin, xMax}) {
      for (double y : new double[]{yMin, yMax}) {
        for (double z = zMin; z <= zMax; z += step) {
          emitter.accept(x, y, z);
        }
      }
    }
  }
  
  public static void region(
                            List<Player> players, Location loc1, Location loc2
  ) {
    region(players, loc1, loc2, Color.GRAY, Color.YELLOW, Color.AQUA);
  }
  
  public static void ring(
                          List<Player> players, Location centerLoc, double radius, int count, Color color
  ) {
    LocationUtils.ring(
      centerLoc,
      radius,
      count,
      loc -> {
        dust(players, loc, color);
      }
    );
  }
  
  public static void ring(
                          List<Player> players, Location loc, double radius, Color color
  ) {
    ring(players, loc, radius, 16, color);
  }
  
  /** By Gemini */
  public static void spiralSphere(
                                  Location center, double radius, Particle particleType, int turns, int pointsPerTurn
  ) {
    final int totalPoints = turns * pointsPerTurn;
    
    final double pitchRad = Math.toRadians(-center.getPitch());
    final double yawRad = Math.toRadians(-center.getYaw());
    
    final double cosYaw = Math.cos(yawRad);
    final double sinYaw = Math.sin(yawRad);
    final double cosPitch = Math.cos(pitchRad);
    final double sinPitch = Math.sin(pitchRad);
    
    final double thetaIncrement = (2 * Math.PI) / pointsPerTurn;
    final double phiIncrement = Math.PI / totalPoints;
    
    for (int i = 0; i <= totalPoints; i++) {
      double phi = i * phiIncrement;
      double theta = i * thetaIncrement;
      
      double sinPhi = Math.sin(phi);
      double x = radius * sinPhi * Math.cos(theta);
      double y = radius * Math.cos(phi);
      double z = radius * sinPhi * Math.sin(theta);
      
      double x_yaw_rotated = x * cosYaw - z * sinYaw;
      double z_yaw_rotated = x * sinYaw + z * cosYaw;
      double y_yaw_rotated = y;
      
      double y_final = y_yaw_rotated * cosPitch - z_yaw_rotated * sinPitch;
      double z_final = y_yaw_rotated * sinPitch + z_yaw_rotated * cosPitch;
      double x_final = x_yaw_rotated;
      
      Location particleLoc = center.clone().add(x_final, y_final, z_final);
      
      new ParticleBuilder(particleType).allPlayers().location(
        particleLoc).count(1).extra(0).spawn();
    }
  }
  
  public static void spiralSphere(
                                  Location center, double radius, Particle particleType
  ) {
    spiralSphere(center, radius, particleType, 15, 30);
  }
}
