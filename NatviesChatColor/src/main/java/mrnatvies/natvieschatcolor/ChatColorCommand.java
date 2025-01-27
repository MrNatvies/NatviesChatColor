package mrnatvies.natvieschatcolor;

import mrnatvies.natvieschatcolor.NatviesChatColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatColorCommand
        implements CommandExecutor {
    private final NatviesChatColor plugin;

    public ChatColorCommand(NatviesChatColor plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§4Your Days §7>> §cТолько игроки могут использовать эту команду");
            return true;
        }
        Player player = (Player)((Object)sender);
        if (args.length == 0) {
            player.sendMessage("§4Your Days §7>> §fИспользование §7>> §c/chatcolor §7[§cЦвет]§7 §f- Установить цвет §8| /chatcolor clear §f- Очистить цвет");
            return true;
        }
        String input = String.join((CharSequence)"", args);
        switch (input.toLowerCase()) {
            case "clear": {
                return this.clearColor(player);
            }
            case "reload": {
                return this.reloadConfig(player);
            }
        }
        if (input.contains(",")) {
            return this.setGradientColor(player, input.split(","));
        }
        if (input.startsWith("#") && input.length() == 7 || input.startsWith("&")) {
            return this.setSingleColor(player, input);
        }
        player.sendMessage("§4Your Days §7>> §cНеверный формат цвета. Используйте #RRGGBB или &c");
        return true;
    }

    private boolean setSingleColor(Player player, String color) {
        if (!this.isValidColor(color)) {
            player.sendMessage("§4Your Days §7>> §cНеверный формат цвета. Используйте #RRGGBB или &c");
            return true;
        }
        this.plugin.getConfig().set("playerColors." + player.getName(), color);
        this.plugin.saveConfig();
        String formattedColor = ChatColor.translateAlternateColorCodes('&', color);
        player.sendMessage("§4Your Days §7>> §fВы успешно установили цвет ");
        return true;
    }

    private boolean setGradientColor(Player player, String[] colors) {
        for (String color : colors) {
            if (color.matches("#[0-9a-fA-F]{6}")) continue;
            player.sendMessage("§4Your Days §7>> §cНеверный цвет");
            return true;
        }
        String gradientText = this.generateGradientText(String.join((CharSequence)",", colors), colors);
        this.plugin.getConfig().set("playerColors." + player.getName(), String.join((CharSequence)",", colors));
        this.plugin.saveConfig();
        String message = "§4Your Days §7>> §fВы успешно установили цвет";
        player.sendMessage(message);
        return true;
    }

    private String generateGradientText(String text, String[] colors) {
        StringBuilder gradientMessage = new StringBuilder();
        int totalSteps = text.length();
        int colorSteps = colors.length - 1;
        for (int i = 0; i < totalSteps; ++i) {
            double ratio = (double)i / (double)(totalSteps - 1);
            int startColorIndex = (int)Math.floor(ratio * (double)colorSteps);
            int endColorIndex = Math.min(startColorIndex + 1, colorSteps);
            String startColor = colors[startColorIndex];
            String endColor = colors[endColorIndex];
            double localRatio = ratio * (double)colorSteps - (double)startColorIndex;
            String interpolatedColor = this.interpolateColor(startColor, endColor, localRatio);
            gradientMessage.append(ChatColor.of(interpolatedColor)).append(text.charAt(i));
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

    private boolean isValidColor(String color) {
        return color.matches("#[0-9a-fA-F]{6}") || color.matches("&[0-9a-fA-F]");
    }

    private String formatColorMessage(String color) {
        if (color.startsWith("&")) {
            return ChatColor.translateAlternateColorCodes('&', color);
        }
        if (color.startsWith("#")) {
            return ChatColor.of(color) + color;
        }
        return color;
    }

    private boolean clearColor(Player player) {
        if (!player.hasPermission("chatcolor.clear")) {
            player.sendMessage("§4Your Days §7>> §cУ вас нет прав для использования этой команды");
            return true;
        }
        this.plugin.getConfig().set("playerColors." + player.getName(), null);
        this.plugin.saveConfig();
        player.sendMessage("§4Your Days §7>> §fВы очистили цвет своих сообщений в чате");
        return true;
    }

    private boolean reloadConfig(Player player) {
        if (!player.hasPermission("chatcolor.reload")) {
            player.sendMessage("§4Your Days §7>> §cУ вас нет прав для использования этой команды");
            return true;
        }
        this.plugin.reloadConfig();
        player.sendMessage("§4Your Days §7>> §fПлагин §cNatviesChatColor §fуспешно перезагружен");
        return true;
    }
}