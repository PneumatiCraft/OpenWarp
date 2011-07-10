package com.lithium3141.OpenWarp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OpenWarp;

public class OWWarpCommandAdapter extends OWCommand {

    public OWWarpCommandAdapter(OpenWarp plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(args.length == 0) {
            return (new OWWarpListCommand(this.plugin)).execute(sender, command, commandLabel, args);
        } else {
            return (new OWWarpCommand(this.plugin)).execute(sender, command, commandLabel, args);
        }
    }

}
