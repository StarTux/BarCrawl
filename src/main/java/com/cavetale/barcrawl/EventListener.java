package com.cavetale.barcrawl;

import com.cavetale.core.event.hud.PlayerHudEvent;
import com.cavetale.core.event.hud.PlayerHudPriority;
import com.cavetale.core.font.VanillaItems;
import com.cavetale.mytems.Mytems;
import com.cavetale.worldmarker.entity.EntityMarker;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
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
import static net.kyori.adventure.text.Component.newline;
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
            event.bossbar(PlayerHudPriority.HIGH,
                          textOfChildren(text(tiny("your item "), GRAY), nextNeed.getMytems(), text(nextNeed.getDisplayName(), GREEN)),
                          BossBar.Color.RED,
                          BossBar.Overlay.PROGRESS,
                          1f);
        }
        final int score = BarCrawlPlugin.plugin().getSaveTag().getScore(player.getUniqueId());
        if (score > 0) {
            final List<Component> sidebar = new ArrayList<>();
            sidebar.add(BarCrawlPlugin.TITLE);
            sidebar.add(textOfChildren(text(tiny("completions "), GRAY), text(score, WHITE)));
            sidebar.addAll(BarCrawlPlugin.plugin().getHighscore());
            event.sidebar(PlayerHudPriority.HIGH, sidebar);
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
        final int index = session.getTag().getNpcs().indexOf(id);
        if (index < 0 || !session.getTag().isStarted()) {
            if (!session.getTag().isStarted()) {
                session.getTag().roll();
                session.getTag().setStarted(true);
                session.save();
            }
            BarCrawlPlugin.plugin().getLogger().info(player.getName() + " rolled");
            final ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            book.editMeta(BookMeta.class, meta -> {
                    meta.author(text("Cavetale"));
                    meta.title(text("BarCrawl"));
                    meta.pages(List.of(textOfChildren(text(id, GRAY),
                                                      newline(),
                                                      text("Happy Halloween! ", GOLD), VanillaItems.CARVED_PUMPKIN,
                                                      newline(),
                                                      newline(),
                                                      text("Go around and find out who needs your current item."),
                                                      space(),
                                                      text("They will be very happy and give you a new item in return."),
                                                      newline(), newline(),
                                                      text("Once everybody is happy, a cool reward awaits.")),
                                       textOfChildren(text("Total completions: ", GRAY),
                                                      text(BarCrawlPlugin.plugin().getSaveTag().getScore(player.getUniqueId())),
                                                      newline(),
                                                      newline(),
                                                      textOfChildren(text("Trades done: ", GRAY),
                                                                     text(session.getTag().getNeedIndex())))));
                });
            player.closeInventory();
            player.openBook(book);
            player.playSound(player.getLocation(), Sound.ENTITY_SNIFFER_HAPPY, SoundCategory.MASTER, 0.5f, 2f);
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
                final int oldScore = BarCrawlPlugin.plugin().getSaveTag().getScore(player.getUniqueId());
                BarCrawlPlugin.plugin().getSaveTag().addScore(player.getUniqueId(), 1);
                BarCrawlPlugin.plugin().getSaveTag().save();
                BarCrawlPlugin.plugin().computeHighscore();
                BarCrawlPlugin.plugin().getLogger().info(player.getName() + " completed #" + BarCrawlPlugin.plugin().getSaveTag().getScore(player.getUniqueId()));
                player.showTitle(title(textOfChildren(Mytems.FIZZY_BREW, text("Complete!", GREEN, BOLD)),
                                       text("You completed the Bar Crawl!", GREEN)));
                if (oldScore % 2 == 0) {
                    Mytems.HALLOWEEN_TOKEN_2.giveItemStack(player, 1);
                } else {
                    Mytems.KITTY_COIN.giveItemStack(player, 1);
                }
                Mytems.FIZZY_BREW.giveItemStack(player, 1);
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 0.5f, 1f);
            } else {
                session.save();
                BarCrawlPlugin.plugin().getLogger().info(player.getName() + " progressed #" + BarCrawlPlugin.plugin().getSaveTag().getScore(player.getUniqueId()) + " need now " + session.getTag().getNeedIndex());
                final Need nextNeed = session.getTag().getNeeds().get(session.getTag().getNeedIndex());
                player.showTitle(title(textOfChildren(currentNeed.getMytems(), space(), Mytems.ARROW_RIGHT, space(), nextNeed.getMytems()),
                                       text("You traded items!", GREEN)));
                if (session.getTag().getNeedIndex() % 2 == 0) {
                    Mytems.RUBY.giveItemStack(player, 1);
                } else {
                    Mytems.HALLOWEEN_TOKEN.giveItemStack(player, 1);
                }
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
