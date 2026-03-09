package dev.huey.destroyTheCore.records;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class Pos implements ConfigurationSerializable {
  double x, y, z;
  float yaw, pitch;
  
  public Pos(double x, double y, double z, float yaw, float pitch) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.yaw = yaw;
    this.pitch = pitch;
  }
  
  public Pos(double x, double y, double z) {
    this(x, y, z, 0, 0);
  }
  
  static public Pos of(Location loc) {
    return new Pos(
      loc.getX(),
      loc.getY(),
      loc.getZ(),
      loc.getYaw(),
      loc.getPitch()
    );
  }
  
  static public Pos of(Entity entity) {
    return of(entity.getLocation());
  }
  
  static public Pos of(Block block) {
    return of(block.getLocation());
  }
  
  public Vector toVec() {
    return new Vector(x, y, z);
  }
  
  public Location toLoc(World world) {
    return new Location(world, x, y, z, yaw, pitch);
  }
  
  public BlockPos toBlockPos() {
    return new BlockPos((int) x, (int) y, (int) z);
  }
  
  public Pos clone() {
    return new Pos(x, y, z, yaw, pitch);
  }
  
  @Override
  public Map<String, Object> serialize() {
    Map<String, Object> map = new HashMap<>();
    map.put("x", x);
    map.put("y", y);
    map.put("z", z);
    map.put("yaw", yaw);
    map.put("pitch", pitch);
    return map;
  }
  
  static public Pos deserialize(Map<String, Object> map) {
    double x = (double) map.get("x");
    double y = (double) map.get("y");
    double z = (double) map.get("z");
    float yaw = ((Number) map.get("yaw")).floatValue();
    float pitch = ((Number) map.get("pitch")).floatValue();
    
    return new Pos(x, y, z, yaw, pitch);
  }
  
  public double getX() {
    return x;
  }
  
  public double getY() {
    return y;
  }
  
  public double getZ() {
    return z;
  }
  
  public float getYaw() {
    return yaw;
  }
  
  public float getPitch() {
    return pitch;
  }
  
  public void setX(double x) {
    this.x = x;
  }
  
  public void setY(double y) {
    this.y = y;
  }
  
  public void setZ(double z) {
    this.z = z;
  }
  
  public void setYaw(float yaw) {
    this.yaw = yaw;
  }
  
  public void setPitch(float pitch) {
    this.pitch = pitch;
  }
  
  public void setPos(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public void setRotation(float yaw, float pitch) {
    this.yaw = yaw;
    this.pitch = pitch;
  }
  
  public int floorX() {
    return (int) x;
  }
  
  public int floorY() {
    return (int) y;
  }
  
  public int floorZ() {
    return (int) z;
  }
  
  public Pos add(double dx, double dy, double dz) {
    return new Pos(
      x + dx,
      y + dy,
      z + dz,
      yaw,
      pitch
    );
  }
  
  public Pos floor() {
    return new Pos(
      Math.floor(x),
      Math.floor(y),
      Math.floor(z),
      yaw,
      pitch
    );
  }
  
  public Pos center() {
    return floor().add(0.5, 0.5, 0.5);
  }
  
  public boolean isSameBlockAs(Pos that) {
    Pos a = this.floor();
    Pos b = that.floor();
    
    return a.x == b.x && a.y == b.y && a.z == b.z;
  }
  
  public double distSq(Pos that) {
    return Math.pow(this.x - that.x, 2) + Math.pow(this.y - that.y, 2) + Math
      .pow(this.z - that.z, 2);
  }
}
