package mrnatvies.natvieschatcolor;

import mrnatvies.natvieschatcolor.NatviesChatColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener
        implements Listener {
    private final NatviesChatColor plugin;

    public ChatListener(NatviesChatColor plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String colorCode = this.plugin.getConfig().getString("playerColors." + player.getName(), "");
        if (colorCode.isEmpty()) {
            return;
        }
        String message = event.getMessage();
        if (colorCode.contains(",")) {
            String[] colors = colorCode.split(",");
            message = this.applyGradient(colors, message);
        } else if (colorCode.startsWith("#")) {
            try {
                message = ChatColor.of(colorCode) + message;
            }
            catch (IllegalArgumentException e) {
                player.sendMessage(ChatColor.RED + "Неверный HEX цвет.");
            }
        } else {
            message = ChatColor.translateAlternateColorCodes('&', colorCode) + message;
        }
        event.setMessage(message);
    }

    private String applyGradient(String[] colors, String message) {
        StringBuilder gradientMessage = new StringBuilder();
        int totalSteps = message.length();
        int colorSteps = colors.length - 1;
        for (int i = 0; i < totalSteps; ++i) {
            double ratio = (double)i / (double)(totalSteps - 1);
            int startColorIndex = (int)Math.floor(ratio * (double)colorSteps);
            int endColorIndex = Math.min(startColorIndex + 1, colorSteps);
            String startColor = colors[startColorIndex];
            String endColor = colors[endColorIndex];
            double localRatio = ratio * (double)colorSteps - (double)startColorIndex;
            String interpolatedColor = this.interpolateColor(startColor, endColor, localRatio);
            gradientMessage.append(ChatColor.of(interpolatedColor)).append(message.charAt(i));
        }
        return gradientMessage.toString();
    }

    private String interpolateColor(String from, String to, double ratio) {
        int fromRed = Integer.parseInt(from.substring(1, 3), 16);
        int fromGreen = Integer.parseInt(from.substring(3, 5), 16);
        int fromBlue = Integer.parseInt(from.substring(5, 7), 16);
        int toRed = Integer.parseInt(to.substring(1, 3), 16);
        int toGreen = Integer.parseInt(to.substring(3, 5), 16);
        int toBlue = Integer.parseInt(to.substring(5, 7), 16);
        int red = (int)((double)fromRed + (double)(toRed - fromRed) * ratio);
        int green = (int)((double)fromGreen + (double)(toGreen - fromGreen) * ratio);
        int blue = (int)((double)fromBlue + (double)(toBlue - fromBlue) * ratio);
        return String.format("#%02X%02X%02X", red, green, blue);
    }
}