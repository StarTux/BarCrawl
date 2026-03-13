package com.cavetale.barcrawl;

import com.cavetale.core.font.VanillaItems;
import com.cavetale.mytems.Mytems;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@Getter
@RequiredArgsConstructor
public enum BarCrawlEdition {
    ST_PATRICKS(new StPatricksNeeds()) {
        @Override public Component getGreeting() {
            return textOfChildren(
                text("Happy Saint Patrick's Day!"),
                Mytems.SMILE
            );
        }

        @Override public void giveTradeReward(Player player, int needIndex) {
            Mytems.RUBY.giveItemStack(player, 1);
        }

        @Override public void giveCompletionReward(Player player, int oldScore) {
            Mytems.KITTY_COIN.giveItemStack(player, 1);
        }
    },
    CAVETOBER(new CavetoberNeeds()) {
        @Override public Component getGreeting() {
            return textOfChildren(
                text("Happy Halloween! ", GOLD),
                VanillaItems.CARVED_PUMPKIN
            );
        }

        @Override public void giveTradeReward(Player player, int needIndex) {
            if (needIndex % 2 == 0) {
                Mytems.RUBY.giveItemStack(player, 1);
            } else {
                Mytems.HALLOWEEN_TOKEN.giveItemStack(player, 1);
            }
        }

        @Override public void giveCompletionReward(Player player, int oldScore) {
            if (oldScore % 2 == 0) {
                Mytems.HALLOWEEN_TOKEN_2.giveItemStack(player, 1);
            } else {
                Mytems.KITTY_COIN.giveItemStack(player, 1);
            }
        }
    },
    ;

    private final Needs needs;

    abstract Component getGreeting();

    abstract void giveTradeReward(Player player, int needIndex);

    abstract void giveCompletionReward(Player player, int oldScore);
}
