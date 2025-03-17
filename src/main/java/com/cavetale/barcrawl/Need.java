package com.cavetale.barcrawl;

import com.cavetale.mytems.Mytems;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Need {
    PAINTBRUSH("Paintbrush", Mytems.GREEN_PAINTBRUSH, "This room needs a fresh coat of paint."),
    JADE("Jade", Mytems.HEART_SHAPED_JADE, "I need a nice round gem with the right color."),
    WHIP("Whip", Mytems.LEATHER_WHIP, "I want to cosplay as a archaeologist, but something is missing."),
    FLOPPY_DISK("Floppy Disk", Mytems.FLOPPY_DISK, "I need to backup all my data but don't have a storage device."),
    AXE("Axe", Mytems.TREE_CHOPPER, "Time to cut down some trees. Where did I put my trusty axe?"),
    SHIELD("Shield", Mytems.CLOVER_ROUND_SHIELD, "I need a nice decorative shield."),
    PAWN("Pawn", Mytems.WHITE_PAWN, "One of my chess pieces went missing."),
    DICE("Dice", Mytems.DICE, "Time to play Yahtzee! But my other dice went missing."),
    KEY("Key", Mytems.SILVER_KEY, "Bummer, I locked my safe and lost the key."),
    PIRATE_FLAG("Pirate Flag", Mytems.PIRATE_FLAG, "By pirate ship is missing a flag before I can set sail."),
    RUSTY_BUCKET("Rusty Bucket", Mytems.RUSTY_BUCKET, "I'm in need of something covered with rust. Don't ask why."),
    RING("Ring", Mytems.SILVER_RUBY_RING, "I lost my ring with the green gem somewhere."),
    TROPHY("Trophy", Mytems.GOLDEN_SPLEEF_TROPHY, "I forgot where I put my golden trophy."),
    HANDHELD("Handheld", Mytems.BIT_BOY, "Have you seen my portable gaming console?"),
    CANDY("Candy", Mytems.VOTE_CANDY, "I crave for something sweet."),
    CLOAK("Cloak", Mytems.BLUE_CLOAK, "I forgot where I hung my cloak."),
    PIANO("Piano", Mytems.ELECTRIC_PIANO, "Let's make some music. Too bad I left my instrument at home."),
    FLAG("Flag", Mytems.IRELAND, "Let's raise the Irish flag today!"),
    WATERING_CAN("Watering Can", Mytems.WATERING_CAN, "I need to water my flowers real quick."),
    BUTTERFLY("Butterfly", Mytems.GREEN_BUTTERFLY, "I saw an awesome butterfly earlier. I wish I had caught it!"),
    TRAFFIC_LIGHT("Traffic Light", Mytems.TRAFFIC_LIGHT, "I want to play Red Light Green Light."),
    ;

    private final String displayName;
    private final Mytems mytems;
    private final String request;
}
