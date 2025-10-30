package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.DestroyTheCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecipesManager {
  public NamespacedKey getKey(String id) {
    return new NamespacedKey(DestroyTheCore.instance, id);
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
      }
      if (recipe instanceof CookingRecipe<?> cr) {
        it.remove();
        cr.setCookingTime(Math.ceilDiv(cr.getCookingTime(), 10));
        recipesToAdd.add(cr);
      }
    }
    
    for (Recipe r : recipesToAdd)
      Bukkit.getServer().addRecipe(r);
    
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
}
