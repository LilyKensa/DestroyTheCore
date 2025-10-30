package dev.huey.destroyTheCore.items.tokens;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class RespawnTeammatesGen extends UsableItemGen {
  public RespawnTeammatesGen() {
    super(
      ItemsManager.ItemKey.RESPAWN_TEAMMATES,
      Material.MANGROVE_SIGN
    );
  }
  
  @Override
  public void use(Player pl, Block block) {
    PlayerData data = DestroyTheCore.game.getPlayerData(pl);
    PlayerUtils.takeOneItemFromHand(pl);
    
    for (Player p : Bukkit.getOnlinePlayers()) {
      PlayerData d = DestroyTheCore.game.getPlayerData(p);
      if (!d.side.equals(data.side)) continue;
      
      if (!d.alive)
        PlayerUtils.respawn(p);
      
      PlayerUtils.fullyHeal(p);
      p.addPotionEffect(
        new PotionEffect(
          PotionEffectType.ABSORPTION,
          120 * 20,
          4,
          true,
          true
        )
      );
    }
    
    PlayerUtils.broadcast(
      TextUtils.$("items.respawn-teammates.announce", List.of(
        Placeholder.component("player", PlayerUtils.getName(pl)),
        Placeholder.component("item", getItem().effectiveName())
      ))
    );
  }
}
