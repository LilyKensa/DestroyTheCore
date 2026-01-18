package dev.huey.destroyTheCore.items.tokens;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.records.SideData;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import java.util.function.BiConsumer;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LastDitchGen extends UsableItemGen {
  
  public LastDitchGen() {
    super(ItemsManager.ItemKey.LAST_DITCH, Material.ACACIA_SIGN, true);
  }
  
  @Override
  public void use(Player pl, Block block) {
    PlayerData data = DestroyTheCore.game.getPlayerData(pl);
    SideData self = DestroyTheCore.game.getSideData(data.side),
      enemy = DestroyTheCore.game.getSideData(data.side.opposite());
    
    if (self.coreHealth > enemy.coreHealth - 30) {
      pl.sendActionBar(TextUtils.$("items.last-ditch.health-too-high"));
      return;
    }
    
    enemy.extraDamageTicks += 120 * 20;
    
    PlayerUtils.takeOneItemFromHand(pl);
    
    for (Player p : PlayerUtils.getTeammates(data.side)) {
      PlayerUtils.fullyHeal(p);
      
      BiConsumer<PotionEffectType, Integer> effectAdder = (type, amplifier) -> {
        p.addPotionEffect(
          new PotionEffect(type, 120 * 20, amplifier, true, true)
        );
      };
      
      effectAdder.accept(PotionEffectType.SPEED, 1);
      effectAdder.accept(PotionEffectType.STRENGTH, 0);
      effectAdder.accept(PotionEffectType.REGENERATION, 0);
    }
    
    PlayerUtils.broadcast(
      TextUtils.$(
        "items.last-ditch.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.component("item", getItem().effectiveName())
        )
      )
    );
  }
}
