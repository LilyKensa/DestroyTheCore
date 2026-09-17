package dev.huey.destroyTheCore.items.tokens;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.records.SideData;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class IgnoreCoreInvulnGen extends UsableItemGen {
  
  public IgnoreCoreInvulnGen() {
    super(ItemsManager.ItemKey.IGNORE_CORE_INVULN, Material.SPRUCE_SIGN, true);
  }
  
  @Override
  public boolean canUse(Player pl) {
    PlayerData data = DTC.game.getPlayerData(pl);
    SideData oppositeSideData = DTC.game.getSideData(
      data.side.opposite()
    );
    
    if (!oppositeSideData.isInvuln()) {
      pl.sendActionBar(TextUtils.$("items.ignore-core-invuln.no-effect"));
      return false;
    }
    
    return true;
  }
  
  @Override
  public void use(Player pl, Block block) {
    if (DTC.game.map.core == null) return;
    
    PlayerData data = DTC.game.getPlayerData(pl);
    SideData oppositeSideData = DTC.game.getSideData(
      data.side.opposite()
    );
    
    oppositeSideData.invulnTicks = 0;
    DTC.boardsManager.refresh();
    
    Location coreLoc = LocUtils.live(
      LocUtils.selfSide(DTC.game.map.core, data.side.opposite())
    );
    coreLoc.getBlock().setType(Material.END_STONE);
    
    PlayerUtils.broadcast(
      TextUtils.$(
        "items.ignore-core-invuln.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.component("item", getItem().effectiveName())
        )
      )
    );
    
    DTC.game.getPlayerData(pl).addExtraExp(25);
  }
}
