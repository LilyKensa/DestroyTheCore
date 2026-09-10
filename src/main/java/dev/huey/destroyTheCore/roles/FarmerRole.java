package dev.huey.destroyTheCore.roles;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.FoodProperties;
import java.util.*;
import java.util.stream.Collectors;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class FarmerRole extends Role {
  
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
    addSkill(90 * 20);
  }
  
  @Override
  public void useSkill(Player pl) {
    skillFeedback(pl);
    
    PlayerData data = DTC.game.getPlayerData(pl);
    
    PlayerInventory inv = pl.getInventory();
    ItemStack offhandItem = inv.getItemInOffHand();
    
    int singleFood = 0;
    float singleSatu = 0;
    
    if (offhandItem.getType().isEdible()) {
      FoodProperties props = offhandItem.getType().getDefaultData(
        DataComponentTypes.FOOD
      );
      singleFood = props.nutrition();
      singleSatu = props.saturation();
    }
    
    if (singleFood == 0) {
      PlayerUtils.setSkillCooldown(pl, 10);
      data.skillReloadedMessage = true;
      
      pl.sendActionBar(TextUtils.$("roles.farmer.skill.no-material"));
      return;
    }
    
    List<Player> teammates = PlayerUtils.getTeammates(pl).stream()
      .filter(p -> p != pl)
      .filter(p -> LocUtils.near(p, pl, 10))
      .collect(Collectors.toList());
    Collections.shuffle(teammates);
    
    Map<Player, Integer> virtualFood = new HashMap<>();
    Map<Player, Float> virtualSatu = new HashMap<>();
    
    for (Player p : teammates) {
      virtualFood.put(p, p.getFoodLevel());
      virtualSatu.put(p, p.getSaturation());
    }
    
    int used = 0;
    boolean next = true;
    
    distributionLoop: while (next) {
      next = false;
      
      for (Player p : teammates) {
        if (used >= offhandItem.getAmount()) break distributionLoop;
        
        int currentFood = virtualFood.get(p);
        float currentSatu = virtualSatu.get(p);
        if (currentFood < 20 || currentSatu < currentFood * 0.8) {
          int nextFood = Math.min(currentFood + singleFood, 20);
          float nextSatu = Math.min(currentSatu + singleSatu, nextFood);
          virtualFood.put(p, nextFood);
          virtualSatu.put(p, nextSatu);
          
          used++;
          next = true;
        }
      }
    }
    
    if (used == 0) {
      PlayerUtils.setSkillCooldown(pl, 10);
      data.skillReloadedMessage = true;
      
      pl.sendActionBar(TextUtils.$("roles.farmer.skill.all-full"));
      return;
    }
    
    for (Player p : teammates) {
      p.setFoodLevel(virtualFood.get(p));
      p.setSaturation(virtualSatu.get(p));
    }
    
    offhandItem.setAmount(offhandItem.getAmount() - used);
    
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
