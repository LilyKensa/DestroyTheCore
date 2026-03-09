package dev.huey.destroyTheCore.items.tokens;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.records.SideData;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class CoreInvulnGen extends UsableItemGen {
  
  public CoreInvulnGen() {
    super(ItemsManager.ItemKey.CORE_INVULN, Material.OAK_SIGN, true);
  }
  
  @Override
  public void use(Player pl, Block block) {
    if (DestroyTheCore.game.map.core == null) return;
    
//    PlayerUtils.takeOneItemFromHand(pl);
    
    SideData sideData = DestroyTheCore.game.getSideData(pl);
    sideData.invulnTicks += 60 * 20;
    DestroyTheCore.boardsManager.refresh();
    
    LocUtils.setLiveBlock(
      LocUtils.selfSide(DestroyTheCore.game.map.core, pl),
      Material.BEDROCK
    );
    
    PlayerUtils.broadcast(
      TextUtils.$(
        "items.core-invuln.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.component("item", getItem().effectiveName())
        )
      )
    );
  }
}
