package com.github.louislef299.blouis.cmd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class ScanCommandTest {
    
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    
    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(errorStreamCaptor));
    }
    
    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
    
    @Test
    public void testScanCommandCreation() {
        ScanCommand scanCommand = new ScanCommand();
        assertNotNull(scanCommand);
    }
    
    @Test
    public void testScanCommandExecution() {
        ScanCommand scanCommand = new ScanCommand();
        
        // This test should handle both scenarios:
        // 1. Environment with Bluetooth - should complete successfully
        // 2. Environment without Bluetooth - should fail gracefully
        try {
            scanCommand.run();
            // If we get here, Bluetooth is working and scan completed successfully
            String output = outputStreamCaptor.toString();
            assertTrue(output.contains("device(s):") || output.contains("No devices found"));
        } catch (RuntimeException e) {
            // Expected in CI/environments without Bluetooth
            String errorMessage = errorStreamCaptor.toString();
            assertTrue(errorMessage.contains("Bluetooth") || 
                      errorMessage.contains("D-Bus") ||
                      errorMessage.contains("BlueZ") ||
                      e.getMessage().contains("Bluetooth") ||
                      e.getMessage().contains("D-Bus") ||
                      e.getMessage().contains("BlueZ"));
        }
    }
}