package io.github.racoondog.tokyo.systems.accounts;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.screens.accounts.AccountsScreen;
import meteordevelopment.meteorclient.gui.screens.accounts.AddAccountScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.systems.accounts.Accounts;

public class AddAccessTokenAccountScreen extends AddAccountScreen {
    public AddAccessTokenAccountScreen(GuiTheme theme, AccountsScreen parent) {
        super(theme, "Add Account", parent);
    }

    @Override
    public void initWidgets() {
        WTable t = add(theme.table()).widget();

        // Token
        t.add(theme.label("Access Token: "));
        WTextBox token = t.add(theme.textBox("")).minWidth(400).expandX().widget();
        token.setFocused(true);
        t.row();

        // Add
        add = t.add(theme.button("Add")).expandX().widget();
        add.action = () -> {
            if (!token.get().isEmpty()) {
                AccessTokenAccount account = new AccessTokenAccount(token.get());
                if (!Accounts.get().exists(account)) {
                    AccountsScreen.addAccount(this, parent, account);
                }
            }
        };

        enterAction = add.action;
    }
}
