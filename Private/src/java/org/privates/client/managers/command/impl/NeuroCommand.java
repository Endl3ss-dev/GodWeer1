package org.privates.client.managers.command.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.text.TextFormatting;
import org.privates.client.Excellent;
import org.privates.client.managers.command.CommandException;
import org.privates.client.managers.command.api.*;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.impl.combat.NeuroSelf;
import org.privates.client.managers.module.impl.combat.TriggerBot;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NeuroCommand implements Command, CommandWithAdvice, MultiNamedCommand {
    final Prefix prefix;
    final Logger logger;

    @Override
    public void execute(Parameters parameters) {
        String action = parameters.asString(0).orElse("status").toLowerCase();

        NeuroSelf neuro = Excellent.inst().moduleManager().get(NeuroSelf.class);
        AuraNew aura = Excellent.inst().moduleManager().get(AuraNew.class);
        TriggerBot triggerBot = Excellent.inst().moduleManager().get(TriggerBot.class);

        if (neuro == null || aura == null) {
            throw new CommandException(TextFormatting.RED + "Neuro/Aura modules not found.");
        }

        switch (action) {
            case "start" -> {
                if (!neuro.isEnabled()) neuro.setEnabled(true);
                if (aura.isEnabled()) aura.setEnabled(false);
                if (triggerBot != null && !triggerBot.isEnabled()) triggerBot.setEnabled(true);

                neuro.startFromCommand();
                logger.log(TextFormatting.GREEN + "Neuro recording started. " + TextFormatting.GRAY + "Aim manually at players and hit them.");
            }
            case "stop" -> {
                if (!neuro.isEnabled()) neuro.setEnabled(true);
                neuro.stopFromCommand();
                logger.log(TextFormatting.GREEN + "Neuro recording stopped.");
            }
            case "play" -> {
                if (!neuro.isEnabled()) neuro.setEnabled(true);
                NeuroSelf.NeuroRotationProfile profile = neuro.getGeneratedProfile();
                if (profile == null || !profile.isValid() || profile.getSamples().isEmpty()) {
                    throw new CommandException(TextFormatting.RED + "No Neuro profile. Use " + TextFormatting.GRAY + prefix.get() + "neuro start/stop");
                }

                if (triggerBot != null && triggerBot.isEnabled()) triggerBot.setEnabled(false);
                aura.componentMode().set("Neuro Self");
                if (!aura.isEnabled()) aura.setEnabled(true);

                logger.log(TextFormatting.GREEN + "Neuro playback enabled. " + TextFormatting.GRAY + "Aura mode: Neuro Self");
            }
            case "clear" -> {
                if (!neuro.isEnabled()) neuro.setEnabled(true);
                neuro.clearProfile();
                logger.log(TextFormatting.GREEN + "Neuro profile cleared.");
            }
            case "status" -> {
                NeuroSelf.NeuroRotationProfile profile = neuro.getGeneratedProfile();
                boolean valid = profile != null && profile.isValid() && !profile.getSamples().isEmpty();
                int profileSamples = valid ? profile.getSamples().size() : 0;
                logger.log(TextFormatting.GRAY + "Neuro status: "
                        + (neuro.isRecording() ? TextFormatting.RED + "RECORDING" : TextFormatting.GREEN + "IDLE")
                        + TextFormatting.GRAY + " | recordedSamples="
                        + TextFormatting.WHITE + neuro.recordedFramesCount()
                        + TextFormatting.GRAY + " | profileSamples="
                        + TextFormatting.WHITE + profileSamples
                        + TextFormatting.GRAY + " | auraMode="
                        + TextFormatting.WHITE + aura.componentMode().getValue());
            }
            default -> throw new CommandException(TextFormatting.RED + "Use: start, stop, play, clear, status");
        }
    }

    @Override
    public String name() {
        return "neuro";
    }

    @Override
    public String description() {
        return "Record and play your own aiming patterns for Aura";
    }

    @Override
    public List<String> aliases() {
        return List.of("ns");
    }

    @Override
    public List<String> adviceMessage() {
        String p = prefix.get();
        return List.of(
                p + "neuro start - start recording your manual aim (TriggerBot-like flow)",
                p + "neuro stop - stop recording and build profile",
                p + "neuro play - enable Aura with Neuro Self playback",
                p + "neuro status - show recording/profile state",
                p + "neuro clear - clear recorded profile"
        );
    }
}
