package dev.huey.destroyTheCore.bases;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.items.starter.StarterBootsGen;
import dev.huey.destroyTheCore.items.starter.StarterChestplateGen;
import dev.huey.destroyTheCore.items.starter.StarterHelmetGen;
import dev.huey.destroyTheCore.items.starter.StarterLeggingsGen;
import dev.huey.destroyTheCore.managers.GUIManager;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.records.Stats;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemBuilder;
import xyz.xenondevs.invui.item.ItemProvider;

public class Role {
  
  /** Used to distinguish skill items, stored data is {@code true} */
  static public final NamespacedKey skillNamespace = new NamespacedKey(
    DTC.instance,
    "skill"
  );
  /**
   * Used to distinguish role-exclusive items, stored data is the name of
   * {@link #id}
   */
  static public final NamespacedKey exclusiveItemNamespace = new NamespacedKey(
    DTC.instance,
    "exclusive-item"
  );
  
  /** Prefixed send */
  static public void send(Player pl, Component message) {
    PlayerUtils.send(pl, TextUtils.$("role.prefix").append(message));
  }
  
  public RolesManager.RoleType type;
  public RolesManager.RoleKey id;
  public String translationName;
  
  /**
   * Add extra settings using:<br>
   * - {@link #addInfo}<br>
   * - {@link #addFeature}<br>
   * - {@link #addExclusiveItem}<br>
   * - {@link #addSkill}
   * - {@link #addLevelReq}
   */
  public Role(RolesManager.RoleType type, RolesManager.RoleKey id) {
    this.type = type;
    this.id = id;
    this.translationName = id.name().toLowerCase().replace('_', '-');
  }
  
  /** Translation with {@code %s} being the role's {@link #id} */
  Component $(String translateRoot) {
    return TextUtils.$(translateRoot.formatted(translationName));
  }
  
  /** Multi-line version of {@link #$} */
  List<Component> $a(String translateRoot) {
    List<Component> list = new ArrayList<>();
    
    String key;
    for (int i = 1; true; ++i) {
      key = "%s-%d".formatted(translateRoot.formatted(translationName), i);
      
      if (DTC.translationsManager.has(key))
        list.add(TextUtils.$(key));
      else
        break;
    }
    
    return list;
  }
  
  public Material iconType;
  public Component name;
  public List<Component> lore;
  
  public List<Component> featureDesc;
  
  public Material itemType;
  public Component itemName;
  public Component itemDesc;
  public Consumer<ItemMeta> itemMetaEditor;
  
  public Component skillName;
  public List<Component> skillDesc;
  public int skillCooldown;
  public double skillRadius = 0;
  
  public int levelReq;
  
  public void addInfo(Material iconType) {
    this.iconType = iconType;
    this.name = $("roles.%s.name").color(null);
    this.lore = $a("roles.%s.desc");
  }
  
  public void addFeature() {
    featureDesc = $a("roles.%s.feature").stream()
      .map(c -> c.color(null)).toList();
  }
  
  public void addExclusiveItem(Material type, Consumer<ItemMeta> editor) {
    itemType = type;
    itemName = $("roles.%s.item.name").color(null);
    itemDesc = $("roles.%s.item.detail").color(null);
    itemMetaEditor = editor;
  }
  
  public void addExclusiveItem(Material type) {
    addExclusiveItem(type, meta -> {
    });
  }
  
  public void addSkill(int cd, double radius) {
    skillName = $("roles.%s.skill.name").color(null);
    skillDesc = $a("roles.%s.skill.desc");
    skillCooldown = cd;
    skillRadius = radius;
  }
  
  public void addSkill(int cd) {
    addSkill(cd, 0);
  }
  
  public void addLevelReq(int lvl) {
    levelReq = lvl;
  }
  
  /** Announce that a player has changed to this role */
  public void announce(Player pl) {
    PlayerUtils.prefixedBroadcast(
      TextUtils.$(
        "role.change",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.component("role", name)
        )
      )
    );
  }
  
  public void editSkillItemMeta(ItemMeta meta) {
    meta.setEnchantmentGlintOverride(true);
    
    meta.displayName(
      TextUtils.$(
        "role.skill.title",
        List.of(Placeholder.component("name", skillName))
      )
    );
    
    List<Component> lore = new ArrayList<>();
    for (Component line : skillDesc)
      lore.add(
        line.decoration(TextDecoration.ITALIC, false)
      );
    
    lore.add(Component.empty());
    lore.add(
      TextUtils.$(
        "role.skill.cooldown",
        List.of(
          Placeholder.component(
            "cooldown",
            Component.text(skillCooldown / 20)
          )
        )
      )
    );
    
    meta.lore(lore);
    
    meta.addItemFlags(
      ItemFlag.HIDE_ATTRIBUTES,
      ItemFlag.HIDE_ARMOR_TRIM,
      ItemFlag.HIDE_DYE,
      ItemFlag.HIDE_ADDITIONAL_TOOLTIP
    );
    
    meta.getPersistentDataContainer().set(
      skillNamespace,
      PersistentDataType.BOOLEAN,
      true
    );
  }
  
  public ItemStack getSkillItem() {
    ItemStack item = new ItemStack(Material.KNOWLEDGE_BOOK);
    item.editMeta(this::editSkillItemMeta);
    return item;
  }
  
  public ItemStack getExclusiveItem() {
    if (itemType == null) return ItemStack.empty();
    
    ItemStack item = new ItemStack(itemType);
    item.editMeta(meta -> {
      meta.setUnbreakable(true);
      
      meta.displayName(itemName);
      meta.lore(
        List.of(
          TextUtils.$(
            "role.item-lore",
            List.of(Placeholder.component("role", name))
          )
        )
      );
      
      itemMetaEditor.accept(meta);
      
      meta.getPersistentDataContainer().set(
        exclusiveItemNamespace,
        PersistentDataType.STRING,
        id.name()
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
    DTC.game.getPlayerData(pl).skills++;
    
    pl.playSound(
      pl.getLocation(),
      Sound.BLOCK_CONDUIT_ACTIVATE,
      1, // Volume
      1 // Pitch
    );
    LocUtils.ring(
      pl.getLocation().add(0, 0.1, 0),
      0.8,
      loc -> {
        new ParticleBuilder(Particle.END_ROD)
          .allPlayers()
          .location(loc)
          .extra(0)
          .spawn();
      }
    );
    pl.swingMainHand();
  }
  
  /** @implNote Required - The skill callback */
  public void useSkill(Player pl) {
    PlayerUtils.prefixedSend(
      pl,
      "This skill is not implemented yet!",
      NamedTextColor.RED
    );
  }
  
  /** @see GUIManager */
  public ItemProvider getGuiItem(Player pl) {
    List<Component> combinedLore = new ArrayList<>();
    
    if (lore != null) {
      combinedLore.addAll(lore);
      combinedLore.add(Component.empty());
    }
    
    combinedLore.add(
      TextUtils.$(
        "role.desc.type",
        List.of(
          Placeholder.component(
            "type",
            TextUtils.$(
              "role.desc.types." + type.name().toLowerCase()
            ).color(null)
          )
        )
      )
    );
    
    if (featureDesc != null) {
      String featureLineType = "first";
      
      for (Component line : featureDesc) {
        combinedLore.add(
          TextUtils.$(
            "role.desc.feature." + featureLineType,
            List.of(Placeholder.component("desc", line))
          )
        );
        
        featureLineType = "others";
      }
    }
    
    if (itemName != null) {
      combinedLore.add(
        TextUtils.$(
          "role.desc.item",
          List.of(
            Placeholder.component("name", itemName),
            Placeholder.component("detail", itemDesc)
          )
        )
      );
    }
    
    if (!combinedLore.getLast().equals(Component.empty()))
      combinedLore.add(Component.empty());
    
    if (skillName != null && skillDesc != null) {
      combinedLore.add(
        TextUtils.$(
          "role.skill.title",
          List.of(Placeholder.component("name", skillName))
        )
      );
      combinedLore.addAll(skillDesc);
      combinedLore.add(
        TextUtils.$(
          "role.skill.cooldown",
          List.of(
            Placeholder.component(
              "cooldown",
              Component.text(skillCooldown / 20)
            )
          )
        )
      );
    }
    
    Stats stat = DTC.game.getStats(pl);
    
    return new ItemBuilder(iconType)
      .setCustomName(
        TextUtils.$(
          "role.name" + (stat.levels >= levelReq ? "" : "-locked"),
          List.of(
            Placeholder.component("name", name),
            Placeholder.component("levels", Component.text(levelReq))
          )
        )
      )
      .addModifier(item -> {
        item.addItemFlags(
          ItemFlag.HIDE_ARMOR_TRIM,
          ItemFlag.HIDE_ATTRIBUTES,
          ItemFlag.HIDE_DYE
        );
        return item;
      })
      .addLoreLines(
        combinedLore.toArray(new Component[0])
      );
  }
  
  /** @see GUIManager */
  public void handleClick(Item item, Gui gui, Click click) {
    Player pl = click.player();
    
    gui.closeForAllViewers();
    
    ItemStack handItem = pl.getInventory().getItemInMainHand();
    ItemGen gen = DTC.itemsManager.getGen(handItem);
    
    if (gen != null && gen.id == ItemsManager.ItemKey.CHOOSE_ROLE) {
      PlayerUtils.broadcast(
        TextUtils.$(
          "items.choose-role.announce",
          List.of(
            Placeholder.component("player", PlayerUtils.getName(pl)),
            Placeholder.component("item", gen.getItem().effectiveName()),
            Placeholder.component("role", name)
          )
        )
      );
      
      DTC.rolesManager.setRole(pl, this);
      DTC.game.enforceDisplay(pl);
      DTC.boardsManager.refresh(pl);
      return;
    }
    
    if (
      DTC.game.getStats(pl).levels >= levelReq || PlayerUtils.isAdmin(pl)
    ) {
      announce(pl);
      pl.playSound(
        pl.getLocation(),
        Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
        1, // Volume
        1 // Pitch
      );
      
      DTC.rolesManager.setRole(pl, this);
    }
    else {
      pl.playSound(
        pl.getLocation(),
        Sound.ENTITY_ENDERMAN_TELEPORT,
        1, // Volume
        1 // Pitch
      );
      pl.sendActionBar(TextUtils.$("items.choose-role.locked"));
    }
  }
}
