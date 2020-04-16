package de.nwex.translate.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class ClientPlayerOrStringArgumentType implements ArgumentType<String>
{
    private static final Collection<String> EXAMPLES = Arrays.asList("networkException", "quiquelhappy");

    private ClientPlayerOrStringArgumentType()
    {
    }

    public static ClientPlayerOrStringArgumentType argument()
    {
        return new ClientPlayerOrStringArgumentType();
    }

    public static String getArgument(CommandContext<ServerCommandSource> context, String arg)
    {
        return context.getArgument(arg, String.class);
    }

    @Override
    public String parse(StringReader reader)
    {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        if(context.getSource() instanceof CommandSource)
        {
            StringReader reader = new StringReader(builder.getInput());
            reader.setCursor(builder.getStart());
            CommandSource source = (CommandSource) context.getSource();
            Parser parser = new Parser(reader);

            try
            {
                parser.parse();
            }
            catch(CommandSyntaxException ignore) {}

            return parser.listSuggestions(builder, b -> CommandSource.suggestMatching(source.getPlayerNames(), b));
        }
        else
        {
            return Suggestions.empty();
        }
    }

    @Override
    public Collection<String> getExamples()
    {
        return ClientPlayerOrStringArgumentType.EXAMPLES;
    }

    static class Parser
    {
        private static final BiConsumer<Vec3d, List<Entity>> UNSORTED = (origin, list) ->
        {
        };

        private final StringReader reader;
        private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestor;
        private BiPredicate<Vec3d, Entity> filter = (origin, entity) -> true;
        private BiConsumer<Vec3d, List<Entity>> sorter = UNSORTED;
        private int limit = Integer.MAX_VALUE;
        private Double originX = null;
        private Double originY = null;
        private Double originZ = null;

        Parser(StringReader reader)
        {
            this.reader = reader;
        }

        ClientEntitySelector parse() throws CommandSyntaxException
        {
            suggestor = this::suggestStart;

            parsePlayerName();
            addFilter((origin, entity) -> entity instanceof PlayerEntity);

            return new ClientEntitySelector(filter, sorter, limit, false, originX, originY, originZ);
        }

        void parsePlayerName() throws CommandSyntaxException
        {
            if(reader.canRead())
            {
                int start = reader.getCursor();
                suggestor = (builder, playerNameSuggestor) ->
                {
                    SuggestionsBuilder newBuilder = builder.createOffset(start);
                    playerNameSuggestor.accept(newBuilder);
                    builder.add(newBuilder);
                    return builder.buildFuture();
                };
            }

            int start = reader.getCursor();
            String playerName = reader.readString();
            if(playerName.isEmpty() || playerName.length() > 16)
            {
                reader.setCursor(start);
                throw EntitySelectorReader.INVALID_ENTITY_EXCEPTION.createWithContext(reader);
            }

            filter = (origin, entity) -> ((PlayerEntity) entity).getGameProfile().getName().equals(playerName);
            limit = 1;
        }

        void addFilter(BiPredicate<Vec3d, Entity> filter)
        {
            final BiPredicate<Vec3d, Entity> prevFilter = this.filter;
            this.filter = (origin, entity) -> filter.test(origin, entity) && prevFilter.test(origin, entity);
        }

        CompletableFuture<Suggestions> listSuggestions(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> playerNameSuggestor)
        {
            return suggestor.apply(builder.createOffset(reader.getCursor()), playerNameSuggestor);
        }

        private CompletableFuture<Suggestions> suggestStart(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> playerNameSuggestor)
        {
            playerNameSuggestor.accept(builder);
            suggestAtSelectors(builder, playerNameSuggestor);
            return builder.buildFuture();
        }

        private CompletableFuture<Suggestions> suggestAtSelectors(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> playerNameSuggestor)
        {
            return builder.buildFuture();
        }
    }
}
