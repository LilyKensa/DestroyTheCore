package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.DTC;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.inventory.*;

public class RecipesManager {
  
  static public int smeltSpeedUp = 10;
  static public int smeltXpUp = 5;
  static public int smeltFuelTimeUp = 2;
  
  public NamespacedKey getKey(String id) {
    return new NamespacedKey(DTC.instance, id);
  }
  
  public void init() {
    Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
    List<Recipe> recipesToAdd = new ArrayList<>();
    while (it.hasNext()) {
      Recipe recipe = it.next();
      if (recipe instanceof CraftingRecipe cr) {
        if (cr.getResult().getType() == Material.GOLDEN_CARROT) {
          it.remove();
        }
        if (Tag.ITEMS_SPEARS.isTagged(recipe.getResult().getType())) {
          it.remove();
        }
      }
      if (recipe instanceof CookingRecipe<?> cr) {
        it.remove();
        cr.setCookingTime(Math.ceilDiv(cr.getCookingTime(), smeltSpeedUp));
        cr.setExperience(cr.getExperience() * smeltXpUp);
        recipesToAdd.add(cr);
      }
    }
    
    for (Recipe r : recipesToAdd) {
      Bukkit.getServer().addRecipe(r);
    }
    
    ShapedRecipe goldenCarrotRecipe = new ShapedRecipe(
      getKey("expensive_golden_carrot"),
      new ItemStack(Material.GOLDEN_CARROT)
    );
    goldenCarrotRecipe.shape(
      "GGG",
      "GCG",
      "GGG"
    );
    goldenCarrotRecipe.setIngredient('G', Material.GOLD_INGOT);
    goldenCarrotRecipe.setIngredient('C', Material.CARROT);
    
    Bukkit.getServer().addRecipe(goldenCarrotRecipe);
  }
  
  public void onFurnaceBurn(FurnaceBurnEvent ev) {
    ev.setBurnTime(
      Math.ceilDiv(ev.getBurnTime() * smeltFuelTimeUp, smeltSpeedUp)
    );
  }
}
