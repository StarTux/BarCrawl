package com.cavetale.barcrawl;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.RegistryBuilderFactory;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.DialogRegistryEntry;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public final class DialogHelper {

    public static void showNoticeDialog(Player player, Component title, List<Component> components) {
        player.showDialog(
            Dialog.create(
                (RegistryBuilderFactory<Dialog, ? extends DialogRegistryEntry.Builder> factory) -> {
                    final DialogRegistryEntry.Builder builder = factory.empty();
                    builder.type(
                        DialogType.notice(
                            ActionButton.builder(text("Ok")).build()
                        )
                    );
                    builder.base(
                        DialogBase.builder(title)
                        .body(
                            components.stream()
                            .map(DialogBody::plainMessage)
                            .toList()
                        )
                        .build()
                    );
                }
            )
        );
    }

    private DialogHelper() { }
}
