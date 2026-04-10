package dev.huey.destroyTheCore.managers;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplayBuilder;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

public class AdvancementsManager {
  UltimateAdvancementAPI api;
  
  AdvancementTab advancementTab;
  
  public RootAdvancement root;
  
  AdvancementDisplayBuilder<AdvancementDisplay.Builder, AdvancementDisplay> getDisplayBuilder(
    Material iconType, String id
  ) {
    List<String> descs = new ArrayList<>();
    
    int index = 1;
    while (true) {
      String key = "advancements.%s.desc-%d".formatted(id, index);
      if (!DestroyTheCore.translationsManager.has(key)) break;
      
      descs.add(TextUtils.$r(key));
      
      index++;
    }
    
    return new AdvancementDisplay.Builder(
      iconType,
      TextUtils.$r("advancements.%s.title".formatted(id))
    ).description(descs);
  }
  
  public void init() {
    api = UltimateAdvancementAPI.getInstance(DestroyTheCore.instance);
    
    api.disableVanillaAdvancements();
    
    advancementTab = api.createAdvancementTab("dtc");
    
    root = new RootAdvancement(
      advancementTab,
      "root",
      getDisplayBuilder(
        Material.END_STONE,
        "root"
      ).build(),
      "textures/block/spruce_log.png"
    );
    
    initEach();
    
    advancementTab.registerAdvancements(root);
  }
  
  public Advancement played;
  public List<Advancement> rolesPlayed;
  
  public void initEach() {
    played = new BaseAdvancement(
      "played",
      getDisplayBuilder(
        Material.GOLDEN_PICKAXE,
        "played"
      ).build(),
      root
    );
  }
  
  public void onPlayerJoin(PlayerJoinEvent ev) {
    Player pl = ev.getPlayer();
    
    advancementTab.showTab(pl);
  }
}
