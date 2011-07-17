package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OpenWarp;

public class OWQuotaSetCommand extends OWCommand {
    
    protected String usageString;

    public OWQuotaSetCommand(OpenWarp plugin) {
        super(plugin);
        
        this.minimumArgs = 2;
        this.maximumArgs = 3;
        
        this.usageString = "Usage: /warp quota set {public|private} {unlimited|VALUE} [PLAYER NAME]";
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String commandLabel, List<String> args) {
        String type = args.get(0);
        if(!type.equals("public") && !type.equals("private")) {
            sender.sendMessage(ChatColor.YELLOW + this.usageString);
            return true;
        }
        
        String value = args.get(1);
        if(!value.equals("unlimited") && !(Integer.parseInt(value) + "").equals(value)) {
            sender.sendMessage(ChatColor.YELLOW + this.usageString);
            return true;
        }
        
        String playerName = null;
        if(args.size() > 2) {
            playerName = args.get(2);
        }
        
        return true;
    }

}
