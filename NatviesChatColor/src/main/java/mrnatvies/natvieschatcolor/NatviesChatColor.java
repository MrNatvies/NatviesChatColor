package mrnatvies.natvieschatcolor;

import mrnatvies.natvieschatcolor.ChatColorCommand;
import mrnatvies.natvieschatcolor.ChatColorPlaceholder;
import mrnatvies.natvieschatcolor.ChatListener;
import org.bukkit.plugin.java.JavaPlugin;

public class NatviesChatColor
        extends JavaPlugin {
    public void onEnable() {
        if (this.getCommand("chatcolor") != null) {
            this.getCommand("chatcolor").setExecutor(new ChatColorCommand(this));
        } else {
            this.getLogger().warning("Команда /chatcolor не зарегистрирована в plugin.yml!");
        }
        this.getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        if (this.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new ChatColorPlaceholder(this).register();
            this.getLogger().info("PlaceholderAPI поддержка включена!");
        } else {
            this.getLogger().warning("PlaceholderAPI не найден. Плейсхолдеры работать не будут.");
        }
        this.saveDefaultConfig();
        this.getLogger().info("NatviesChatColor успешно включен!");
    }

    public void onDisable() {
        this.getLogger().info("NatviesChatColor выключен!");
    }
}