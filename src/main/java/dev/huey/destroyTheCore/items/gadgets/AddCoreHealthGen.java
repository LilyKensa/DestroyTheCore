package dev.huey.destroyTheCore.items.gadgets;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.records.SideData;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public class AddCoreHealthGen extends UsableItemGen {
  
  public AddCoreHealthGen() {
    super(ItemsManager.ItemKey.ADD_CORE_HEALTH, Material.DRAGON_BREATH);
  }
  
  @Override
  public void use(Player pl, Block block) {
    Game.Side side = DestroyTheCore.game.getPlayerData(pl).side;
    if (side.equals(Game.Side.SPECTATOR)) return;
    
    SideData sideData = DestroyTheCore.game.getSideData(side);
    
    if (sideData.coreHealth >= SideData.maxCoreHealth) {
      pl.sendActionBar(TextUtils.$("items.add-core-health.core-full"));
      return;
    }
    
    PlayerUtils.takeOneItemFromHand(pl);
    
    sideData.coreHealth++;
    
    PlayerUtils.broadcast(
      TextUtils.$(
        "items.add-core-health.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.component("item", getItem().effectiveName())
        )
      )
    );
    
    DestroyTheCore.boardsManager.refresh();
  }
}
