package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.EditorTool;
import dev.huey.destroyTheCore.tools.*;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ToolsManager {
  
  public Map<String, List<EditorTool>> kits;
  
  public void init() {
    kits = Map.ofEntries(
      Map.entry(
        "lobby",
        List.of(
          new LobbyTool(),
          new StartButtonTool(),
          new JoinRedTool(),
          new JoinGreenTool(),
          new JoinSpectatorTool(),
          new EmptyTool(),
          new EmptyTool(),
          new EmptyTool(),
          new CancelTool()
        )
      ),
      Map.entry(
        "map",
        List.of(
          new RestAreaTool(),
          new SpawnpointsTool(),
          new CoreBlockTool(),
          new WoodsTool(),
          new OresTool(),
          new DiamondsTool(),
          new ShopsTool(),
          new MissionTool(),
          new CancelTool()
        )
      )
    );
    
    refresh();
  }
  
  public void loadKit(Player pl, String name) {
    List<EditorTool> tools = kits.get(name);
    if (tools == null) return;
    
    DestroyTheCore.inventoriesManager.saveHotbar(pl);
    
    for (int i = 0; i < tools.size(); ++i) {
      pl.getInventory().setItem(i, tools.get(i).getItem());
    }
  }
  
  /** On config file reload */
  public void refresh() {
    if (DestroyTheCore.toolsManager.kits == null) return;
    
    for (String key : kits.keySet()) {
      for (EditorTool tool : kits.get(key)) {
        tool.refresh();
      }
    }
  }
  
  public void onParticleTick() {
    for (String key : kits.keySet()) {
      for (EditorTool tool : kits.get(key)) {
        for (Player p : Bukkit.getOnlinePlayers()) if (
          tool.checkItem(p.getInventory().getItemInMainHand())
        ) tool.onParticleTick(p);
      }
    }
  }
  
  public void onPlayerInteract(PlayerInteractEvent ev) {
    Player pl = ev.getPlayer();
    ItemStack item = ev.getItem();
    
    if (
      ev.getAction() != Action.RIGHT_CLICK_AIR
        && ev.getAction() != Action.RIGHT_CLICK_BLOCK
    ) return;
    if (item == null || item.getType().isAir()) return;
    
    for (String key : kits.keySet()) {
      for (EditorTool tool : kits.get(key)) {
        if (!tool.checkItem(item)) continue;
        
        switch (ev.getAction()) {
          case RIGHT_CLICK_AIR -> tool.onRightClickAir(pl);
          case RIGHT_CLICK_BLOCK -> tool.onRightClickBlock(
            pl,
            ev.getClickedBlock()
          );
        }
        pl.swingMainHand();
        ev.setCancelled(true);
        return;
      }
    }
  }
  
  public void onBlockBreak(BlockBreakEvent ev) {
    Player pl = ev.getPlayer();
    ItemStack item = pl.getInventory().getItemInMainHand();
    
    if (item.getType().isAir()) return;
    
    for (String key : kits.keySet()) {
      for (EditorTool tool : kits.get(key)) {
        if (!tool.checkItem(item)) continue;
        
        tool.onBreakBlock(pl, ev.getBlock());
        ev.setCancelled(true);
        return;
      }
    }
  }
}
