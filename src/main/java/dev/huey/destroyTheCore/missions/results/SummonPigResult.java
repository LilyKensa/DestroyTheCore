package dev.huey.destroyTheCore.missions.results;

import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class SummonPigResult extends Mission.Result {
  
  public SummonPigResult() {
    super("summon-pig");
  }
  
  @Override
  public void forLoser(Game.Side side) {
    Component name = TextUtils.$("mission.results.pig-name").color(null);
    
    announce(side, List.of(Placeholder.component("name", name)));
    
    for (Player p : PlayerUtils.getTeammates(side)) {
      PigZombie piggy = (PigZombie) p.getWorld().spawnEntity(p.getLocation(),
        EntityType.ZOMBIFIED_PIGLIN);
      
      piggy.setBaby();
      
      piggy.customName(name.color(side.color));
      piggy.setCustomNameVisible(true);
      
      piggy.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.32);
      piggy.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(0.1);
      piggy.getAttribute(Attribute.MAX_HEALTH).setBaseValue(50);
      piggy.setHealth(50);
      
      ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
      helmet.editMeta(uncastedMeta -> {
        LeatherArmorMeta meta = (LeatherArmorMeta) uncastedMeta;
        
        meta.setColor(side.dyeColor);
        meta.displayName(name.append(Component.text(" ☠")).color(side.color));
        meta.setUnbreakable(true);
        meta.addEnchant(Enchantment.PROTECTION, 2, true);
      });
      
      EntityEquipment eq = piggy.getEquipment();
      eq.setHelmet(helmet);
      eq.setDropChance(EquipmentSlot.HEAD, 1);
    }
  }
}
