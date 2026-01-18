package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.roles.*;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;

public class RolesManager {
  public enum RoleKey {
    DEFAULT, ATTACKER, GUARD, GOLD_DIGGER, RANGER, KEKKAI_MASTER, WANDERER, ASSASSIN, CONSTRUCTOR, EATER, PROVOCATEUR, JOCKEY, NOBLE
  }
  
  public Map<RoleKey, Role> roles;
  
  public void init() {
    roles = Stream.of(
      new DefaultRole(),
      new AttackerRole(),
      new GuardRole(),
      new GoldDiggerRole(),
      new RangerRole(),
      new KekkaiMasterRole(),
      new AssassinRole(),
      new ConstructorRole(),
      new WandererRole(),
      new ProvocateurRole(),
      new JockeyRole(),
      new NobleRole()
    ).collect(Collectors.toMap(
      r -> r.id,
      r -> r,
      (e, n) -> e,
      LinkedHashMap::new
    ));
  }
  
  public void setRole(Player pl, Role role) {
    DestroyTheCore.game.getPlayerData(pl).setRole(role);
    DestroyTheCore.game.enforceTeam(pl);
    DestroyTheCore.boardsManager.refresh(pl);
    
    if (!DestroyTheCore.game.isPlaying) return;
    
    PlayerInventory inv = pl.getInventory();
    
    TriConsumer<Supplier<ItemStack>, Consumer<ItemStack>, ItemsManager.ItemKey> replacer = (getter, setter, key) -> {
      ItemStack replacement = DestroyTheCore.itemsManager.gens.get(
        key).getItem();
      
      if (key.name().startsWith("STARTER"))
        replacement.editMeta(uncastedMeta -> {
          LeatherArmorMeta meta = (LeatherArmorMeta) uncastedMeta;
          
          meta.setColor(DestroyTheCore.game.getPlayerData(pl).side.dyeColor);
          meta.addItemFlags(ItemFlag.HIDE_DYE);
        });
        
      if (
        getter.get() == null || getter.get().isEmpty() || (DestroyTheCore.itemsManager.isGen(
          getter.get()) && DestroyTheCore.itemsManager.getGen(
            getter.get()).isTrash())
      ) {
        setter.accept(replacement);
      }
      else if (
        replacement.hasItemMeta() && replacement.getItemMeta().hasEnchant(
          Enchantment.BINDING_CURSE)
      ) {
        pl.give(getter.get());
        setter.accept(replacement);
      }
    };
    
    replacer.accept(inv::getHelmet, inv::setHelmet, role.defHelmet());
    replacer.accept(inv::getChestplate,
      inv::setChestplate,
      role.defChestplate());
    replacer.accept(inv::getLeggings, inv::setLeggings, role.defLeggings());
    replacer.accept(inv::getBoots, inv::setBoots, role.defBoots());
    
    ItemStack[] contents = inv.getContents();
    boolean hasItem = false;
    for (int i = 0; i < contents.length; ++i) {
      ItemStack item = contents[i];
      if (item == null) continue;
      
      if (item.getType() == Material.KNOWLEDGE_BOOK) {
        contents[i] = role.getSkillItem();
      }
      if (isExclusiveItem(item)) {
        hasItem = true;
        contents[i] = role.getExclusiveItem();
      }
    }
    inv.setContents(contents);
    
    if (!hasItem)
      pl.give(role.getExclusiveItem());
    
    pl.setCooldown(
      Material.KNOWLEDGE_BOOK,
      Math.min(pl.getCooldown(Material.KNOWLEDGE_BOOK), role.skillCooldown)
    );
  }
  
  public boolean isExclusiveItem(ItemStack item) {
    return item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(
      Role.exclusiveItemNamespace);
  }
  
  public boolean canTakeExclusiveItem(Player pl, ItemStack item) {
    if (!isExclusiveItem(item)) return true;
    
    PlayerData data = DestroyTheCore.game.getPlayerData(pl);
    
    String roleIdStr = item.getItemMeta().getPersistentDataContainer().get(
      Role.exclusiveItemNamespace,
      PersistentDataType.STRING);
    
    return RolesManager.RoleKey.valueOf(roleIdStr).equals(data.role.id);
  }
  
  public void onPhaseChange(Game.Phase phase) {
    for (Role role : roles.values())
      for (Player p : PlayerUtils.all().stream().filter(
        p -> DestroyTheCore.game.getPlayerData(p).role.id.equals(role.id)
      ).toList()) {
        role.onPhaseChange(phase, p);
      }
  }
}
