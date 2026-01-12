package dev.huey.destroyTheCore.items.tokens;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.records.SideData;
import dev.huey.destroyTheCore.utils.LocationUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public class IgnoreCoreInvulnGen extends UsableItemGen {
  public IgnoreCoreInvulnGen() {
    super(
      ItemsManager.ItemKey.IGNORE_CORE_INVULN,
      Material.SPRUCE_SIGN,
      true
    );
  }
  
  @Override
  public void use(Player pl, Block block) {
    if (DestroyTheCore.game.map.core == null) return;
    
    PlayerData data = DestroyTheCore.game.getPlayerData(pl);
    SideData oppositeSideData = DestroyTheCore.game.getSideData(data.side.opposite());
    
    if (!oppositeSideData.isInvuln()) {
      pl.sendActionBar(TextUtils.$("items.ignore-core-invuln.no-effect"));
      return;
    }
    
    PlayerUtils.takeOneItemFromHand(pl);
    
    oppositeSideData.invulnTicks = 0;
    DestroyTheCore.boardsManager.refresh();
    
    Location coreLoc = LocationUtils.live(
      LocationUtils.selfSide(DestroyTheCore.game.map.core, data.side.opposite())
    );
    coreLoc.getBlock().setType(Material.END_STONE);
    
    PlayerUtils.broadcast(
      TextUtils.$("items.ignore-core-invuln.announce", List.of(
        Placeholder.component("player", PlayerUtils.getName(pl)),
        Placeholder.component("item", getItem().effectiveName())
      ))
    );
  }
}
