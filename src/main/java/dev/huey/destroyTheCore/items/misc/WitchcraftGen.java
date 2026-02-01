package dev.huey.destroyTheCore.items.misc;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WitchcraftGen extends UsableItemGen {
  
  public WitchcraftGen() {
    super(
      ItemsManager.ItemKey.WITCHCRAFT,
      Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE
    );
  }
  
  @Override
  public void computeMeta(ItemMeta meta) {
    meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
  }
  
  @Override
  public void use(Player pl, Block block) {
    PlayerData data = DestroyTheCore.game.getPlayerData(pl);
    
    if (!PlayerUtils.checkHandCooldown(pl)) return;
    PlayerUtils.setHandCooldown(pl, 20);
    
    PlayerUtils.takeOneItemFromHand(pl);
    
    switch (RandomUtils.range(12)) {
      case 0 -> {
        for (Player e : PlayerUtils.getEnemies(data.side)) {
          PlayerUtils.delayAssign(
            pl,
            e,
            Particle.WITCH,
            () -> {
              DestroyTheCore.inventoriesManager.dropOres(e);
            }
          );
        }
        
        announce(
          TextUtils.$(
            "items.witchcraft.announce.drop-ores",
            List.of(
              Placeholder.component("player", PlayerUtils.getName(pl)),
              Placeholder.component("enemy", data.side.opposite().titleComp())
            )
          )
        );
      }
      case 1 -> {
        for (Player e : PlayerUtils.getEnemies(data.side)) {
          PlayerUtils.delayAssign(
            pl,
            e,
            Particle.WITCH,
            () -> {
              e.addPotionEffect(
                new PotionEffect(
                  PotionEffectType.POISON,
                  10 * 20,
                  1,
                  false,
                  true
                )
              );
            }
          );
        }
        
        announce(
          TextUtils.$(
            "items.witchcraft.announce.poison",
            List.of(
              Placeholder.component("player", PlayerUtils.getName(pl)),
              Placeholder.component("enemy", data.side.opposite().titleComp())
            )
          )
        );
      }
      case 2 -> {
        for (Player e : PlayerUtils.getEnemies(data.side)) {
          PlayerUtils.delayAssign(
            pl,
            e,
            Particle.WITCH,
            () -> {
              e.setCooldown(
                Material.KNOWLEDGE_BOOK,
                e.getCooldown(Material.KNOWLEDGE_BOOK) + 60 * 20
              );
            }
          );
        }
        
        announce(
          TextUtils.$(
            "items.witchcraft.announce.add-skill-cooldown",
            List.of(
              Placeholder.component("player", PlayerUtils.getName(pl)),
              Placeholder.component("enemy", data.side.opposite().titleComp())
            )
          )
        );
      }
      case 3 -> {
        Player e = RandomUtils.pick(PlayerUtils.getEnemies(data.side));
        Role role = RandomUtils.pick(
          DestroyTheCore.rolesManager.roles.values().stream().filter(
            r -> r.id != RolesManager.RoleKey.DEFAULT
          ).toList()
        );
        
        PlayerUtils.delayAssign(
          pl,
          e,
          Particle.WITCH,
          () -> {
            DestroyTheCore.rolesManager.setRole(e, role);
            DestroyTheCore.game.enforceTeam(e);
            DestroyTheCore.boardsManager.refresh(e);
            
            announce(
              TextUtils.$(
                "items.witchcraft.announce.random-role",
                List.of(
                  Placeholder.component("player", PlayerUtils.getName(pl)),
                  Placeholder.component("target", PlayerUtils.getName(e)),
                  Placeholder.unparsed("role", role.name)
                )
              )
            );
          }
        );
      }
      case 4 -> {
        for (Player e : PlayerUtils.getEnemies(data.side)) {
          PlayerUtils.delayAssign(
            pl,
            e,
            Particle.WITCH,
            () -> {
              e.addPotionEffect(
                new PotionEffect(
                  PotionEffectType.UNLUCK,
                  60 * 20,
                  0,
                  false,
                  true
                )
              );
            }
          );
        }
        
        announce(
          TextUtils.$(
            "items.witchcraft.announce.unluck",
            List.of(
              Placeholder.component("player", PlayerUtils.getName(pl)),
              Placeholder.component("enemy", data.side.opposite().titleComp())
            )
          )
        );
      }
      case 5 -> {
        for (Player e : PlayerUtils.getEnemies(data.side)) {
          PlayerUtils.delayAssign(
            pl,
            e,
            Particle.WITCH,
            () -> {
              DestroyTheCore.game.getPlayerData(e).addRespawnTime(15);
            }
          );
        }
        
        announce(
          TextUtils.$(
            "items.witchcraft.announce.add-respawn-time",
            List.of(
              Placeholder.component("player", PlayerUtils.getName(pl)),
              Placeholder.component("enemy", data.side.opposite().titleComp())
            )
          )
        );
      }
      case 6 -> {
        DestroyTheCore.game.getSideData(data.side.opposite()).banOres(60 * 20);
        DestroyTheCore.game.noOresBars.show(data.side.opposite());
        DestroyTheCore.game.banOres(data.side.opposite());
        
        announce(
          TextUtils.$(
            "items.witchcraft.announce.ban-ores",
            List.of(
              Placeholder.component("player", PlayerUtils.getName(pl)),
              Placeholder.component("enemy", data.side.opposite().titleComp())
            )
          )
        );
      }
      case 7 -> {
        for (Player e : PlayerUtils.getEnemies(data.side)) {
          PlayerUtils.delayAssign(
            pl,
            e,
            Particle.WITCH,
            () -> {
              e.addPotionEffect(
                new PotionEffect(
                  PotionEffectType.GLOWING,
                  60 * 20,
                  0,
                  false,
                  true
                )
              );
            }
          );
        }
        
        announce(
          TextUtils.$(
            "items.witchcraft.announce.glow",
            List.of(
              Placeholder.component("player", PlayerUtils.getName(pl)),
              Placeholder.component("enemy", data.side.opposite().titleComp())
            )
          )
        );
      }
      case 8 -> {
        DestroyTheCore.game.getSideData(
          data.side.opposite()
        ).directAttackCore();
        DestroyTheCore.game.checkWinner();
        
        announce(
          TextUtils.$(
            "items.witchcraft.announce.attack-core",
            List.of(
              Placeholder.component("player", PlayerUtils.getName(pl)),
              Placeholder.component("enemy", data.side.opposite().titleComp())
            )
          )
        );
      }
      case 9 -> {
        for (int i = 0; i < 3; ++i) DestroyTheCore.game.getSideData(
          data.side.opposite()
        ).directAttackCore();
        
        announce(
          TextUtils.$(
            "items.witchcraft.announce.attack-core-3",
            List.of(
              Placeholder.component("player", PlayerUtils.getName(pl)),
              Placeholder.component("enemy", data.side.opposite().titleComp())
            )
          )
        );
      }
      case 10 -> {
        for (Player e : PlayerUtils.getEnemies(data.side)) {
          PlayerUtils.delayAssign(
            pl,
            e,
            Particle.WITCH,
            () -> {
              e.addPotionEffect(
                new PotionEffect(
                  PotionEffectType.SLOWNESS,
                  30 * 20,
                  1,
                  false,
                  true
                )
              );
            }
          );
        }
        
        announce(
          TextUtils.$(
            "items.witchcraft.announce.slowness",
            List.of(
              Placeholder.component("player", PlayerUtils.getName(pl)),
              Placeholder.component("enemy", data.side.opposite().titleComp())
            )
          )
        );
      }
      case 11 -> {
        for (Player e : PlayerUtils.getEnemies(data.side)) {
          PlayerUtils.delayAssign(
            pl,
            e,
            Particle.WITCH,
            () -> {
              e.addPotionEffect(
                new PotionEffect(
                  PotionEffectType.MINING_FATIGUE,
                  30 * 20,
                  0,
                  false,
                  true
                )
              );
            }
          );
        }
        
        announce(
          TextUtils.$(
            "items.witchcraft.announce.mining-fatigue",
            List.of(
              Placeholder.component("player", PlayerUtils.getName(pl)),
              Placeholder.component("enemy", data.side.opposite().titleComp())
            )
          )
        );
      }
    }
  }
  
  static final Component prefix = TextUtils.$("items.witchcraft.prefix");
  
  void announce(Component comp) {
    for (Player p : Bukkit.getOnlinePlayers()) {
      PlayerUtils.send(p, prefix.append(comp));
    }
  }
}
