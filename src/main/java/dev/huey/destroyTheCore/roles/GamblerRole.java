package dev.huey.destroyTheCore.roles;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class GamblerRole extends Role {
  public GamblerRole() {
    super(RolesManager.RoleType.WORKING, RolesManager.RoleKey.GAMBLER);
    addInfo(Material.BROWN_BUNDLE);
    addFeature();
    addExclusiveItem(Material.NAME_TAG, meta -> {
      meta.addEnchant(Enchantment.LURE, 3, true);
    });
    addSkill(20 * 20);
    addLevelReq(8);
  }
  
  @Override
  public void onTick(Player pl) {
    if (DTC.ticksManager.ticksCount % (10 * 20) == 0) {
      DTC.game.getPlayerData(pl).addRespawnTime(-1);
    }
  }
  
  EnumSet<Material> acceptableTypes = EnumSet.of(
    Material.IRON_INGOT,
    Material.GOLD_INGOT,
    Material.REDSTONE,
    Material.EMERALD,
    Material.LAPIS_LAZULI,
    Material.DIAMOND
  );
  
  Component generatingComp = Component.text("JQK")
    .decorate(TextDecoration.OBFUSCATED)
    .color(NamedTextColor.LIGHT_PURPLE);
  
  enum Symbol {
    SEVEN(Component.text(">7<").color(NamedTextColor.YELLOW)),
    BAR(Component.text("BAR").color(NamedTextColor.GOLD)),
    CHERRY(Component.text("O^O").color(NamedTextColor.RED));
    
    final Component title;
    
    Symbol(Component title) {
      this.title = title;
    }
  }
  
  @Override
  public void useSkill(Player pl) {
    skillFeedback(pl);
    
    ItemStack offhandItem = pl.getInventory().getItemInOffHand();
    Material type = offhandItem.getType();
    int amount = offhandItem.getAmount();
    
    PlayerData data = DTC.game.getPlayerData(pl);
    
    if (!acceptableTypes.contains(type)) {
      pl.setCooldown(Material.KNOWLEDGE_BOOK, 10);
      
      data.skillReloadedMessage = true;
      pl.sendActionBar(TextUtils.$("roles.gambler.skill.no-material"));
      return;
    }
    
    if (amount < 10) {
      pl.setCooldown(Material.KNOWLEDGE_BOOK, 10);
      
      data.skillReloadedMessage = true;
      pl.sendActionBar(TextUtils.$("roles.gambler.skill.too-few"));
      return;
    }
    
    if (PlayerUtils.shouldHandle(pl)) {
      pl.getInventory().setItemInOffHand(ItemStack.empty());
    }
    
    PlayerUtils.auraBroadcast(
      pl.getLocation(),
      10,
      TextUtils.$(
        "roles.gambler.skill.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.unparsed("role", name),
          Placeholder.component(
            "item",
            offhandItem.effectiveName().color(NamedTextColor.AQUA)
          ),
          Placeholder.component("amount", Component.text(amount))
        )
      )
    );
    
    Symbol first = RandomUtils.pick(
      Symbol.SEVEN,
      Symbol.BAR,
      Symbol.BAR,
      Symbol.BAR,
      Symbol.CHERRY,
      Symbol.CHERRY,
      Symbol.CHERRY,
      Symbol.CHERRY,
      Symbol.CHERRY,
      Symbol.CHERRY
    );
    Symbol second = RandomUtils.pick(
      Symbol.SEVEN,
      Symbol.BAR,
      Symbol.BAR,
      Symbol.CHERRY,
      Symbol.CHERRY
    );
    Symbol third = RandomUtils.pick(
      Symbol.SEVEN,
      Symbol.SEVEN,
      Symbol.BAR,
      Symbol.BAR,
      Symbol.CHERRY
    );
    
    EnumMap<Symbol, Integer> count = new EnumMap<>(Symbol.class);
    for (Symbol symbol : new Symbol[]{
      first, second, third
    }) {
      count.put(symbol, count.getOrDefault(symbol, 0) + 1);
    }
    
    double rewardRatio;
    
    if (count.getOrDefault(Symbol.SEVEN, 0) >= 3) {
      rewardRatio = 25;
    }
    else if (count.getOrDefault(Symbol.BAR, 0) >= 3) {
      rewardRatio = 3;
    }
    else if (count.getOrDefault(Symbol.CHERRY, 0) >= 2) {
      rewardRatio = 1.5;
    }
    else {
      rewardRatio = 0;
    }
    
    new BukkitRunnable() {
      int age = 0;
      int stage = 0;
      
      @Override
      public void run() {
        if (age >= 4 * 20) {
          if (rewardRatio > 0) {
            PlayerUtils.give(pl, type, (int) Math.ceil(amount * rewardRatio));
          }
          
          PlayerUtils.normalTitleTimes(pl);
          pl.sendTitlePart(
            TitlePart.TITLE,
            TextUtils.$(
              "roles.gambler.skill.result." + (rewardRatio > 0 ? "good"
                : "bad"),
              List.of(
                Placeholder.component("ratio", Component.text(rewardRatio))
              )
            )
          );
          pl.sendTitlePart(TitlePart.SUBTITLE, Component.empty());
          
          pl.playSound(
            pl.getLocation(),
            Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
            1, // Volume
            1 // Pitch
          );
          
          cancel();
          return;
        }
        
        if (age % 20 == 0) {
          PlayerUtils.normalTitleTimes(pl);
          pl.sendTitlePart(
            TitlePart.TITLE,
            Component.join(
              JoinConfiguration.separator(
                Component.text(" - ").color(NamedTextColor.GRAY)
              ),
              Component.empty(),
              stage >= 1
                ? first.title
                : generatingComp,
              stage >= 2
                ? second.title
                : generatingComp,
              stage >= 3
                ? third.title
                : generatingComp,
              Component.empty()
            )
          );
          pl.sendTitlePart(TitlePart.SUBTITLE, Component.empty());
          
          stage++;
          
          pl.playSound(
            pl.getLocation(),
            Sound.BLOCK_PISTON_EXTEND,
            0.5F, // Volume
            1 // Pitch
          );
        }
        else if (age % 2 == 0) {
          pl.playSound(
            pl.getLocation(),
            Sound.BLOCK_DISPENSER_DISPENSE,
            0.5F, // Volume
            1 // Pitch
          );
        }
        
        age++;
      }
    }.runTaskTimer(DTC.instance, 0, 1);
  }
}
