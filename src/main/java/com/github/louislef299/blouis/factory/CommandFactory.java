package com.github.louislef299.blouis.factory;

import com.github.louislef299.blouis.cmd.VersionCommand;

public class CommandFactory {
    
    public static VersionCommand createVersionCommand() {
        return new VersionCommand();
    }
    
    public static <T extends Runnable> T createCommand(Class<T> commandClass) {
        if (commandClass == VersionCommand.class) {
            return commandClass.cast(createVersionCommand());
        }
        
        throw new IllegalArgumentException("Unknown command class: " + commandClass.getName());
    }
}