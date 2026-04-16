package dev.huey.destroyTheCore.items.tokens;

import dev.huey.destroyTheCore.DTC;
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
  public boolean canUse(Player pl) {
    SideData sideData = DTC.game.getSideData(pl);
    
    if (sideData.usedTruce) {
      pl.sendActionBar(TextUtils.$("items.truce.duped-usage"));
      return false;
    }
    
    if (DTC.game.phase.isAfter(Game.Phase.DoubleDamage)) {
      pl.sendActionBar(TextUtils.$("items.truce.too-late"));
      return false;
    }
    
    return true;
  }
  
  @Override
  public void use(Player pl, Block block) {
    SideData sideData = DTC.game.getSideData(pl);
    
    sideData.usedTruce = true;
    
    DTC.game.truceTimer += 5 * 60 * 20;
    
    PlayerUtils.broadcast(
      TextUtils.$(
        "items.truce.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.component("item", getItem().effectiveName())
        )
      )
    );
    
    DTC.game.getPlayerData(pl).addExtraExp(25);
  }
}
