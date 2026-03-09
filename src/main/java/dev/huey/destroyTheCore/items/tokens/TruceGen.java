package dev.huey.destroyTheCore.items.tokens;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.records.SideData;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TruceGen extends UsableItemGen {
  
  public TruceGen() {
    super(ItemsManager.ItemKey.TRUCE, Material.CRIMSON_SIGN, true);
  }
  
  @Override
  public void use(Player pl, Block block) {
    if (DestroyTheCore.game.phase.isAfter(Game.Phase.DoubleDamage)) {
      pl.sendActionBar(TextUtils.$("items.truce.too-late"));
      return;
    }
    
    SideData sideData = DestroyTheCore.game.getSideData(pl);
    if (sideData.usedTruce) {
      pl.sendActionBar(TextUtils.$("items.truce.duped-usage"));
      return;
    }
    
//    PlayerUtils.takeOneItemFromHand(pl);
    sideData.usedTruce = true;
    
    DestroyTheCore.game.truceTimer += 5 * 60 * 20;
    
    PlayerUtils.broadcast(
      TextUtils.$(
        "items.truce.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.component("item", getItem().effectiveName())
        )
      )
    );
  }
}
