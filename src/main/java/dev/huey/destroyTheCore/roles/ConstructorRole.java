package dev.huey.destroyTheCore.roles;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.utils.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ConstructorRole extends Role {
  
  static final ItemStack dummyAxe = new ItemStack(Material.NETHERITE_AXE);
  
  public static void onBlockBreak(Player pl, Block block) {
    if (
      DestroyTheCore.game.getPlayerData(
        pl).role.id != RolesManager.RoleKey.CONSTRUCTOR
    ) return;
    
    if (block.isPreferredTool(dummyAxe)) {
      if (
        Stream.of(Tag.PLANKS, Tag.WOODEN_SLABS, Tag.WOODEN_STAIRS).anyMatch(
          t -> t.isTagged(block.getType()))
      ) {
        pl.addPotionEffect(
          new PotionEffect(PotionEffectType.HASTE, 40, 0, true, false)
        );
      }
      
      if (RandomUtils.hit(0.05)) {
        pl.sendActionBar(TextUtils.$("roles.constructor.eat-wood"));
        pl.setFoodLevel(pl.getFoodLevel() + 2);
        pl.setSaturation(pl.getSaturation() + 1);
      }
    }
  }
  
  List<Set<Vector>> skillPlacePos = new ArrayList<>();
  
  public ConstructorRole() {
    super(RolesManager.RoleKey.CONSTRUCTOR);
    addInfo(Material.STONE_AXE);
    addFeature();
    addExclusiveItem(
      Material.STONE_AXE,
      meta -> {
        meta.addEnchant(Enchantment.EFFICIENCY, 7, true);
      }
    );
    addSkill(300 * 20);
    
    for (int i = 0; i < 11; ++i) skillPlacePos.add(new HashSet<>());
    
    for (int i = 0; i < 4; ++i) {
      for (int x : new int[]{i, -i}) for (
                                          int z = -i; z <= i; ++z
      ) skillPlacePos.get(i).add(new Vector(x, -1, z));
      for (int z : new int[]{i, -i}) for (
                                          int x = -i; x <= i; ++x
      ) skillPlacePos.get(i).add(new Vector(x, -1, z));
    }
    
    for (int i = 4; i < 7; ++i) {
      for (int x : new int[]{-3, 3}) for (
                                          int z = -3; z <= 3; ++z
      ) skillPlacePos.get(i).add(new Vector(x, i - 4, z));
      for (int z : new int[]{-3, 3}) for (
                                          int x = -3; x <= 3; ++x
      ) skillPlacePos.get(i).add(new Vector(x, i - 4, z));
    }
    
    for (int i = 7; i < 11; ++i) {
      for (int x : new int[]{-10 + i, 10 - i}) for (
                                                    int z = -10 + i; z <= 10 - i; ++z
      ) skillPlacePos.get(i).add(new Vector(x, 3, z));
      for (int z : new int[]{-10 + i, 10 - i}) for (
                                                    int x = -10 + i; x <= 10 - i; ++x
      ) skillPlacePos.get(i).add(new Vector(x, 3, z));
    }
  }
  
  @Override
  public ItemsManager.ItemKey defHelmet() {
    return ItemsManager.ItemKey.CONSTRUCTOR_HELMET;
  }
  
  @Override
  public void useSkill(Player pl) {
    skillFeedback(pl);
    
    PlayerUtils.auraBroadcast(
      pl.getLocation(),
      10,
      TextUtils.$(
        "roles.constructor.skill.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.unparsed("role", name)
        )
      )
    );
    
    new BukkitRunnable() {
      final Location startLoc = LocationUtils.toBlockCenter(pl.getLocation());
      int step = 0;
      
      @Override
      public void run() {
        for (Vector vec : skillPlacePos.get(step)) {
          Location loc = startLoc.clone().add(vec);
          if (loc.getBlock().isCollidable()) continue;
          if (LocationUtils.nearSpawn(loc)) continue;
          
          Location restCenter = LocationUtils.live(
            DestroyTheCore.game.map.restArea
          );
          if (restCenter != null) restCenter.setX(0);
          if (
            restCenter != null && LocationUtils.near(loc, restCenter, 6)
          ) continue;
          
          loc.getBlock().setType(Material.OAK_PLANKS);
          
          ParticleUtils.cloud(PlayerUtils.all(), loc);
        }
        
        step++;
        if (step >= skillPlacePos.size()) {
          cancel();
        }
      }
    }.runTaskTimer(DestroyTheCore.instance, 0, 2);
  }
}
