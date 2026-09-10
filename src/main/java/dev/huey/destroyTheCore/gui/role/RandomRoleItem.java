package dev.huey.destroyTheCore.gui.role;

import dev.huey.destroyTheCore.DTC;
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
import xyz.xenondevs.invui.item.BoundItem;
import xyz.xenondevs.invui.item.ItemBuilder;

public class RandomRoleItem {
  
  static public final BoundItem it = BoundItem.builder()
    .setItemProvider(
      (pl, gui) -> new ItemBuilder(Material.REDSTONE)
        .setCustomName(TextUtils.$("gui.buttons.pick-random.title"))
    )
    .addClickHandler((item, gui, click) -> {
      onClick(click.player());
      gui.closeForAllViewers();
    })
    .build();
  
  static public void onClick(Player pl) {
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
          Placeholder.component("role", role.name)
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
    DTC.game.enforceDisplay(pl);
    DTC.boardsManager.refresh(pl);
  }
}
