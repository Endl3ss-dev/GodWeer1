package antileak.base.client.ui.account;

import antileak.base.mixin.IMinecraftClientAccessor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.session.Session;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class AccountScreen extends Screen {
    private static final String[] PREFIXES = {"Dark", "Shadow", "Crystal", "Night", "Fire", "Ice", "Storm", "Thunder", "Ghost", "Blaze", "Frost", "Neon", "Cyber", "Pixel", "Omega", "Ultra", "Super", "Hyper", "Mega", "Ultra"};
    private static final String[] SUFFIXES = {"Wolf", "Fox", "Dragon", "Phoenix", "Knight", "Warrior", "Hunter", "Wizard", "Ninja", "Demon", "Angel", "Tiger", "Hawk", "Viper", "Cobra", "Bear", "Lord", "King", "Queen", "Star"};

    private final Screen parent;
    private int scrollOffset;
    private Account selectedAccount;

    private static final int PANEL_W = 220;
    private static final int BTN_W = 60;

    public AccountScreen(Screen parent) {
        super(Text.of("Accounts"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = width / 2;

        addDrawableChild(ButtonWidget.builder(Text.of("Generate"), b -> {
            String name = generateName();
            AccountManager.getInstance().generateOffline(name);
        }).dimensions(cx - PANEL_W / 2, 28, BTN_W, 16).build());

        addDrawableChild(ButtonWidget.builder(Text.of("Login"), b -> {
            if (selectedAccount != null) login(selectedAccount);
        }).dimensions(cx - BTN_W / 2, 28, BTN_W, 16).build());

        addDrawableChild(ButtonWidget.builder(Text.of("Delete"), b -> {
            if (selectedAccount != null) {
                AccountManager.getInstance().remove(selectedAccount);
                selectedAccount = null;
            }
        }).dimensions(cx + PANEL_W / 2 - BTN_W, 28, BTN_W, 16).build());

        addDrawableChild(ButtonWidget.builder(Text.of("Back"), b -> {
            client.setScreen(parent);
        }).dimensions(cx - 30, height - 24, 60, 16).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int cx = width / 2;
        int panelX = cx - PANEL_W / 2;
        int panelY = 50;
        int panelH = height - panelY - 34;

        context.drawTextWithShadow(textRenderer, Text.of("Accounts"), cx - textRenderer.getWidth("Accounts") / 2, 10, 0xFFFFFF);

        context.fill(panelX, panelY, panelX + PANEL_W, panelY + panelH, 0x80202020);

        context.fill(panelX, panelY, panelX + PANEL_W, panelY + 14, 0x90303030);
        context.drawTextWithShadow(textRenderer, Text.of("Name"), panelX + 6, panelY + 4, 0xA0A0A0);
        context.drawTextWithShadow(textRenderer, Text.of("Status"), panelX + PANEL_W - 48, panelY + 4, 0xA0A0A0);

        int itemH = 16;
        context.enableScissor(panelX, panelY + 14, panelX + PANEL_W, panelY + panelH);

        List<Account> accounts = AccountManager.getInstance().getAccounts();
        String currentName = client.getSession().getUsername();
        int yy = panelY + 14 - scrollOffset;
        int index = 0;

        for (Account a : accounts) {
            if (yy + itemH > panelY + 14 && yy < panelY + panelH) {
                boolean hovered = mouseX >= panelX && mouseX <= panelX + PANEL_W && mouseY >= yy && mouseY <= yy + itemH;
                boolean sel = a == selectedAccount;
                boolean active = a.getName().equals(currentName);

                if (sel) context.fill(panelX, yy, panelX + PANEL_W, yy + itemH, 0x60454595);
                else if (hovered) context.fill(panelX, yy, panelX + PANEL_W, yy + itemH, 0x40303030);
                else if (index % 2 == 0) context.fill(panelX, yy, panelX + PANEL_W, yy + itemH, 0x15202020);

                context.drawTextWithShadow(textRenderer, Text.of(a.getName()), panelX + 6, yy + 4, sel ? 0xFFFFFF : 0xC0C0C0);

                String onOff = active ? "ON" : "OFF";
                int onOffColor = active ? 0x55FF55 : 0xFF5555;
                context.drawTextWithShadow(textRenderer, Text.of(onOff), panelX + PANEL_W - 48, yy + 4, onOffColor);
            }
            yy += itemH;
            index++;
        }

        context.disableScissor();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;

        int cx = width / 2;
        int panelX = cx - PANEL_W / 2;
        int panelY = 50;
        int itemH = 16;

        List<Account> accounts = AccountManager.getInstance().getAccounts();
        int yy = panelY + 14 - scrollOffset;
        for (Account a : accounts) {
            if (mouseX >= panelX && mouseX <= panelX + PANEL_W && mouseY >= yy && mouseY <= yy + itemH) {
                selectedAccount = a;
                return true;
            }
            yy += itemH;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int panelY = 50;
        int panelH = height - panelY - 34;
        int itemH = 16;
        int contentH = AccountManager.getInstance().getAccounts().size() * itemH;
        int maxScroll = Math.max(0, contentH - (panelH - 14));
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (int)verticalAmount * itemH));
        return true;
    }

    private void login(Account account) {
        try {
            Session session = new Session(
                    account.getName(),
                    UUID.fromString(account.getUuid()),
                    "",
                    Optional.empty(),
                    Optional.empty(),
                    Session.AccountType.MSA
            );
            ((IMinecraftClientAccessor) client).setSession(session);
        } catch (Exception ignored) {}
    }

    private String generateName() {
        Random r = new Random();
        return PREFIXES[r.nextInt(PREFIXES.length)] + SUFFIXES[r.nextInt(SUFFIXES.length)] + (r.nextInt(99) + 1);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
