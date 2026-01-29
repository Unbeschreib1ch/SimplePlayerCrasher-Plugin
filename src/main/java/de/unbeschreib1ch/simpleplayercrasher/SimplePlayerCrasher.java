package de.unbeschreib1ch.simpleplayercrasher;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SimplePlayerCrasher extends JavaPlugin implements CommandExecutor {

    private static final String PREFIX =
            "§7[§4§lSimplePlayerCrasher§7] §8» ";

    @Override
    public void onEnable() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }
        reloadConfig();

        if (getCommand("crash") != null) {
            getCommand("crash").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!getConfig().getBoolean("crashcmd-enabled")) {
            sender.sendMessage(msg("command_disabled"));
            return true;
        }

        if (!sender.hasPermission("simpleplayercrash.use")) {
            sender.sendMessage(msg("no_permission"));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(msg("wrong_syntax"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);

        if (target == null) {
            sender.sendMessage(msg("player_offline"));
            return true;
        }

        Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "execute as " + target.getName() +
                        " at @s run particle minecraft:cloud ~ ~ ~ 0 0 0 100 1000000000 force @s"
        );

        sender.sendMessage(
                msg("crash_success").replace("%player%", target.getName())
        );
        return true;
    }

    private String msg(String key) {
        String lang = getConfig().getString("language", "german").toLowerCase();

        switch (lang) {
            case "english":
                return switch (key) {
                    case "no_permission" -> PREFIX + "§cYou don't have permission!";
                    case "wrong_syntax" -> PREFIX + "§cWrong syntax! Correct usage: §e/crash <player>";
                    case "player_offline" -> PREFIX + "§cThis player is not online!";
                    case "command_disabled" -> PREFIX + "§cThis command is disabled!";
                    case "crash_success" -> PREFIX + "§aThe game of §e%player% §ahas been crashed!";
                    default -> PREFIX + "§cUnknown message!";
                };

            default:
                return switch (key) {
                    case "no_permission" -> PREFIX + "§cDazu hast du keine Rechte!";
                    case "wrong_syntax" -> PREFIX + "§cFalscher Syntax! Korrekte Nutzung: §e/crash <player>";
                    case "player_offline" -> PREFIX + "§cDieser Spieler ist nicht online!";
                    case "command_disabled" -> PREFIX + "§cDieser Command ist deaktiviert!";
                    case "crash_success" -> PREFIX + "§aDas Spiel von §e%player% §astürzt nun ab!";
                    default -> PREFIX + "§cUnbekannte Nachricht!";
                };
        }
    }
}
