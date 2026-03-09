package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.missions.InstantMission;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.RandomUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class DiscountTraderMission extends InstantMission {
  
  public DiscountTraderMission() {
    super("discount-trader");
  }
  
  @Override
  public void run() {
    WanderingTrader trader = (WanderingTrader) centerLoc.getWorld().spawnEntity(
      centerLoc,
      EntityType.WANDERING_TRADER
    );
    trader.customName(
      TextUtils.$("missions.discount-trader.trader").color(null)
    );
    trader.setCustomNameVisible(true);
    trader.setInvulnerable(true);
    
    trader.setCanDrinkPotion(false);
    trader.setCanDrinkMilk(false);
    trader.setCanPickupItems(false);
    
    List<MerchantRecipe> trades = new ArrayList<>();
    
    BiConsumer<Integer, ItemStack> adder = (price, item) -> {
      if (RandomUtils.hit(0.8)) return;
      
      MerchantRecipe recipe = new MerchantRecipe(item, 0, 1, true, 0, 1);
      recipe.addIngredient(new ItemStack(Material.GOLD_INGOT, price));
      
      trades.add(recipe);
    };
    
    BiFunction<ItemsManager.ItemKey, Integer, ItemStack> customGen = (
      key, count
    ) -> DestroyTheCore.itemsManager.gens.get(key).getItem(count);
    
    BiFunction<Enchantment, Integer, ItemStack> bookGen = (enchant, level) -> {
      ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
      item.editMeta(uncastedMeta -> {
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) uncastedMeta;
        meta.addStoredEnchant(enchant, level, true);
      });
      return item;
    };
    
    adder.accept(10, new ItemStack(Material.EXPERIENCE_BOTTLE, 64));
    adder.accept(30, new ItemStack(Material.DIAMOND, 5));
    adder.accept(15, customGen.apply(ItemsManager.ItemKey.LOTTERY, 15));
    adder.accept(16, customGen.apply(ItemsManager.ItemKey.KB_STICK, 1));
    adder.accept(3, new ItemStack(Material.TNT));
    adder.accept(5, new ItemStack(Material.COOKED_RABBIT, 64));
    adder.accept(5, new ItemStack(Material.COOKED_BEEF, 64));
    adder.accept(64, new ItemStack(Material.GOLDEN_APPLE, 16));
    adder.accept(5, customGen.apply(ItemsManager.ItemKey.RANDOM_ROLE, 1));
    adder.accept(10, bookGen.apply(Enchantment.AQUA_AFFINITY, 1));
    adder.accept(50, bookGen.apply(Enchantment.BANE_OF_ARTHROPODS, 5));
    adder.accept(40, bookGen.apply(Enchantment.BLAST_PROTECTION, 4));
    adder.accept(10, bookGen.apply(Enchantment.CHANNELING, 1));
    adder.accept(10, bookGen.apply(Enchantment.BINDING_CURSE, 1));
    adder.accept(10, bookGen.apply(Enchantment.VANISHING_CURSE, 1));
    adder.accept(30, bookGen.apply(Enchantment.DEPTH_STRIDER, 3));
    adder.accept(50, bookGen.apply(Enchantment.EFFICIENCY, 5));
    adder.accept(40, bookGen.apply(Enchantment.FEATHER_FALLING, 4));
    adder.accept(20, bookGen.apply(Enchantment.FIRE_ASPECT, 2));
    adder.accept(40, bookGen.apply(Enchantment.FIRE_PROTECTION, 4));
    adder.accept(10, bookGen.apply(Enchantment.FLAME, 1));
    adder.accept(30, bookGen.apply(Enchantment.FORTUNE, 3));
    adder.accept(20, bookGen.apply(Enchantment.FROST_WALKER, 2));
    adder.accept(50, bookGen.apply(Enchantment.IMPALING, 5));
    adder.accept(10, bookGen.apply(Enchantment.INFINITY, 1));
    adder.accept(20, bookGen.apply(Enchantment.KNOCKBACK, 2));
    adder.accept(30, bookGen.apply(Enchantment.LOOTING, 3));
    adder.accept(30, bookGen.apply(Enchantment.LOYALTY, 3));
    adder.accept(30, bookGen.apply(Enchantment.LUCK_OF_THE_SEA, 3));
    adder.accept(30, bookGen.apply(Enchantment.LURE, 3));
    adder.accept(10, bookGen.apply(Enchantment.MENDING, 1));
    adder.accept(10, bookGen.apply(Enchantment.MULTISHOT, 1));
    adder.accept(40, bookGen.apply(Enchantment.PIERCING, 4));
    adder.accept(50, bookGen.apply(Enchantment.POWER, 5));
    adder.accept(40, bookGen.apply(Enchantment.PROJECTILE_PROTECTION, 4));
    adder.accept(40, bookGen.apply(Enchantment.PROTECTION, 4));
    adder.accept(20, bookGen.apply(Enchantment.PUNCH, 2));
    adder.accept(30, bookGen.apply(Enchantment.QUICK_CHARGE, 3));
    adder.accept(30, bookGen.apply(Enchantment.RESPIRATION, 3));
    adder.accept(30, bookGen.apply(Enchantment.RIPTIDE, 3));
    adder.accept(30, bookGen.apply(Enchantment.QUICK_CHARGE, 3));
    adder.accept(50, bookGen.apply(Enchantment.SHARPNESS, 5));
    adder.accept(10, bookGen.apply(Enchantment.SILK_TOUCH, 1));
    adder.accept(50, bookGen.apply(Enchantment.SMITE, 5));
    adder.accept(30, bookGen.apply(Enchantment.SWEEPING_EDGE, 3));
    adder.accept(30, bookGen.apply(Enchantment.THORNS, 3));
    adder.accept(30, bookGen.apply(Enchantment.UNBREAKING, 3));
    
    trader.setRecipes(trades);
    
    DestroyTheCore.missionsManager.team.addEntity(trader);
  }
}
