package dev.huey.destroyTheCore.roles;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.FoodProperties;
import java.util.*;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class FarmerRole extends Role {
  
  static public Map<Player, Integer> distribute(
    List<Player> players, int total
  ) {
    players = new ArrayList<>(players);
    Map<Player, Integer> distribution = new HashMap<>();
    
    if (players.isEmpty() || total <= 0) {
      return distribution;
    }
    
    int playerCount = players.size();
    int baseAmount = total / playerCount;
    int remainder = total % playerCount;
    
    Collections.shuffle(players);
    
    for (int i = 0; i < playerCount; i++) {
      int finalAmount = baseAmount + (i < remainder ? 1 : 0);
      
      if (finalAmount > 0) {
        distribution.put(players.get(i), finalAmount);
      }
    }
    
    return distribution;
  }
  
  public FarmerRole() {
    super(RolesManager.RoleType.WORKING, RolesManager.RoleKey.FARMER);
    addInfo(Material.CARROT);
    addFeature();
    addExclusiveItem(
      Material.IRON_HOE,
      meta -> {
        meta.addEnchant(Enchantment.FORTUNE, 3, true);
      }
    );
    addSkill(60 * 20);
  }
  
  @Override
  public void useSkill(Player pl) {
    skillFeedback(pl);
    
    PlayerData data = DTC.game.getPlayerData(pl);
    
    PlayerInventory inv = pl.getInventory();
    ItemStack offhandItem = inv.getItemInOffHand();
    
    int food = 0;
    float saturation = 0;
    
    if (offhandItem.getType().isEdible()) {
      FoodProperties props = offhandItem.getType().getDefaultData(
        DataComponentTypes.FOOD
      );
      food = props.nutrition();
      saturation = props.saturation();
    }
    
    if (food == 0) {
      PlayerUtils.setSkillCooldown(pl, 10);
      data.skillReloadedMessage = true;
      
      pl.sendActionBar(TextUtils.$("roles.farmer.skill.no-material"));
      return;
    }
    
    food *= offhandItem.getAmount();
    saturation *= offhandItem.getAmount();
    
    inv.setItemInOffHand(ItemStack.empty());
    
    List<Player> teammates = PlayerUtils.getTeammates(pl).stream()
      .filter(p -> p != pl)
      .toList();
    
    Map<Player, Integer> foodMap = distribute(teammates, food);
    
    float s = saturation / teammates.size();
    for (Player p : teammates) {
      int f = foodMap.get(p);
      
      PlayerUtils.delayAssign(
        pl,
        p,
        Particle.HAPPY_VILLAGER,
        () -> {
          p.setFoodLevel(p.getFoodLevel() + f);
          p.setSaturation(p.getSaturation() + s);
        }
      );
    }
    
    PlayerUtils.auraBroadcast(
      pl.getLocation(),
      10,
      TextUtils.$(
        "roles.farmer.skill.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.unparsed("role", name)
        )
      )
    );
  }
}
