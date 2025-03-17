package com.cavetale.barcrawl;

import com.cavetale.core.event.hud.PlayerHudEvent;
import com.cavetale.core.event.hud.PlayerHudPriority;
import com.cavetale.mytems.Mytems;
import com.cavetale.worldmarker.entity.EntityMarker;
import java.util.List;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import static com.cavetale.core.font.Unicode.tiny;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.*;
import static net.kyori.adventure.title.Title.title;

public final class EventListener implements Listener {
    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, BarCrawlPlugin.plugin());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onPlayerJoin(PlayerJoinEvent event) {
        Session.load(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onPlayerQuit(PlayerQuitEvent event) {
        Session.unload(event.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onPlayerHud(PlayerHudEvent event) {
        if (!BarCrawlPlugin.plugin().getSaveTag().isEnabled()) return;
        final Player player = event.getPlayer();
        final Session session = Session.get(player.getUniqueId());
        if (session.getTag().isStarted()) {
            final Need nextNeed = session.getTag().getNeeds().get(session.getTag().getNeedIndex());
            event.bossbar(PlayerHudPriority.DEFAULT,
                          textOfChildren(text(tiny("your item "), GRAY), nextNeed.getMytems(), text(nextNeed.getDisplayName(), GREEN)),
                          BossBar.Color.GREEN,
                          BossBar.Overlay.PROGRESS,
                          1f);
        }
        if (BarCrawlPlugin.plugin().getSaveTag().getScore(player.getUniqueId()) > 0) {
            event.sidebar(PlayerHudPriority.DEFAULT, BarCrawlPlugin.plugin().getHighscore());
        }
    }

    @EventHandler
    private void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!BarCrawlPlugin.plugin().getSaveTag().isEnabled()) return;
        final String id = EntityMarker.getId(event.getRightClicked());
        if (id == null) return;
        final Player player = event.getPlayer();
        final Session session = Session.get(player.getUniqueId());
        event.setCancelled(true);
        if ("start".equals(id) && !session.getTag().isStarted()) {
            session.getTag().roll();
            session.getTag().setStarted(true);
            session.save();
            BarCrawlPlugin.plugin().getLogger().info(player.getName() + " rolled");
        }
        final int index = session.getTag().getNpcs().indexOf(id);
        if (index < 0 || !session.getTag().isStarted()) {
            final ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            book.editMeta(BookMeta.class, meta -> {
                    meta.author(text("Cavetale"));
                    meta.title(text("BarCrawl"));
                    meta.pages(List.of(textOfChildren(text(id + ": ", GRAY),
                                                      text("Happy Saint Patrick's Day! ", BLACK), Mytems.SMILE)));
                    player.playSound(player.getLocation(), Sound.ENTITY_SNIFFER_HAPPY, SoundCategory.MASTER, 0.5f, 2f);
                });
            player.closeInventory();
            player.openBook(book);
            return;
        }
        final Need thisNeed = session.getTag().getNeeds().get(index);
        final Need currentNeed = session.getTag().getNeeds().get(session.getTag().getNeedIndex());
        if (thisNeed == currentNeed) {
            session.getTag().setNeedIndex(session.getTag().getNeedIndex() + 1);
            if (session.getTag().getNeedIndex() >= session.getTag().getNeeds().size()) {
                // complete
                session.getTag().setStarted(false);
                session.save();
                BarCrawlPlugin.plugin().getSaveTag().addScore(player.getUniqueId(), 1);
                BarCrawlPlugin.plugin().getSaveTag().save();
                BarCrawlPlugin.plugin().computeHighscore();
                BarCrawlPlugin.plugin().getLogger().info(player.getName() + " completed #" + BarCrawlPlugin.plugin().getSaveTag().getScore(player.getUniqueId()));
                player.showTitle(title(textOfChildren(Mytems.FIZZY_BREW, text("Complete!", GREEN, BOLD)),
                                       text("You completed the Bar Crawl!", GREEN)));
                Mytems.KITTY_COIN.giveItemStack(player, 1);
                Mytems.FIZZY_BREW.giveItemStack(player, 1);
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 0.5f, 1f);
            } else {
                session.save();
                BarCrawlPlugin.plugin().getLogger().info(player.getName() + " progressed #" + BarCrawlPlugin.plugin().getSaveTag().getScore(player.getUniqueId()) + " need now " + session.getTag().getNeedIndex());
                final Need nextNeed = session.getTag().getNeeds().get(session.getTag().getNeedIndex());
                player.showTitle(title(textOfChildren(currentNeed.getMytems(), space(), Mytems.ARROW_RIGHT, space(), nextNeed.getMytems()),
                                       text("You traded items!", GREEN)));
                Mytems.RUBY.giveItemStack(player, 1);
                Mytems.FIZZY_BREW.giveItemStack(player, 1);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 0.5f, 0.75f);
            }
        } else {
            final ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            book.editMeta(BookMeta.class, meta -> {
                    meta.author(text("Cavetale"));
                    meta.title(text("BarCrawl"));
                    if (session.getTag().getNeedIndex() > index) {
                        meta.pages(List.of(textOfChildren(text(id + ": ", GRAY),
                                                          text("Thank you so much! ", BLACK), Mytems.SMILE)));
                        player.playSound(player.getLocation(), Sound.ENTITY_SNIFFER_HAPPY, SoundCategory.MASTER, 0.5f, 2f);
                    } else {
                        meta.pages(List.of(textOfChildren(text(id + ": ", GRAY),
                                                          text(thisNeed.getRequest(), BLACK))));
                        player.playSound(player.getLocation(), Sound.ENTITY_STRIDER_HAPPY, SoundCategory.MASTER, 0.5f, 2f);
                    }
                });
            player.closeInventory();
            player.openBook(book);
        }
    }
}
