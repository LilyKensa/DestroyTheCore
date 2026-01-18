package dev.huey.destroyTheCore.managers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.reflect.TypeToken;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.utils.CoreUtils;
import dev.huey.destroyTheCore.utils.LocationUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.*;
import java.util.function.Predicate;

public class GlowManager {
  enum MetadataBit {
    ON_FIRE  (0b00000001, Player::isVisualFire),
    SNEAKING (0b00000010, Player::isSneaking),
    RIDING   (0b00000100, p -> false), // This is unused
    SPRINTING(0b00001000, Player::isSprinting),
    SWIMMING (0b00010000, Player::isSwimming),
    INVISIBLE(0b00100000, Player::isInvisible),
    GLOWING  (0b01000000, Player::isGlowing),
    FLYING   (0b10000000, Player::isFlying);
    
    final byte bit;
    final Predicate<Player> predicate;
    
    MetadataBit(int bit, Predicate<Player> predicate) {
      this.bit = (byte) bit;
      this.predicate = predicate;
    }
  }
  
  ProtocolManager manager;
  
  Map<UUID, Set<UUID>> packetState = new HashMap<>();
  
  public void updatePacketState(Player viewer, Player target, boolean glow) {
    UUID vid = viewer.getUniqueId(), tid = target.getUniqueId();
    
    if (!packetState.containsKey(vid)) packetState.put(vid, new HashSet<>());
    Set<UUID> set = packetState.get(vid);

    if (glow)
      set.add(tid);
    else
      set.remove(tid);
  }
  
  boolean isWearingHat(Player pl) {
    return DestroyTheCore.itemsManager.checkGen(
      pl.getInventory().getHelmet(),
      ItemsManager.ItemKey.GOD_HELMET
    );
  }
  
  boolean shouldSeeGlow(Player viewer, Player target) {
    return target.isGlowing() || (
      isWearingHat(viewer) &&
      LocationUtils.near(target, viewer, 15)
    );
  }
  
  void resend(Player viewer, Player target, boolean glow) {
    PacketContainer packet = manager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
    packet.getIntegers().write(0, target.getEntityId());
    
    byte flags = 0;
    for (MetadataBit mb : MetadataBit.values()) {
      if (mb == MetadataBit.GLOWING) {
        if (glow) flags |= mb.bit;
      }
      else if (mb.predicate.test(target)) {
        flags |= mb.bit;
      }
    }
    
    List<WrappedDataValue> dataValues = new ArrayList<>();
    dataValues.add(new WrappedDataValue(
      0,
      WrappedDataWatcher.Registry.get(TypeToken.of(Byte.class).getType()),
      flags
    ));
    packet.getDataValueCollectionModifier().write(0, dataValues);
    
    manager.sendServerPacket(viewer, packet, false);
    updatePacketState(viewer, target, glow);
  }
  
  class CustomAdapter extends PacketAdapter {
    public CustomAdapter() {
      super(
        DestroyTheCore.instance,
        ListenerPriority.NORMAL,
        PacketType.Play.Server.ENTITY_METADATA
      );
    }
    
    @Override
    public void onPacketSending(PacketEvent ev) {
      PacketContainer packet = ev.getPacket().deepClone();
      Player viewer = ev.getPlayer();
      int entityId = packet.getIntegers().read(0);
      
      Player target = PlayerUtils.getPlayerByEntityId(entityId);
      if (target == null || target == viewer) return;
      
//      CoreUtils.log("Caught %s to %s".formatted(
//        target.getName(),
//        viewer.getName()
//      ));
      
      boolean glow = shouldSeeGlow(viewer, target);
      
      List<WrappedDataValue> dataValues = packet.getDataValueCollectionModifier().read(0);
      for (WrappedDataValue value : dataValues) {
        if (value.getIndex() != 0) continue;
        
        byte v = (byte) value.getValue();
//        CoreUtils.log("Value: %s".formatted(String.format("%8s", Integer.toBinaryString(v & 0xFF)).replace(' ', '0')));
        value.setValue((byte) (glow ? v | MetadataBit.GLOWING.bit : v & ~MetadataBit.GLOWING.bit));
      }
      packet.getDataValueCollectionModifier().write(0, dataValues);
      
      ev.setPacket(packet);
      updatePacketState(viewer, target, glow);
    }
  }
  
  public void init() {
    manager = ProtocolLibrary.getProtocolManager();
    manager.addPacketListener(new CustomAdapter());
  }
  
  void onParticleTick() {
    for (Player viewer : Bukkit.getOnlinePlayers()) {
      for (Player target : Bukkit.getOnlinePlayers()) {
        if (viewer == target) continue;
        
        UUID vid = viewer.getUniqueId(), tid = target.getUniqueId();
        
        if (!packetState.containsKey(vid)) packetState.put(vid, new HashSet<>());
        Set<UUID> set = packetState.get(vid);
        
        boolean last = set.contains(tid);
        boolean now = shouldSeeGlow(viewer, target);
        
        if (last != now) {
//          CoreUtils.log("Resending %s to %s: %c -> %c".formatted(
//            target.getName(),
//            viewer.getName(),
//            last ? 'O' : 'X',
//            now ? 'O' : 'X'
//          ));
          resend(viewer, target, now);
        }
      }
    }
  }
  
  void onPlayerDeath(PlayerDeathEvent ev) {
    for (Player p : Bukkit.getOnlinePlayers()) {
      CoreUtils.setTickOut(() -> {
        if (packetState.containsKey(p.getUniqueId()))
          resend(p, ev.getPlayer(), false);
      });
    }
  }
}
