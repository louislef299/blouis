package com.github.louislef299.blouis.cmd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class VersionCommandTest {
    
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStream;
    
    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }
    
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
    
    @Test
    void testVersionCommandOutput() {
        VersionCommand command = new VersionCommand();
        command.run();
        
        String output = outputStream.toString().trim();
        assertTrue(output.startsWith("blou version "), 
                   "Output should start with 'blou version ', but was: " + output);
        assertFalse(output.contains("unknown"), 
                    "Version should not be 'unknown' when properties file exists");
    }
}