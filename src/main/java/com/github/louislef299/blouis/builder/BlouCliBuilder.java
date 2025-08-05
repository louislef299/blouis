package com.github.louislef299.blouis.builder;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.builder.CliBuilder;
import com.github.louislef299.blouis.cmd.HelpCommand;
import com.github.louislef299.blouis.cmd.ScanCommand;
import com.github.louislef299.blouis.cmd.VersionCommand;
import com.github.louislef299.blouis.cmd.DevicesCommand;

public class BlouCliBuilder {
    
    private final CliBuilder<Runnable> builder;
    
    public BlouCliBuilder() {
        this.builder = Cli.<Runnable>builder("blou")
                .withDescription("Blou CLI tool")
                .withDefaultCommand(HelpCommand.class);
    }
    
    public BlouCliBuilder withHelpCommand() {
        builder.withCommand(HelpCommand.class);
        return this;
    }
    
    public BlouCliBuilder withScanCommand() {
        builder.withCommand(ScanCommand.class);
        return this;
    }
    
    public BlouCliBuilder withVersionCommand() {
        builder.withCommand(VersionCommand.class);
        return this;
    }
    
    public BlouCliBuilder withDevicesCommand() {
        builder.withCommand(DevicesCommand.class);
        return this;
    }
    
    public BlouCliBuilder withAllCommands() {
        return this.withHelpCommand()
                .withScanCommand()
                .withDevicesCommand()
                .withVersionCommand();
    }
    
    public Cli<Runnable> build() {
        return builder.build();
    }
    
    public static Cli<Runnable> createDefaultCli() {
        return new BlouCliBuilder()
                .withAllCommands()
                .build();
    }
}