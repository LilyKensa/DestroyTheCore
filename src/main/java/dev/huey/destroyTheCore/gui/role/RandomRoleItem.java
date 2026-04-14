package dev.huey.destroyTheCore.gui.role;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.GUIItem;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class RandomRoleItem extends GUIItem {
  
  @Override
  public ItemProvider getItemProvider() {
    return new ItemBuilder(Material.REDSTONE).setDisplayName(
      TextUtils.$r(
        "gui.buttons.pick-random.title"
      )
    );
  }
  
  @Override
  public void handleClick(ClickType click, Player pl, InventoryClickEvent ev) {
    Role role = RandomUtils.pick(
      DTC.rolesManager.roles.values().stream().filter(
        r -> r.id != RolesManager.RoleKey.DEFAULT &&
          r.levelReq <= DTC.game.stats
            .get(
              pl.getUniqueId()
            ).levels
      ).toList()
    );
    
    PlayerUtils.prefixedBroadcast(
      TextUtils.$(
        "gui.buttons.pick-random.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.unparsed("role", role.name)
        )
      )
    );
    pl.playSound(
      pl.getLocation(),
      Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
      1, // Volume
      1 // Pitch
    );
    
    DTC.rolesManager.setRole(pl, role);
    DTC.game.enforceTeam(pl);
    DTC.boardsManager.refresh(pl);
    
    closeWindow(pl);
  }
}
