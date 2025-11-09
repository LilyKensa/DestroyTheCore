package dev.huey.destroyTheCore.bases;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.items.armors.StarterBootsGen;
import dev.huey.destroyTheCore.items.armors.StarterChestplateGen;
import dev.huey.destroyTheCore.items.armors.StarterHelmetGen;
import dev.huey.destroyTheCore.items.armors.StarterLeggingsGen;
import dev.huey.destroyTheCore.managers.GUIManager;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.utils.CoreUtils;
import dev.huey.destroyTheCore.utils.LocationUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Role extends GUIItem {
  /** Used to distinguish skill items, stored data is {@code true} */
  static public final NamespacedKey skillNamespace = new NamespacedKey(
    DestroyTheCore.instance,
    "skill"
  );
  /** Used to distinguish role-exclusive items, stored data is the name of {@link #id} */
  static public final NamespacedKey exclusiveItemNamespace = new NamespacedKey(
    DestroyTheCore.instance,
    "exclusive-item"
  );
  
  /** Prefixed send */
  static public void send(Player pl, Component message) {
    PlayerUtils.send(pl, TextUtils.$("role.prefix").append(message));
  }
  
  public RolesManager.RoleKey id;
  public String translationName;
  
  /**
   * Add extra settings using:<br>
   * - {@link #addInfo}<br>
   * - {@link #addFeature}<br>
   * - {@link #addExclusiveItem}<br>
   * - {@link #addSkill}
   */
  public Role(RolesManager.RoleKey id) {
    this.id = id;
    this.translationName = id.name().toLowerCase().replace('_', '-');
  }
  
  /** Translation with {@code %s} being the role's {@link #id} */
  String $r(String translateRoot) {
    return TextUtils.$r(translateRoot.formatted(translationName));
  }
  
  /** Multi-line version of {@link #$r} */
  List<String> $ra(String translateRoot) {
    List<String> list = new ArrayList<>();
    
    String key;
    for (int i = 1; true; ++i) {
      key = translateRoot.formatted(translationName) + "-" + i;
      
      if (DestroyTheCore.translationsManager.has(key))
        list.add(TextUtils.miniToRawCodes(
          DestroyTheCore.translationsManager.getRaw(key)
        ));
      else
        break;
    }
    
    return list;
  }
  
  public Material iconType;
  public String name;
  public List<String> lore;
  
  public List<String> featureDesc;
  
  public Material itemType;
  public String itemName;
  public String itemDesc;
  public Consumer<ItemMeta> itemMetaEditor;
  
  public String skillName;
  public List<String> skillDesc;
  public int skillCooldown;
  
  public void addInfo(Material iconType) {
    this.iconType = iconType;
    this.name = CoreUtils.stripColor($r("roles.%s.name"));
    this.lore = $ra("roles.%s.desc");
  }
  
  public void addFeature() {
    featureDesc = $ra("roles.%s.feature");
  }
  
  public void addExclusiveItem(Material type, Consumer<ItemMeta> editor) {
    itemType = type;
    itemName = $r("roles.%s.item.name");
    itemDesc = $r("roles.%s.item.detail");
    itemMetaEditor = editor;
  }
  public void addExclusiveItem(Material type) {
    addExclusiveItem(type, meta -> {});
  }
  
  public void addSkill(int cd) {
    skillName = CoreUtils.stripColor($r("roles.%s.skill.name"));
    skillDesc = $ra("roles.%s.skill.desc");
    skillCooldown = cd;
  }
  
  /** Announce that a player has changed to this role */
  public void announce(Player pl) {
    PlayerUtils.prefixedBroadcast(
      TextUtils.$("role.change", List.of(
        Placeholder.component("player", PlayerUtils.getName(pl)),
        Placeholder.unparsed("role", name)
      ))
    );
  }
  
  public ItemStack getSkillItem() {
    ItemStack item = new ItemStack(Material.KNOWLEDGE_BOOK);
    item.editMeta(meta -> {
      meta.setEnchantmentGlintOverride(true);
      
      meta.displayName(TextUtils.$("role.skill", List.of(
        Placeholder.unparsed("name", skillName)
      )));
      
      List<Component> lore = new ArrayList<>();
      for (String line : skillDesc)
        lore.add(Component.text(line)
          .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
      meta.lore(lore);
      
      meta.addItemFlags(
        ItemFlag.HIDE_ATTRIBUTES,
        ItemFlag.HIDE_ARMOR_TRIM,
        ItemFlag.HIDE_DYE,
        ItemFlag.HIDE_ADDITIONAL_TOOLTIP
      );
      
      meta.getPersistentDataContainer()
        .set(skillNamespace, PersistentDataType.BOOLEAN, true);
    });
    return item;
  }
  
  public ItemStack getExclusiveItem() {
    if (itemType == null) return ItemStack.empty();
    
    ItemStack item = new ItemStack(itemType);
    item.editMeta(meta -> {
      meta.setUnbreakable(true);
      
      meta.displayName(Component.text(itemName));
      meta.lore(List.of(TextUtils.$("role.item-lore", List.of(
        Placeholder.unparsed("role", name)
      ))));
      
      itemMetaEditor.accept(meta);
      
      meta.getPersistentDataContainer().set(
        exclusiveItemNamespace,
        PersistentDataType.STRING,
        this.id.name()
      );
    });
    return item;
  }
  
  /** Helmet on spawn, default to {@link StarterHelmetGen} */
  public ItemsManager.ItemKey defHelmet() {
    return ItemsManager.ItemKey.STARTER_HELMET;
  }
  /** Chestplate on spawn, default to {@link StarterChestplateGen} */
  public ItemsManager.ItemKey defChestplate() {
    return ItemsManager.ItemKey.STARTER_CHESTPLATE;
  }
  /** Leggings on spawn, default to {@link StarterLeggingsGen} */
  public ItemsManager.ItemKey defLeggings() {
    return ItemsManager.ItemKey.STARTER_LEGGINGS;
  }
  /** Boots on spawn, default to {@link StarterBootsGen} */
  public ItemsManager.ItemKey defBoots() {
    return ItemsManager.ItemKey.STARTER_BOOTS;
  }
  
  /** Used in {@link Game#onTick} */
  public void onTick(Player pl) {
  
  }
  
  /** @implNote Optional */
  public void onPhaseChange(Game.Phase phase, Player pl) {
  
  }
  
  /** Call this if the skill is successfully used */
  public void skillFeedback(Player pl) {
    pl.playSound(
      pl.getLocation(),
      Sound.BLOCK_CONDUIT_ACTIVATE,
      1, // Volume
      1 // Pitch
    );
    LocationUtils.ring(pl.getLocation().add(0, 0.1, 0), 0.8, loc -> {
      new ParticleBuilder(Particle.END_ROD)
        .allPlayers()
        .location(loc)
        .extra(0)
        .spawn();
    });
    pl.swingMainHand();
  }
  
  /** @implNote Required - The skill callback */
  public void useSkill(Player pl) {
    PlayerUtils.prefixedSend(pl, "This skill is not implemented yet!", NamedTextColor.RED);
  }
  
  /** @see GUIManager */
  @Override
  public ItemProvider getItemProvider() {
    List<String> combinedLore = new ArrayList<>();
    if (lore != null) {
      combinedLore.addAll(lore);
      combinedLore.add("");
    }
    if (featureDesc != null) {
      String type = "first";
      
      for (String line : featureDesc) {
        combinedLore.add(TextUtils.$r("role.desc.feature." + type, List.of(
          Placeholder.unparsed("desc", line)
        )));
        
        type = "others";
      }
    }
    if (itemName != null) {
      combinedLore.add(TextUtils.$r("role.desc.item", List.of(
        Placeholder.unparsed("name", CoreUtils.stripColor(itemName)),
        Placeholder.unparsed("detail", CoreUtils.stripColor(itemDesc))
      )));
    }
    if (!combinedLore.isEmpty() && !combinedLore.getLast().isEmpty())
      combinedLore.add("");
    if (skillName != null && skillDesc != null) {
      combinedLore.add(TextUtils.$r("role.skill", List.of(
        Placeholder.unparsed("name", skillName)
      )));
      combinedLore.addAll(skillDesc);
    }
    
    return new ItemBuilder(iconType)
      .setDisplayName("§e" + name)
      .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
      .addLoreLines(combinedLore.toArray(new String[0]));
  }
  
  /** @see GUIManager */
  @Override
  public void handleClick(ClickType clickType, Player pl, InventoryClickEvent ev) {
    announce(pl);
    pl.playSound(
      pl.getLocation(),
      Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
      1, // Volume
      1 // Pitch
    );
    
    DestroyTheCore.rolesManager.setRole(pl, this);
    closeWindow(pl);
  }
}
