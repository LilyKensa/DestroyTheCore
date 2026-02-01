package dev.huey.destroyTheCore.items.misc;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.utils.*;
import it.unimi.dsi.fastutil.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class LotteryGen extends UsableItemGen {
  
  public LotteryGen() {
    super(ItemsManager.ItemKey.LOTTERY, Material.HEART_OF_THE_SEA);
  }
  
  @Override
  public void use(Player pl, Block block) {
    PlayerData data = DestroyTheCore.game.getPlayerData(pl);
    
    if (!PlayerUtils.checkHandCooldown(pl)) return;
    PlayerUtils.setHandCooldown(pl, 20);
    
    PlayerUtils.takeOneItemFromHand(pl);
    
    final Location centerLoc = pl.getLocation().add(0, 0.1, 0);
    final int percentage = Math.floorMod(
      RandomUtils.nextInt(),
      100
    ) + data.lotteryShift;
    new BukkitRunnable() {
      int age = 0;
      Color color = Color.GRAY;
      
      @Override
      public void run() {
        if (age == 20) {
          int totalChance = 0;
          if (percentage < (totalChance += 15)) {
            data.lotteryShift += 2;
            giveTreasure(pl);
            color = Color.ORANGE;
          }
          else if (percentage < (totalChance += 35)) {
            data.lotteryShift += 1;
            giveNormal(pl);
            color = Color.FUCHSIA;
          }
          else {
            giveJunk(pl);
            color = Color.TEAL;
          }
          
          pl.playSound(
            pl.getLocation(),
            Sound.BLOCK_PISTON_EXTEND,
            1, // Volume
            1 // Pitch
          );
        }
        else if (age == 24) {
          pl.playSound(
            pl.getLocation(),
            Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
            1, // Volume
            1 // Pitch
          );
        }
        else if (age < 20 && age % 2 == 0) {
          pl.playSound(
            pl.getLocation(),
            Sound.BLOCK_DISPENSER_DISPENSE,
            0.5F, // Volume
            1 // Pitch
          );
        }
        
        ParticleUtils.ring(
          PlayerUtils.all(),
          centerLoc,
          Math.min(age, 20) * 0.1,
          color
        );
        
        if (age >= 25) cancel();
        age++;
        centerLoc.addRotation(5, 0);
      }
    }.runTaskTimer(DestroyTheCore.instance, 0, 1);
  }
  
  static final Component prefix = TextUtils.$("items.lottery.prefix");
  
  void announce(Component comp) {
    for (Player p : Bukkit.getOnlinePlayers()) {
      PlayerUtils.send(p, prefix.append(comp));
    }
  }
  
  void teamAnnounce(Player pl, Component comp) {
    for (Player p : PlayerUtils.getTeammates(pl)) {
      PlayerUtils.send(p, prefix.append(comp));
    }
  }
  
  void give(Player pl, ItemStack item, boolean big) {
    if (item == null) return;
    
    item.editMeta(meta -> {
      if (
        !DestroyTheCore.itemsManager.isGen(
          item
        )
          || DestroyTheCore.itemsManager.gens.get(
            ItemsManager.ItemKey.PLACEHOLDER
          ).checkItem(item)
      ) return;
      
      List<Component> lore = meta.lore();
      if (lore == null) {
        lore = new ArrayList<>();
      }
      
      if (
        !lore.isEmpty()
          && (lore.getLast() instanceof TextComponent lastLore)
          && !lastLore.content().startsWith(
            "-"
          )
      ) lore.add(Component.empty());
      lore.add(TextUtils.$("items.lottery.item-lore"));
      
      meta.lore(lore);
    });
    
    if (DestroyTheCore.game.getPlayerData(pl).alive) {
      pl.give(item);
    }
    else if (DestroyTheCore.game.map.spawnpoints != null) {
      pl.sendActionBar(TextUtils.$("items.lottery.sent-to-spawn"));
      pl.getWorld().dropItemNaturally(
        LocUtils.live(
          LocUtils.selfSide(
            LocUtils.toSpawnPoint(
              RandomUtils.pick(DestroyTheCore.game.map.spawnpoints)
            ),
            pl
          )
        ),
        item
      );
    }
    
    Consumer<Component> announcer = comp -> {
      if (big) {
        announce(comp);
      }
      else {
        teamAnnounce(pl, comp);
      }
    };
    
    announcer.accept(
      TextUtils.$(
        "items.lottery.announce.item",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.component(
            "item",
            item.effectiveName().colorIfAbsent(
              item.getItemMeta().hasRarity() ? item.getItemMeta().getRarity()
                .color() : NamedTextColor.WHITE
            )
          ),
          Placeholder.component("amount", Component.text(item.getAmount()))
        )
      )
    );
  }
  
  void give(Player pl, ItemStack item) {
    give(pl, item, false);
  }
  
  void giveRandom(Player pl, List<ItemStack> items, boolean big) {
    give(pl, RandomUtils.pick(items), big);
  }
  
  void giveRandom(Player pl, List<ItemStack> items) {
    give(pl, RandomUtils.pick(items), false);
  }
  
  ItemStack getCustomItem(ItemsManager.ItemKey key, int amount) {
    ItemStack item = DestroyTheCore.itemsManager.gens.get(key).getItem();
    item.setAmount(amount);
    return item;
  }
  
  ItemStack getCustomItem(ItemsManager.ItemKey key) {
    return getCustomItem(key, 1);
  }
  
  void giveTreasure(Player pl) {
    giveRandom(
      pl,
      List.of(
        getCustomItem(ItemsManager.ItemKey.ELYTRA),
        getCustomItem(ItemsManager.ItemKey.GOD_HELMET),
        getCustomItem(ItemsManager.ItemKey.GOD_CHESTPLATE),
        getCustomItem(ItemsManager.ItemKey.GOD_LEGGINGS),
        getCustomItem(ItemsManager.ItemKey.GOD_BOOTS),
        getCustomItem(ItemsManager.ItemKey.TRIDENT),
        new ItemStack(Material.TNT),
        getCustomItem(ItemsManager.ItemKey.ABSORPTION_POTION),
        new ItemStack(Material.TOTEM_OF_UNDYING),
        getCustomItem(ItemsManager.ItemKey.DAMAGE_ASSIST),
        getCustomItem(ItemsManager.ItemKey.LEVI_STICK),
        getCustomItem(ItemsManager.ItemKey.INK_ASSIST),
        getCustomItem(ItemsManager.ItemKey.ABSORPTION_HELMET),
        getCustomItem(ItemsManager.ItemKey.ASSIGN_RESPAWN_TIME, 3),
        new ItemStack(Material.ENCHANTED_GOLDEN_APPLE),
        new ItemStack(Material.EXPERIENCE_BOTTLE, 64),
        getCustomItem(ItemsManager.ItemKey.NETHERITE_SWORD),
        getCustomItem(ItemsManager.ItemKey.NETHERITE_AXE),
        new ItemStack(Material.GOLDEN_APPLE, 10),
        new ItemStack(Material.COBWEB, 5),
        new ItemStack(Material.VEX_SPAWN_EGG, 3),
        new ItemStack(Material.LAVA_BUCKET),
        new ItemStack(Material.ANCIENT_DEBRIS)
      ),
      true
    );
    
    pl.give(
      DestroyTheCore.itemsManager.gens.get(
        ItemsManager.ItemKey.RANDOM_ROLE
      ).getItem()
    );
  }
  
  void giveNormal(Player pl) {
    giveRandom(
      pl,
      List.of(
        new ItemStack(Material.EMERALD, 2),
        new ItemStack(Material.GOLD_INGOT, 15),
        new ItemStack(Material.IRON_INGOT, 30),
        new ItemStack(Material.COOKED_BEEF, 16),
        new ItemStack(Material.PUFFERFISH_BUCKET, 1),
        getCustomItem(ItemsManager.ItemKey.INVIS_POTION),
        new ItemStack(Material.SPONGE),
        new ItemStack(Material.EXPERIENCE_BOTTLE, 32),
        new ItemStack(Material.FIREWORK_ROCKET, 2),
        new ItemStack(Material.DIAMOND),
        new ItemStack(Material.SPECTRAL_ARROW, 16)
      )
    );
  }
  
  List<Pair<Integer, Consumer<Player>>> junk = List.of(
    Pair.of(10, pl -> give(pl, new ItemStack(Material.CAKE))),
    Pair.of(10, pl -> give(pl, new ItemStack(Material.EXPERIENCE_BOTTLE, 3))),
    Pair.of(10, pl -> give(pl, new ItemStack(Material.COOKED_CHICKEN))),
    Pair.of(10, pl -> give(pl, new ItemStack(Material.LEATHER))),
    Pair.of(10, pl -> give(pl, new ItemStack(Material.TURTLE_EGG))),
    Pair.of(10, pl -> give(pl, new ItemStack(Material.ARROW, 3))),
    Pair.of(10, pl -> give(pl, new ItemStack(Material.BONE_MEAL, 3))),
    Pair.of(10, pl -> give(pl, new ItemStack(Material.PUMPKIN_PIE))),
    Pair.of(10, pl -> give(pl, new ItemStack(Material.BEETROOT_SOUP))),
    Pair.of(
      10,
      pl -> give(pl, getCustomItem(ItemsManager.ItemKey.PLACEHOLDER))
    ),
    Pair.of(
      30,
      pl -> {
        for (Player p : PlayerUtils.getTeammates(pl)) {
          p.addPotionEffect(
            new PotionEffect(PotionEffectType.POISON, 5 * 20, 255, false, true)
          );
        }
        
        announce(
          TextUtils.$(
            "items.lottery.announce.poison",
            List.of(
              Placeholder.component("player", PlayerUtils.getName(pl)),
              Placeholder.component(
                "side",
                DestroyTheCore.game.getPlayerData(pl).side.titleComp()
              )
            )
          )
        );
      }
    ),
    Pair.of(
      5,
      pl -> {
        ItemStack item = getCustomItem(ItemsManager.ItemKey.SHAME_CHESTPLATE);
        pl.getInventory().setChestplate(item);
        
        announce(
          TextUtils.$(
            "items.lottery.announce.wear",
            List.of(
              Placeholder.component("player", PlayerUtils.getName(pl)),
              Placeholder.component("item", item.effectiveName())
            )
          )
        );
      }
    ),
    Pair.of(
      25,
      pl -> {
        teamAnnounce(
          pl,
          TextUtils.$(
            "items.lottery.announce.nothing",
            List.of(Placeholder.component("player", PlayerUtils.getName(pl)))
          )
        );
      }
    ),
    Pair.of(
      2,
      pl -> {
        for (int i = 0; i < 9; ++i) pl.getInventory().setItem(
          i,
          new ItemStack(Material.ROTTEN_FLESH)
        );
        
        announce(
          TextUtils.$(
            "items.lottery.announce.replace-hotbar",
            List.of(Placeholder.component("player", PlayerUtils.getName(pl)))
          )
        );
      }
    ),
    Pair.of(
      2,
      pl -> {
        List<? extends Player> teammates = PlayerUtils.getTeammates(pl);
        
        // Offset the sin value of killing teammates
        DestroyTheCore.game.getPlayerData(pl).addRespawnTime(
          -PlayerData.killPunishment * teammates.size()
        );
        
        for (Player p : teammates) {
          if (!PlayerUtils.shouldHandle(p)) continue;
          
          DestroyTheCore.damageManager.addDamage(pl, p, Double.MAX_VALUE);
          p.damage(
            Double.MAX_VALUE,
            DamageSource.builder(DamageType.MAGIC).build()
          );
        }
        
        CoreUtils.setTickOut(
          () -> {
            announce(
              TextUtils.$(
                "items.lottery.announce.kill",
                List.of(
                  Placeholder.component("player", PlayerUtils.getName(pl)),
                  Placeholder.unparsed(
                    "action",
                    RandomUtils.pick(
                      TextUtils.translateRaw(
                        "items.lottery.announce.kill-actions"
                      ).split("\\|")
                    )
                  ),
                  Placeholder.component(
                    "side",
                    DestroyTheCore.game.getPlayerData(pl).side.titleComp()
                  )
                )
              )
            );
          },
          2
        ); // Make sure this message comes after the kill messages
      }
    ),
    Pair.of(
      5,
      pl -> {
        List<? extends Player> teammates = PlayerUtils.getTeammates(pl);
        
        for (Player p : teammates) if (
          PlayerUtils.shouldHandle(p)
        ) PlayerUtils.teleportToSpawnPoint(p);
        
        CoreUtils.setTickOut(
          () -> {
            announce(
              TextUtils.$(
                "items.lottery.announce.send-spawn",
                List.of(
                  Placeholder.component("player", PlayerUtils.getName(pl)),
                  Placeholder.component(
                    "side",
                    DestroyTheCore.game.getPlayerData(pl).side.titleComp()
                  )
                )
              )
            );
          },
          2
        );
      }
    ),
    Pair.of(
      1,
      pl -> {
        for (
          PotionEffectType type : List.of(
            PotionEffectType.WITHER,
            PotionEffectType.REGENERATION,
            PotionEffectType.STRENGTH,
            PotionEffectType.WEAKNESS,
            PotionEffectType.SPEED,
            PotionEffectType.SLOWNESS,
            PotionEffectType.HASTE,
            PotionEffectType.MINING_FATIGUE,
            PotionEffectType.DARKNESS,
            PotionEffectType.HUNGER,
            PotionEffectType.DOLPHINS_GRACE,
            PotionEffectType.JUMP_BOOST,
            PotionEffectType.SLOW_FALLING
          )
        ) {
          pl.addPotionEffect(new PotionEffect(type, 10 * 20, 9, true, true));
        }
        
        announce(
          TextUtils.$(
            "items.lottery.announce.chaos",
            List.of(Placeholder.component("player", PlayerUtils.getName(pl)))
          )
        );
      }
    )
  );
  int junkSize = junk.stream().mapToInt(Pair::key).sum();
  
  void giveJunk(Player pl) {
    int rand = Math.floorMod(RandomUtils.nextInt(), junkSize);
    
    int sum = 0;
    for (Pair<Integer, Consumer<Player>> j : junk) {
      sum += j.key();
      if (sum > rand) {
        j.value().accept(pl);
        return;
      }
    }
  }
}
