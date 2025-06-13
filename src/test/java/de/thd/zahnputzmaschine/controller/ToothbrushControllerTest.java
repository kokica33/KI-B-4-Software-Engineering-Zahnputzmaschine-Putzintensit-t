package de.thd.zahnputzmaschine.controller;

import de.thd.zahnputzmaschine.model.IntensityLevel;
import de.thd.zahnputzmaschine.model.state.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

public class ToothbrushControllerTest {

    private ToothbrushController controller;

    @BeforeEach
    public void setUp() {
        controller = new ToothbrushController();
    }

    @Test
    public void testInitialState() {
        assertEquals(IntensityLevel.OFF, controller.getCurrentIntensity());
    }
}