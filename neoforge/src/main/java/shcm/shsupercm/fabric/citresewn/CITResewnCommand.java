package shcm.shsupercm.fabric.citresewn;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import shcm.shsupercm.fabric.citresewn.cit.ActiveCITs;
import shcm.shsupercm.fabric.citresewn.cit.CIT;
import shcm.shsupercm.fabric.citresewn.cit.CITCondition;
import shcm.shsupercm.fabric.citresewn.cit.CITRegistry;
import shcm.shsupercm.fabric.citresewn.cit.CITType;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyKey;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;
import shcm.shsupercm.fabric.citresewn.platform.Platform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/** NeoForge-native implementation of the {@code /citresewn} client command. */
public final class CITResewnCommand {
    private CITResewnCommand() {
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void register(RegisterClientCommandsEvent event) {
        event.getDispatcher().register((LiteralArgumentBuilder) LiteralArgumentBuilder.literal("citresewn")
                .executes(context -> {
                    feedback(Text.literal("CIT Resewn v" + Platform.getModVersion("citresewn") + ":"));
                    feedback(Text.literal("  Registered: " + CITRegistry.TYPES.values().stream().distinct().count()
                            + " types and " + CITRegistry.CONDITIONS.values().stream().distinct().count() + " conditions"));

                    boolean active = CITResewnConfig.INSTANCE.enabled && ActiveCITs.isActive();
                    feedback(Text.literal("  Active: " + (active ? "yes" : "no, "
                            + (CITResewnConfig.INSTANCE.enabled ? "no CIT packs loaded" : "disabled in config"))));
                    if (active)
                        feedback(Text.literal("  Loaded: "
                                + ActiveCITs.getActive().cits.values().stream().mapToLong(Collection::size).sum()
                                + " CITs from " + ActiveCITs.getActive().cits.values().stream()
                                .flatMap(Collection::stream).map(cit -> cit.packName).distinct().count()
                                + " resource packs"));

                    return 1;
                })
                .then(LiteralArgumentBuilder.literal("config").executes(context -> {
                    feedback(Text.literal("Configuration: " + CITResewnConfig.file().toAbsolutePath()
                            + " (edit the JSON file and restart Minecraft)"));
                    return 1;
                }))
                .then(LiteralArgumentBuilder.literal("analyze")
                        .then(LiteralArgumentBuilder.literal("pack")
                                .then(RequiredArgumentBuilder.argument("pack", new LoadedCITPackArgument())
                                        .executes(context -> analyzePack(context.getArgument("pack", String.class)))))));
    }

    private static int analyzePack(String pack) {
        if (!ActiveCITs.isActive()) {
            feedback(Text.literal("Not active"));
            return 1;
        }

        feedback(Text.literal("Analyzed CIT data of \"" + pack + "\u00a7r\":"));
        List<Text> lines = new ArrayList<>();

        for (Map.Entry<PropertyKey, Set<PropertyValue>> entry : ActiveCITs.getActive().globalProperties.properties.entrySet())
            for (PropertyValue value : entry.getValue())
                if (value.packName().equals(pack))
                    lines.add(Text.literal("  " + entry.getKey()
                            + (value.keyMetadata() == null ? "" : "." + value.keyMetadata())
                            + " = " + value.value()));

        if (!lines.isEmpty()) {
            feedback(Text.literal(" Global Properties:"));
            lines.forEach(CITResewnCommand::feedback);
            lines.clear();
        }

        for (Map.Entry<Class<? extends CITType>, List<CIT<?>>> entry : ActiveCITs.getActive().cits.entrySet()) {
            long count = entry.getValue().stream().filter(cit -> cit.packName.equals(pack)).count();
            if (count > 0)
                lines.add(Text.literal("  " + CITRegistry.idOfType(entry.getKey()) + " = " + count));
        }

        if (!lines.isEmpty()) {
            feedback(Text.literal(" Types:"));
            lines.forEach(CITResewnCommand::feedback);
        }

        List<CITCondition> conditions = ActiveCITs.getActive().cits.values().stream()
                .flatMap(Collection::stream)
                .filter(cit -> cit.packName.equals(pack))
                .flatMap(cit -> Arrays.stream(cit.conditions))
                .toList();
        if (!conditions.isEmpty())
            feedback(Text.literal(" Utilizing " + conditions.size() + " conditions ("
                    + conditions.stream().map(Object::getClass).distinct().count() + " unique condition types)"));

        return 1;
    }

    private static void feedback(Text message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.inGameHud != null)
            client.inGameHud.getChatHud().addMessage(message);
    }

    private static final class LoadedCITPackArgument implements ArgumentType<String> {
        @Override
        public String parse(StringReader reader) throws CommandSyntaxException {
            StringBuilder value = new StringBuilder();
            while (reader.canRead())
                value.append(reader.read());

            String pack = value.toString().trim();
            if (!getPacks().contains(pack)) {
                LiteralMessage message = new LiteralMessage("Could not find CIT pack");
                throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
            }
            return pack;
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            getPacks().forEach(builder::suggest);
            return builder.buildFuture();
        }

        private static Set<String> getPacks() {
            if (!ActiveCITs.isActive())
                return Collections.emptySet();
            return ActiveCITs.getActive().cits.values().stream()
                    .flatMap(Collection::stream)
                    .map(cit -> cit.packName)
                    .collect(Collectors.toSet());
        }
    }
}
