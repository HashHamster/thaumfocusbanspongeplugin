package com.hashhamster.thaumcraftfocusban;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class ThaumcraftFocusBanCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        try {
            Optional<String> focusKeyToUpdate = args.getOne("focuskey");
            Optional<Boolean> valueToChangeTo = args.getOne("isbanned");

            if (focusKeyToUpdate.isPresent() && valueToChangeTo.isPresent()){
                ConfigurationNode config = SpellCastListener.getInstance().getConfig();
                String fullFocusName = "thaumcraft." + focusKeyToUpdate.get().toUpperCase();
                if (!config.getNode("IS_SPELL_BANNED", fullFocusName).isVirtual())
                {
                    config.getNode("IS_SPELL_BANNED", fullFocusName).setValue(valueToChangeTo.get().booleanValue());
                    src.sendMessage(Text.of(fullFocusName + " set to " + valueToChangeTo.get().booleanValue()));
                }
                else
                {
                    src.sendMessage(Text.of("Error: Focus Node - " + fullFocusName + " not found in config."));
                    src.sendMessage(Text.of("Valid Foci Nodes Are: AIR, BOLT, BREAK, CLOUD, CURSE, EARTH, EXCHANGE, FIRE, FLUX, FROST, HEAL, MINE, PLAN, PROJECTILE, RIFT, SCATTER, SPELLBAT, SPLITTARGET, SPLITTRAJECTORY, TOUCH"));
                }
                SpellCastListener.getInstance().getConfigManager().save(config);
            }
        }
        catch (Exception e) {
            SpellCastListener.getLogger().error("thaumcraftfocusban command could not update config settings");
        }

        return CommandResult.success();
    }
}
