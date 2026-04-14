package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.roles.*;
import dev.huey.destroyTheCore.utils.CoreUtils;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;

public class RolesManager {
  
  public enum RoleType {
    USELESS,
    ATTACKING,
    DEFENSE,
    WORKING,
    ASSISTANCE
  }
  
  public enum RoleKey {
    DEFAULT,
    ATTACKER,
    GUARD,
    GOLD_DIGGER,
    RANGER,
    KEKKAI_MASTER,
    WANDERER,
    ASSASSIN,
    CONSTRUCTOR,
    PROVOCATEUR,
    MOLE,
    JOCKEY,
    ROYAL,
    GLUTTON,
    HACKER
  }
  
  public Map<RoleKey, Role> roles;
  
  public void init() {
    roles = Stream.of(
      new DefaultRole(),
      new AttackerRole(),
      new GuardRole(),
      new GoldDiggerRole(),
      new KekkaiMasterRole(), // Lv 2
      new ProvocateurRole(), // Lv 3
      new JockeyRole(), // Lv 4
      new ConstructorRole(), // Lv 5
      new RoyalRole(), // Lv 6
      new AssassinRole(), // Lv 7
      new GluttonRole(), // Lv 8
      new RangerRole(), // Lv 9
      new WandererRole(), // Lv 10
      new HackerRole() // Lv 11
//      new MoleRole() // Lv 12
    ).collect(
      Collectors.toMap(r -> r.id, r -> r, (e, n) -> e, LinkedHashMap::new)
    );
  }
  
  public void setRole(Player pl, Role role) {
    PlayerData data = DTC.game.getPlayerData(pl);
    
    data.setRole(role);
    
    DTC.game.enforceTeam(pl);
    DTC.boardsManager.refresh(pl);
    
    if (!DTC.game.isPlaying) return;
    
    PlayerInventory inv = pl.getInventory();
    
    BiConsumer<EquipmentSlot, ItemsManager.ItemKey> replacer = (slot, key) -> {
      ItemStack replacement = DTC.itemsManager.gens
        .get(key).getItem();
      
      if (key.name().startsWith("STARTER")) {
        CoreUtils.dyeTeamColor(replacement, data.side);
      }
      
      ItemStack item = inv.getItem(slot);
      
      if (
        item.isEmpty()
          || (DTC.itemsManager.isGen(item)
            && DTC.itemsManager.getGen(item).isTrash())
      ) {
        inv.setItem(slot, replacement);
      }
      else if (
        replacement.hasItemMeta()
          && replacement.getItemMeta().hasEnchant(Enchantment.BINDING_CURSE)
      ) {
        pl.getWorld()
          .dropItemNaturally(LocUtils.hitboxCenter(pl), item)
          .setPickupDelay(20);
        inv.setItem(slot, replacement);
      }
    };
    
    replacer.accept(EquipmentSlot.HEAD, role.defHelmet());
    replacer.accept(EquipmentSlot.CHEST, role.defChestplate());
    replacer.accept(EquipmentSlot.LEGS, role.defLeggings());
    replacer.accept(EquipmentSlot.FEET, role.defBoots());
    
    ItemStack[] contents = inv.getContents();
    boolean hasItem = false;
    for (int i = 0; i < contents.length; ++i) {
      ItemStack item = contents[i];
      if (item == null) continue;
      
      if (
        item.hasItemMeta()
          && item.getItemMeta().getPersistentDataContainer()
            .has(Role.skillNamespace)
      ) {
        contents[i].editMeta(role::editSkillItemMeta);
      }
      if (isExclusiveItem(item)) {
        hasItem = true;
        contents[i] = role.getExclusiveItem();
      }
    }
    inv.setContents(contents);
    
    if (!hasItem) PlayerUtils.give(pl, role.getExclusiveItem());
    
    pl.setCooldown(
      Material.KNOWLEDGE_BOOK,
      Math.min(pl.getCooldown(Material.KNOWLEDGE_BOOK), role.skillCooldown)
    );
  }
  
  public boolean isExclusiveItem(ItemStack item) {
    return (item.hasItemMeta()
      && item.getItemMeta().getPersistentDataContainer().has(
        Role.exclusiveItemNamespace
      ));
  }
  
  public boolean checkExclusiveItem(ItemStack item, RoleKey key) {
    if (!isExclusiveItem(item)) return false;
    
    String roleIdStr = item.getItemMeta().getPersistentDataContainer().get(
      Role.exclusiveItemNamespace,
      PersistentDataType.STRING
    );
    
    return RolesManager.RoleKey.valueOf(roleIdStr).equals(key);
  }
  
  public boolean canTakeExclusiveItem(Player pl, ItemStack item) {
    PlayerData data = DTC.game.getPlayerData(pl);
    return !isExclusiveItem(item) || checkExclusiveItem(item, data.role.id);
  }
  
  public void onPhaseChange(Game.Phase phase) {
    for (Role role : roles.values())
      for (
        Player p : PlayerUtils.all().stream().filter(
          p -> DTC.game.getPlayerData(p).role.id.equals(
            role.id
          )
        ).toList()
      ) {
        role.onPhaseChange(phase, p);
      }
  }
}
