package mrnatvies.natvieschatcolor;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import mrnatvies.natvieschatcolor.NatviesChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChatColorPlaceholder
        extends PlaceholderExpansion {
    private final NatviesChatColor plugin;

    public ChatColorPlaceholder(NatviesChatColor plugin) {
        this.plugin = plugin;
    }

    @NotNull
    public String getIdentifier() {
        return "natvies";
    }

    @NotNull
    public String getAuthor() {
        return String.join((CharSequence)", ", this.plugin.getDescription().getAuthors());
    }

    @NotNull
    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    public boolean persist() {
        return true;
    }

    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if ("chatcolor".equalsIgnoreCase(params)) {
            return this.plugin.getConfig().getString("chatColor", "&f");
        }
        return null;
    }
}