package com.hashhamster.thaumcraftfocusban;

import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import thaumcraft.api.casters.FocusModSplit;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.common.items.casters.ItemCaster;
import thaumcraft.common.items.casters.ItemFocus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

@Plugin(
        id = "thaumcraftfocusban",
        name = "Thaumcraft Focus Ban",
        version = "0.1",
        description = "Allows Specific Thaumcraft Spells To Be Disabled"
)
public class SpellCastListener {

    //Spell Effect Types
    public static final String THAUM_EFFECT_AIR = "thaumcraft.AIR";
    public static final String THAUM_EFFECT_BREAK = "thaumcraft.BREAK";
    public static final String THAUM_EFFECT_CURSE = "thaumcraft.CURSE";
    public static final String THAUM_EFFECT_EARTH = "thaumcraft.EARTH";
    public static final String THAUM_EFFECT_EXCHANGE = "thaumcraft.EXCHANGE";
    public static final String THAUM_EFFECT_FIRE = "thaumcraft.FIRE";
    public static final String THAUM_EFFECT_FLUX = "thaumcraft.FLUX";
    public static final String THAUM_EFFECT_FROST = "thaumcraft.FROST";
    public static final String THAUM_EFFECT_HEAL = "thaumcraft.HEAL";
    public static final String THAUM_EFFECT_RIFT = "thaumcraft.RIFT";

    //Spell Medium Types
    public static final String THAUM_MEDIUM_BOLT = "thaumcraft.BOLT";
    public static final String THAUM_MEDIUM_CLOUD = "thaumcraft.CLOUD";
    public static final String THAUM_MEDIUM_MINE = "thaumcraft.MINE";
    public static final String THAUM_MEDIUM_PLAN = "thaumcraft.PLAN";
    public static final String THAUM_MEDIUM_PROJECTILE = "thaumcraft.PROJECTILE";
    public static final String THAUM_MEDIUM_SPELLBAT = "thaumcraft.SPELLBAT";
    public static final String THAUM_MEDIUM_TOUCH = "thaumcraft.TOUCH";

    //Spell Mod Types
    public static final String THAUM_MOD_SCATTER = "thaumcraft.SCATTER";
    public static final String THAUM_MOD_SPLITTARGET = "thaumcraft.SPLITTARGET";
    public static final String THAUM_MOD_SPLITTRAJECTORY = "thaumcraft.SPLITTRAJECTORY";

    //Config Node
    private ConfigurationNode config = null;

    private static Logger logger;

    @Inject
    private PluginContainer pluginContainer;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private File defaultConfig;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    private static SpellCastListener instance;

    @Listener
    public void onConstruct(GameConstructionEvent event) {
        instance = this;
    }

    @Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
        logger = this.pluginContainer.getLogger();
        logger.info("Thaumcraft Focus Ban Plugin Initializing...");

        //Initialize Config
        try {
            if (!getDefaultConfig().exists()) {

                getDefaultConfig().createNewFile();
                this.config = getConfigManager().load();

                //Set Config Version
                this.config.getNode("ThaumCraftFocusBanConfigVersion").setValue(1);

                //Add Spells To Configure
                this.config.getNode("IS_SPELL_BANNED", THAUM_EFFECT_AIR).setValue(false);
                this.config.getNode("IS_SPELL_BANNED", THAUM_EFFECT_BREAK).setValue(false);
                this.config.getNode("IS_SPELL_BANNED", THAUM_EFFECT_CURSE).setValue(false);
                this.config.getNode("IS_SPELL_BANNED", THAUM_EFFECT_EARTH).setValue(false);
                this.config.getNode("IS_SPELL_BANNED", THAUM_EFFECT_EXCHANGE).setValue(false);
                this.config.getNode("IS_SPELL_BANNED", THAUM_EFFECT_FIRE).setValue(false);
                this.config.getNode("IS_SPELL_BANNED", THAUM_EFFECT_FLUX).setValue(false);
                this.config.getNode("IS_SPELL_BANNED", THAUM_EFFECT_FROST).setValue(false);
                this.config.getNode("IS_SPELL_BANNED", THAUM_EFFECT_HEAL).setValue(false);
                this.config.getNode("IS_SPELL_BANNED", THAUM_EFFECT_RIFT).setValue(false);

                this.config.getNode("IS_SPELL_BANNED", THAUM_MEDIUM_BOLT).setValue(false);
                this.config.getNode("IS_SPELL_BANNED", THAUM_MEDIUM_CLOUD).setValue(false);
                this.config.getNode("IS_SPELL_BANNED", THAUM_MEDIUM_MINE).setValue(false);
                this.config.getNode("IS_SPELL_BANNED", THAUM_MEDIUM_PLAN).setValue(false);
                this.config.getNode("IS_SPELL_BANNED", THAUM_MEDIUM_PROJECTILE).setValue(false);
                this.config.getNode("IS_SPELL_BANNED", THAUM_MEDIUM_SPELLBAT).setValue(false);
                this.config.getNode("IS_SPELL_BANNED", THAUM_MEDIUM_TOUCH).setValue(false);

                this.config.getNode("IS_SPELL_BANNED", THAUM_MOD_SCATTER).setValue(false);
                this.config.getNode("IS_SPELL_BANNED", THAUM_MOD_SPLITTARGET).setValue(false);
                this.config.getNode("IS_SPELL_BANNED", THAUM_MOD_SPLITTRAJECTORY).setValue(false);

                getConfigManager().save(this.config);
                getLogger().info("Created default config file for Thaumcraft Focus Ban");
            }

            this.config = getConfigManager().load();
        }
        catch (Exception e)
        {
            logger.error("Unable to create default config for Thaumcraft Focus Ban");
        }

        //Register Command Interface
        CommandSpec thaumcraftFocusBanCommandSpec = CommandSpec.builder()
                .description(Text.of("Thaumcraft Focus Spell Ban / Unban Toggle Command"))
                .permission("minecraft.command.op")
                .arguments(
                        GenericArguments.string(Text.of("focuskey")),
                        GenericArguments.bool(Text.of("isbanned")))
                .executor(new ThaumcraftFocusBanCommand())
                .build();

        Sponge.getCommandManager().register(pluginContainer, thaumcraftFocusBanCommandSpec, "focusban");
    }

    @Listener
    public void onInitialization(GameInitializationEvent event) {
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("Thaumcraft Focus Ban Plugin Started.");

    }

    @Listener()
    public void onItemRightClick(InteractItemEvent.Secondary event, @First Player player) {

        ItemStackSnapshot itemSnapshot = event.getItemStack();
        org.spongepowered.api.item.inventory.ItemStack itemStackUsed = itemSnapshot.createStack();

        if (itemStackUsed.getType().getId().equals("thaumcraft:caster_basic"))
        {
            boolean cancelEvent = false;

            net.minecraft.item.ItemStack isNative = org.spongepowered.common.item.inventory.util.ItemStackUtil.toNative(itemStackUsed);
            ItemCaster itemCaster = (ItemCaster)isNative.getItem();
            net.minecraft.item.ItemStack focusStack = itemCaster.getFocusStack(isNative);

            FocusPackage fp = ItemFocus.getPackage(focusStack);
            HashSet<String> uniqueFocusSpellKeys = getUniqueSpellNodeKeysForFocusPackage(fp);

            if (this.config.getNode("IS_SPELL_BANNED", THAUM_EFFECT_AIR).getBoolean()){
                for (String nodeKey : uniqueFocusSpellKeys) {
                    if (nodeKey == THAUM_EFFECT_AIR) {
                        sendPlayerBannedMessage(THAUM_EFFECT_AIR, player);
                        cancelEvent = true;
                    }
                }
            }
            if (this.config.getNode("IS_SPELL_BANNED", THAUM_EFFECT_BREAK).getBoolean()){
                for (String nodeKey : uniqueFocusSpellKeys) {
                    if (nodeKey == THAUM_EFFECT_BREAK) {
                        sendPlayerBannedMessage(THAUM_EFFECT_BREAK, player);
                        cancelEvent = true;
                    }
                }
            }
            if (this.config.getNode("IS_SPELL_BANNED", THAUM_EFFECT_CURSE).getBoolean()){
                for (String nodeKey : uniqueFocusSpellKeys) {
                    if (nodeKey == THAUM_EFFECT_CURSE) {
                        sendPlayerBannedMessage(THAUM_EFFECT_CURSE, player);
                        cancelEvent = true;
                    }
                }
            }
            if (this.config.getNode("IS_SPELL_BANNED", THAUM_EFFECT_EARTH).getBoolean()){
                for (String nodeKey : uniqueFocusSpellKeys) {
                    if (nodeKey == THAUM_EFFECT_EARTH) {
                        sendPlayerBannedMessage(THAUM_EFFECT_EARTH, player);
                        cancelEvent = true;
                    }
                }
            }
            if (this.config.getNode("IS_SPELL_BANNED", THAUM_EFFECT_EXCHANGE).getBoolean()){
                for (String nodeKey : uniqueFocusSpellKeys) {
                    if (nodeKey == THAUM_EFFECT_EXCHANGE) {
                        sendPlayerBannedMessage(THAUM_EFFECT_EXCHANGE, player);
                        cancelEvent = true;
                    }
                }
            }
            if (this.config.getNode("IS_SPELL_BANNED", THAUM_EFFECT_FIRE).getBoolean()){
                for (String nodeKey : uniqueFocusSpellKeys) {
                    if (nodeKey == THAUM_EFFECT_FIRE) {
                        sendPlayerBannedMessage(THAUM_EFFECT_FIRE, player);
                        cancelEvent = true;
                    }
                }
            }
            if (this.config.getNode("IS_SPELL_BANNED", THAUM_EFFECT_FLUX).getBoolean()){
                for (String nodeKey : uniqueFocusSpellKeys) {
                    if (nodeKey == THAUM_EFFECT_FLUX) {
                        sendPlayerBannedMessage(THAUM_EFFECT_FLUX, player);
                        cancelEvent = true;
                    }
                }
            }
            if (this.config.getNode("IS_SPELL_BANNED", THAUM_EFFECT_FROST).getBoolean()){
                for (String nodeKey : uniqueFocusSpellKeys) {
                    if (nodeKey == THAUM_EFFECT_FROST) {
                        sendPlayerBannedMessage(THAUM_EFFECT_FROST, player);
                        cancelEvent = true;
                    }
                }
            }
            if (this.config.getNode("IS_SPELL_BANNED", THAUM_EFFECT_HEAL).getBoolean()){
                for (String nodeKey : uniqueFocusSpellKeys) {
                    if (nodeKey == THAUM_EFFECT_HEAL) {
                        sendPlayerBannedMessage(THAUM_EFFECT_HEAL, player);
                        cancelEvent = true;
                    }
                }
            }
            if (this.config.getNode("IS_SPELL_BANNED", THAUM_EFFECT_RIFT).getBoolean()){
                for (String nodeKey : uniqueFocusSpellKeys) {
                    if (nodeKey == THAUM_EFFECT_RIFT) {
                        sendPlayerBannedMessage(THAUM_EFFECT_RIFT, player);
                        cancelEvent = true;
                    }
                }
            }
            if (this.config.getNode("IS_SPELL_BANNED", THAUM_MEDIUM_BOLT).getBoolean()){
                for (String nodeKey : uniqueFocusSpellKeys) {
                    if (nodeKey == THAUM_MEDIUM_BOLT) {
                        sendPlayerBannedMessage(THAUM_MEDIUM_BOLT, player);
                        cancelEvent = true;
                    }
                }
            }
            if (this.config.getNode("IS_SPELL_BANNED", THAUM_MEDIUM_CLOUD).getBoolean()){
                for (String nodeKey : uniqueFocusSpellKeys) {
                    if (nodeKey == THAUM_MEDIUM_CLOUD) {
                        sendPlayerBannedMessage(THAUM_MEDIUM_CLOUD, player);
                        cancelEvent = true;
                    }
                }
            }
            if (this.config.getNode("IS_SPELL_BANNED", THAUM_MEDIUM_MINE).getBoolean()){
                for (String nodeKey : uniqueFocusSpellKeys) {
                    if (nodeKey == THAUM_MEDIUM_MINE) {
                        sendPlayerBannedMessage(THAUM_MEDIUM_MINE, player);
                        cancelEvent = true;
                    }
                }
            }
            if (this.config.getNode("IS_SPELL_BANNED", THAUM_MEDIUM_PLAN).getBoolean()){
                for (String nodeKey : uniqueFocusSpellKeys) {
                    if (nodeKey == THAUM_MEDIUM_PLAN) {
                        sendPlayerBannedMessage(THAUM_MEDIUM_PLAN, player);
                        cancelEvent = true;
                    }
                }
            }
            if (this.config.getNode("IS_SPELL_BANNED", THAUM_MEDIUM_PROJECTILE).getBoolean()){
                for (String nodeKey : uniqueFocusSpellKeys) {
                    if (nodeKey == THAUM_MEDIUM_PROJECTILE) {
                        sendPlayerBannedMessage(THAUM_MEDIUM_PROJECTILE, player);
                        cancelEvent = true;
                    }
                }
            }
            if (this.config.getNode("IS_SPELL_BANNED", THAUM_MEDIUM_SPELLBAT).getBoolean()){
                for (String nodeKey : uniqueFocusSpellKeys) {
                    if (nodeKey == THAUM_MEDIUM_SPELLBAT) {
                        sendPlayerBannedMessage(THAUM_MEDIUM_SPELLBAT, player);
                        cancelEvent = true;
                    }
                }
            }
            if (this.config.getNode("IS_SPELL_BANNED", THAUM_MEDIUM_TOUCH).getBoolean()){
                for (String nodeKey : uniqueFocusSpellKeys) {
                    if (nodeKey == THAUM_MEDIUM_TOUCH) {
                        sendPlayerBannedMessage(THAUM_MEDIUM_TOUCH, player);
                        cancelEvent = true;
                    }
                }
            }
            if (this.config.getNode("IS_SPELL_BANNED", THAUM_MOD_SCATTER).getBoolean()){
                for (String nodeKey : uniqueFocusSpellKeys) {
                    if (nodeKey == THAUM_MOD_SCATTER) {
                        sendPlayerBannedMessage(THAUM_MOD_SCATTER, player);
                        cancelEvent = true;
                    }
                }
            }
            if (this.config.getNode("IS_SPELL_BANNED", THAUM_MOD_SPLITTARGET).getBoolean()){
                for (String nodeKey : uniqueFocusSpellKeys) {
                    if (nodeKey == THAUM_MOD_SPLITTARGET) {
                        sendPlayerBannedMessage(THAUM_MOD_SPLITTARGET, player);
                        cancelEvent = true;
                    }
                }
            }
            if (this.config.getNode("IS_SPELL_BANNED", THAUM_MOD_SPLITTRAJECTORY).getBoolean()){
                for (String nodeKey : uniqueFocusSpellKeys) {
                    if (nodeKey == THAUM_MOD_SPLITTRAJECTORY) {
                        sendPlayerBannedMessage(THAUM_MOD_SPLITTRAJECTORY, player);
                        cancelEvent = true;
                    }
                }
            }

            if (cancelEvent)
            {
                event.setCancelled(true);
            }
        }
    }

    private void sendPlayerBannedMessage(String thaumNodeKey, Player player) {
        player.sendMessage(Text.of(TextColors.RED, TextStyles.ITALIC,"Focus Node: "+ thaumNodeKey + " is banned."));
    }

    public HashSet<String> getUniqueSpellNodeKeysForFocusPackage(FocusPackage fp)
    {
        HashSet<String> uniqueKeys = new HashSet<String>();
        if (fp != null && fp.nodes != null) {
            for (IFocusElement el : fp.nodes) {
                uniqueKeys.add(el.getKey());
                if ((el instanceof FocusPackage)) {
                    uniqueKeys.addAll(getUniqueSpellNodeKeysForFocusPackage((FocusPackage)el));
                } else if ((el instanceof FocusModSplit)) {
                    FocusModSplit fms = (FocusModSplit)el;
                    ArrayList<FocusPackage> splitPackages = fms.getSplitPackages();
                    for (FocusPackage sfp : splitPackages) {
                        uniqueKeys.addAll(getUniqueSpellNodeKeysForFocusPackage(sfp));
                    }
                }
            }
        }

        return uniqueKeys;
    }


    public static Logger getLogger() {
        return logger;
    }

    public File getDefaultConfig() {
        return this.defaultConfig;
    }

    public ConfigurationLoader<CommentedConfigurationNode> getConfigManager() {
        return configManager;
    }

    public static SpellCastListener getInstance() {
        return instance;
    }

    public ConfigurationNode getConfig() {
        return config;
    }
}
