package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.bases.itemGens.AssistItemGen;
import dev.huey.destroyTheCore.bases.itemGens.ProjItemGen;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.items.armors.*;
import dev.huey.destroyTheCore.items.assistance.DamageAssistGen;
import dev.huey.destroyTheCore.items.assistance.InkAssistGen;
import dev.huey.destroyTheCore.items.assistance.SkillCooldownAssistGen;
import dev.huey.destroyTheCore.items.fragments.PlaceholderGen;
import dev.huey.destroyTheCore.items.fragments.SoulGen;
import dev.huey.destroyTheCore.items.gadgets.*;
import dev.huey.destroyTheCore.items.gui.ChooseRoleGen;
import dev.huey.destroyTheCore.items.gui.SpectatorTeleporterGen;
import dev.huey.destroyTheCore.items.misc.AbsorptionPotionGen;
import dev.huey.destroyTheCore.items.misc.InvisPotionGen;
import dev.huey.destroyTheCore.items.misc.LotteryGen;
import dev.huey.destroyTheCore.items.misc.WitchcraftGen;
import dev.huey.destroyTheCore.items.projectiles.IceArrowGen;
import dev.huey.destroyTheCore.items.projectiles.PoisonArrowGen;
import dev.huey.destroyTheCore.items.projectiles.WeaknessArrowGen;
import dev.huey.destroyTheCore.items.roles.*;
import dev.huey.destroyTheCore.items.tokens.*;
import dev.huey.destroyTheCore.items.wands.LeviStickGen;
import dev.huey.destroyTheCore.items.weapons.*;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class ItemsManager {
  
  /** Every item-gen's unique key */
  public enum ItemKey {
    // Armors
    ELYTRA,
    STARTER_HELMET,
    STARTER_CHESTPLATE,
    STARTER_LEGGINGS,
    STARTER_BOOTS,
    GOD_HELMET,
    GOD_CHESTPLATE,
    GOD_LEGGINGS,
    GOD_BOOTS,
    ABSORPTION_HELMET,
    SHAME_CHESTPLATE,
    // Weapons
    TRIDENT,
    NETHERITE_SWORD,
    NETHERITE_AXE,
    STARTER_SWORD,
    KB_STICK,
    // Assists,
    DAMAGE_ASSIST,
    INK_ASSIST,
    SKILL_COOLDOWN_ASSIST,
    // Gadgets
    ASSIGN_RESPAWN_TIME,
    ADD_CORE_HEALTH,
    BRIDGE_HELPER,
    GIVE_SPEED,
    GIVE_JUMP_BOOST,
    GIVE_STRENGTH,
    GRENADE,
    RANDOM_ROLE,
    // Wands
    LEVI_STICK,
    // Tokens
    ASSIGN_CLEAR_INV,
    ENEMY_GLOW,
    CORE_INVULN,
    IGNORE_CORE_INVULN,
    ASSIGN_MORE_RESPAWN_TIME,
    LAST_DITCH,
    RESPAWN_TEAMMATES,
    TRUCE,
    // Projectiles
    ICE_ARROW,
    WEAKNESS_ARROW,
    POISON_ARROW,
    // Misc
    ABSORPTION_POTION,
    INVIS_POTION,
    LOTTERY,
    WITCHCRAFT,
    // Fragments
    PLACEHOLDER,
    SOUL,
    // GUI
    CHOOSE_ROLE,
    SPECTATOR_TELEPORTER,
    // Roles
    GOLD_DIGGER_CHESTPLATE,
    RANGER_HELMET,
    KEKKAI_MASTER_LEGGINGS,
    CONSTRUCTOR_HELMET,
    PROVOCATEUR_HELMET,
  }
  
  /** Filter item-gens by type */
  <T extends ItemGen> Map<ItemKey, T> filterGens(Class<T> clazz) {
    return gens.entrySet().stream().filter(
      e -> clazz.isInstance(
        e.getValue()
      )
    ).collect(
      Collectors.toMap(Map.Entry::getKey, e -> clazz.cast(e.getValue()))
    );
  }
  
  public Map<ItemKey, ItemGen> gens;
  public Map<ItemKey, UsableItemGen> usableGens;
  public Map<ItemKey, AssistItemGen> assistGens;
  public Map<ItemKey, ProjItemGen> projGens;
  
  public void init() {
    gens = Stream.of(
      // Armors
      new ElytraGen(),
      new GodHelmetGen(),
      new GodChestplateGen(),
      new GodLeggingsGen(),
      new GodBootsGen(),
      new StarterHelmetGen(),
      new StarterChestplateGen(),
      new StarterLeggingsGen(),
      new StarterBootsGen(),
      new AbsorptionHelmetGen(),
      new ShameChestplateGen(),
      // Weapons
      new TridentGen(),
      new SwordGen(),
      new AxeGen(),
      new StarterSwordGen(),
      new KbStickGen(),
      // Assists
      new DamageAssistGen(),
      new InkAssistGen(),
      new SkillCooldownAssistGen(),
      // Gadgets
      new AssignRespawnTimeGen(),
      new AddCoreHealthGen(),
      new BridgeHelperGen(),
      new GiveSpeedGen(),
      new GiveJumpBoostGen(),
      new GiveStrengthGen(),
      new GrenadeGen(),
      new RandomRoleGen(),
      // Wands
      new LeviStickGen(),
      // Tokens
      new AssignClearInvGen(),
      new EnemyGlowGen(),
      new CoreInvulnGen(),
      new IgnoreCoreInvulnGen(),
      new AssignMoreRespawnTimeGen(),
      new LastDitchGen(),
      new RespawnTeammatesGen(),
      new TruceGen(),
      // Projectiles
      new IceArrowGen(),
      new WeaknessArrowGen(),
      new PoisonArrowGen(),
      // Misc
      new AbsorptionPotionGen(),
      new InvisPotionGen(),
      new LotteryGen(),
      new WitchcraftGen(),
      // Fragments
      new PlaceholderGen(),
      new SoulGen(),
      // GUI
      new ChooseRoleGen(),
      new SpectatorTeleporterGen(),
      // Roles
      new GoldDiggerChestplateGen(),
      new RangerHelmetGen(),
      new KekkaiMasterLeggingsGen(),
      new ConstructorHelmetGen(),
      new ProvocateurHelmetGen()
    ).collect(Collectors.toMap(ci -> ci.id, ci -> ci));
    
    usableGens = filterGens(UsableItemGen.class);
    assistGens = filterGens(AssistItemGen.class);
    projGens = filterGens(ProjItemGen.class);
  }
  
  public boolean isGen(ItemStack item) {
    return (item != null
      && item.hasItemMeta()
      && item.getItemMeta().getPersistentDataContainer().has(
        ItemGen.dataNamespace
      ));
  }
  
  /** Check if the item stack is an instance of the specific item-gen */
  public boolean checkGen(ItemStack item, ItemKey key) {
    return (isGen(item)
      && key.name().equals(
        item.getItemMeta().getPersistentDataContainer().get(
          ItemGen.dataNamespace,
          PersistentDataType.STRING
        )
      ));
  }
  
  /** Get an instance of a item-gen */
  public ItemGen getGen(ItemStack item) {
    if (!isGen(item)) return null;
    
    String id = item.getItemMeta().getPersistentDataContainer().get(
      ItemGen.dataNamespace,
      PersistentDataType.STRING
    );
    return gens.get(ItemKey.valueOf(id));
  }
  
  public void onUpdateTick() {
    for (Player pl : Bukkit.getOnlinePlayers()) {
      ItemStack item = pl.getInventory().getItemInOffHand();
      if (item.getType().isAir()) continue;
      
      for (AssistItemGen ig : assistGens.values()) {
        if (ig.checkItem(item)) ig.onEquippingTick(pl);
      }
    }
  }
  
  public void onPlayerDamage(Player attacker, Player victim) {
    if (
      !PlayerUtils.shouldHandle(attacker) || !PlayerUtils.shouldHandle(victim)
    ) return;
    
    ItemStack item = victim.getInventory().getItemInOffHand();
    
    for (AssistItemGen ig : assistGens.values()) {
      if (ig.checkItem(item)) ig.onAttack(victim, attacker);
    }
  }
  
  public void onPlayerInteract(PlayerInteractEvent ev) {
    if (
      ev.getHand() != EquipmentSlot.HAND
        || !(ev.getAction() == Action.RIGHT_CLICK_AIR
          || ev.getAction() == Action.RIGHT_CLICK_BLOCK)
    ) return;
    
    Player pl = ev.getPlayer();
    ItemStack item = ev.getItem();
    Block block = ev.getClickedBlock();
    
    if (PlayerUtils.checkUsingBlock(pl, block)) return;
    
    for (UsableItemGen ig : usableGens.values()) {
      if (!ig.checkItem(item)) continue;
      
      pl.swingMainHand();
      ig.use(pl, block);
      ev.setCancelled(true);
      return;
    }
  }
  
  public void onPlayerDropItem(PlayerDropItemEvent ev) {
    if (!PlayerUtils.shouldHandle(ev.getPlayer())) return;
    
    Item itemEntity = ev.getItemDrop();
    ItemStack item = itemEntity.getItemStack();
    if (isGen(item)) {
      ItemGen gen = getGen(item);
      if (gen.isTrash()) {
        itemEntity.remove();
      }
      if (gen.isBound()) {
        ev.setCancelled(true);
        return;
      }
    }
  }
}
