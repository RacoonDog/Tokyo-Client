package io.github.racoondog.tokyo.utils.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.accounts.Accounts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.command.CommandSource.suggestMatching;

/**
 * @author Crosby
 * @since 1.2.3
 */
@Environment(EnvType.CLIENT)
public class AccountArgumentType implements ArgumentType<String> {
    private static final DynamicCommandExceptionType NO_SUCH_ACCOUNT = new DynamicCommandExceptionType(name -> Text.literal("Account with name " + name + " doesn't exist."));
    private static final Collection<String> EXAMPLES = List.of("seasnail8169", "McChiggenLord");

    public static AccountArgumentType create() {
        return new AccountArgumentType();
    }

    public static Account<?> get(CommandContext<?> context) {
        return get(context, "account");
    }

    public static Account<?> get(CommandContext<?> context, String argumentName) {
        String name = context.getArgument(argumentName, String.class);
        for (var account : Accounts.get()) {
            if (name.equals(account.getUsername())) return account;
        }
        return null;
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String argument = reader.readString();

        for (var account : Accounts.get()) {
            if (argument.equals(account.getUsername())) return argument;
        }

        throw NO_SUCH_ACCOUNT.create(argument);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return suggestMatching(Accounts.get(), builder, Account::getUsername, account -> null);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
