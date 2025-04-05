package com.cavetale.barcrawl;

import com.cavetale.core.command.AbstractCommand;
import com.cavetale.core.command.CommandWarn;
import com.cavetale.fam.trophy.Highscore;
import com.cavetale.mytems.item.trophy.TrophyCategory;
import com.cavetale.worldmarker.entity.EntityMarker;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public final class BarCrawlCommand extends AbstractCommand<BarCrawlPlugin> {
    protected BarCrawlCommand(final BarCrawlPlugin plugin) {
        super(plugin, "barcrawl");
    }

    @Override
    protected void onEnable() {
        rootNode.addChild("info").denyTabCompletion()
            .description("Print info")
            .senderCaller(this::info);
        rootNode.addChild("enable").denyTabCompletion()
            .description("Enable plugin")
            .senderCaller(this::enable);
        rootNode.addChild("disable").denyTabCompletion()
            .description("Disable plugin")
            .senderCaller(this::disable);
        rootNode.addChild("scan").denyTabCompletion()
            .description("Scan NPCs")
            .playerCaller(this::scan);
        rootNode.addChild("reload").denyTabCompletion()
            .description("Reload configs")
            .playerCaller(this::reload);
        rootNode.addChild("progress").denyTabCompletion()
            .description("Make progress")
            .playerCaller(this::progress);
        rootNode.addChild("export").denyTabCompletion()
            .description("Export highscore")
            .senderCaller(this::export);
        rootNode.addChild("reward").denyTabCompletion()
            .description("Make rewards")
            .senderCaller(this::reward);
    }

    private void info(CommandSender sender) {
        sender.sendMessage(textOfChildren(text("Enabled ", GRAY), (plugin.getSaveTag().isEnabled()
                                                                   ? text("True", GREEN)
                                                                   : text("False", RED))));
        sender.sendMessage(textOfChildren(text("Completions ", GRAY), text(plugin.getSaveTag().getTotalScore(), WHITE)));
    }

    private void enable(CommandSender sender) {
        plugin.getSaveTag().setEnabled(true);
        plugin.getSaveTag().save();
        sender.sendMessage(text("Bar Crawl enabled!", GREEN));
    }

    private void disable(CommandSender sender) {
        plugin.getSaveTag().setEnabled(false);
        plugin.getSaveTag().save();
        sender.sendMessage(text("Bar Crawl disabled!", RED));
    }

    private void scan(Player player) {
        final List<String> list = new ArrayList<>();
        for (ArmorStand as : player.getWorld().getEntitiesByClass(ArmorStand.class)) {
            final String id = EntityMarker.getId(as);
            if (id == null) continue;
            list.add(id);
        }
        final List<String> list2 = new ArrayList<>();
        for (String it : list) {
            if (it.equals("start")) continue;
            if (!NonPlayerCharacter.IDS.contains(it)) {
                player.sendMessage(text("Not in id list: " + it, RED));
            }
            list2.add("\"" + it + "\"");
        }
        for (String it : NonPlayerCharacter.IDS) {
            if (!list.contains(it)) {
                player.sendMessage(text("NPC ID not found: " + it, RED));
            }
        }
        final String msg = String.join(", ", list2);
        player.sendMessage(text(msg, YELLOW));
        BarCrawlPlugin.plugin().getLogger().info(msg);
        player.sendMessage(text("Total " + list.size() + " NPCs", YELLOW));
    }

    private void progress(Player player) {
        Session session = Session.get(player.getUniqueId());
        if (session.getTag().getNeedIndex() >= session.getTag().getNeeds().size() - 1) {
            throw new CommandWarn("Max progress reached!");
        }
        session.getTag().setNeedIndex(session.getTag().getNeedIndex() + 1);
        session.save();
        player.sendMessage(text("Need Index is now " + session.getTag().getNeedIndex(), YELLOW));
    }

    private void reload(CommandSender sender) {
        BarCrawlPlugin.plugin().reload();
        Session.reload();
        sender.sendMessage(text("Reloaded", YELLOW));
    }

    private void export(CommandSender sender) {
        // Write file
        final Set<UUID> uuids = new HashSet<>();
        for (Map.Entry<UUID, Integer> entry : plugin.getSaveTag().getScores().entrySet()) {
            final UUID uuid = entry.getKey();
            final int score = entry.getValue();
            if (score >= 1) uuids.add(uuid);
        }
        final File file = new File(plugin.getDataFolder(), "uuids.txt");
        try (PrintWriter writer = new PrintWriter(file)) {
            for (UUID uuid : uuids) {
                writer.println(uuid.toString());
            }
        } catch (IOException ioe) {
            throw new UncheckedIOException("Writing to " + file, ioe);
        }
        sender.sendMessage(text(uuids.size() + " written to " + file, YELLOW));
    }

    private void reward(CommandSender sender) {
        // Give trophies
        final int trophies = Highscore.reward(plugin.getSaveTag().getScores(),
                                              "barcrawl",
                                              TrophyCategory.MEDAL,
                                              BarCrawlPlugin.TITLE,
                                              hi -> (hi.score == 1
                                                     ? "You finished one Bar Crawl!"
                                                     : "You finished " + hi.score + " Bar Crawls!"));
        sender.sendMessage(text(trophies + " Trophies delivered", YELLOW));
    }
}
