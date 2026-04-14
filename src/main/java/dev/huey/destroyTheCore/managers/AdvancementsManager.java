package dev.huey.destroyTheCore.managers;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplayBuilder;
import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.*;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

public class AdvancementsManager {
  UltimateAdvancementAPI api;
  
  AdvancementTab advancementTab;
  
  public RootAdvancement rootAdv;
  public Set<BaseAdvancement> all = new HashSet<>();
  
  public BaseAdvancement chooseRoleAdv;
  public Map<RolesManager.RoleKey, BaseAdvancement> roleAdvMap = new HashMap<>();
  
  public BaseAdvancement playedAdv;
  public BaseAdvancement playedSpectatorAdv;
  
  public BaseAdvancement usedSkillAdv;
  public BaseAdvancement usedManySkillsAdv;
  
  public BaseAdvancement usedLotteryAdv;
  public BaseAdvancement usedManyLotteriesAdv;
  
  AdvancementDisplayBuilder<AdvancementDisplay.Builder, AdvancementDisplay> getDisplayBuilder(
    Material iconType, String id,
    List<TagResolver> placeholders
  ) {
    List<String> descs = new ArrayList<>();
    
    int index = 1;
    while (true) {
      String key = "advancements.%s.desc-%d".formatted(id, index);
      if (!DTC.translationsManager.has(key)) break;
      
      descs.add(TextUtils.$r(key, placeholders));
      
      index++;
    }
    
    return new AdvancementDisplay.Builder(
      iconType,
      TextUtils.$r("advancements.%s.title".formatted(id), placeholders)
    ).description(descs);
  }
  
  AdvancementDisplayBuilder<AdvancementDisplay.Builder, AdvancementDisplay> getDisplayBuilder(
    Material iconType, String id
  ) {
    return getDisplayBuilder(iconType, id, List.of());
  }
  
  public void init() {
    api = UltimateAdvancementAPI.getInstance(DTC.instance);
    
    api.disableVanillaAdvancements();
    
    advancementTab = api.createAdvancementTab("dtc");
    
    rootAdv = new RootAdvancement(
      advancementTab,
      "root",
      getDisplayBuilder(
        Material.END_STONE,
        "root"
      ).coords(0, 9f).build(),
      "textures/block/spruce_log.png"
    );
    
    chooseRoleAdv = new BaseAdvancement(
      "chose-role",
      getDisplayBuilder(
        Material.ENDER_CHEST,
        "chose-role"
      ).coords(1, 7.5f).build(),
      rootAdv
    );
    all.add(chooseRoleAdv);
    
    int y = 0;
    for (Role role : DTC.rolesManager.roles.values()) {
      roleAdvMap.put(
        role.id,
        new BaseAdvancement(
          "played-" + role.id.name().toLowerCase(),
          getDisplayBuilder(
            role.iconType,
            "played-role",
            List.of(
              Placeholder.unparsed(
                "role",
                role.name
              )
            )
          ).coords(2, y).build(),
          chooseRoleAdv
        )
      );
      y++;
    }
    all.addAll(roleAdvMap.values());
    
    playedAdv = new BaseAdvancement(
      "played",
      getDisplayBuilder(
        Material.WOODEN_SWORD,
        "played"
      ).coords(1, 14).build(),
      rootAdv
    );
    all.add(playedAdv);
    
    playedSpectatorAdv = new BaseAdvancement(
      "played-spectator",
      getDisplayBuilder(
        Material.ENDER_EYE,
        "played-spectator"
      ).coords(2, 14).build(),
      playedAdv
    );
    all.add(playedSpectatorAdv);
    
    usedSkillAdv = new BaseAdvancement(
      "used-skill",
      getDisplayBuilder(
        Material.WRITTEN_BOOK,
        "used-skill"
      ).coords(1, 15).build(),
      rootAdv
    );
    all.add(usedSkillAdv);
    
    usedManySkillsAdv = new BaseAdvancement(
      "used-many-skills",
      getDisplayBuilder(
        Material.KNOWLEDGE_BOOK,
        "used-many-skills"
      ).goalFrame().coords(2, 15).build(),
      usedSkillAdv,
      100
    );
    all.add(usedManySkillsAdv);
    
    usedLotteryAdv = new BaseAdvancement(
      "used-lottery",
      getDisplayBuilder(
        Material.HEART_OF_THE_SEA,
        "used-lottery"
      ).coords(1, 16).build(),
      rootAdv
    );
    all.add(usedLotteryAdv);
    
    usedManyLotteriesAdv = new BaseAdvancement(
      "used-many-lotteries",
      getDisplayBuilder(
        Material.CONDUIT,
        "used-many-lotteries"
      ).goalFrame().coords(2, 16).build(),
      usedLotteryAdv,
      100
    );
    all.add(usedManyLotteriesAdv);
    
    advancementTab.registerAdvancements(
      rootAdv,
      all
    );
  }
  
  public void onPlayerJoin(PlayerJoinEvent ev) {
    Player pl = ev.getPlayer();
    
    advancementTab.showTab(pl);
  }
}
