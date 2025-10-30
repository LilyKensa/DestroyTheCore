package dev.huey.destroyTheCore.items.gadgets;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public class RandomRoleGen extends UsableItemGen {
  public RandomRoleGen() {
    super(
      ItemsManager.ItemKey.RANDOM_ROLE,
      Material.ENCHANTED_BOOK
    );
  }
  
  @Override
  public void use(Player pl, Block block) {
    PlayerUtils.takeOneItemFromHand(pl);
    
    Role role = RandomUtils.pick(
      DestroyTheCore.rolesManager.roles.values().stream()
        .filter(r -> r.id != RolesManager.RoleKey.DEFAULT)
        .toList()
    );
    
    PlayerUtils.broadcast(
      TextUtils.$("items.random-role.announce", List.of(
        Placeholder.component("player", PlayerUtils.getName(pl)),
        Placeholder.component("item", getItem().effectiveName()),
        Placeholder.unparsed("role", role.name)
      ))
    );
    
    DestroyTheCore.rolesManager.setRole(pl, role);
    DestroyTheCore.game.enforceTeam(pl);
    DestroyTheCore.boardsManager.refresh(pl);
  }
}
