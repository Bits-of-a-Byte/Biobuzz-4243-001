package org.firstinspires.ftc.teamcode.pedro;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PedroSetupTest {
    @Test
    public void untunedFollowerFailsBeforeTouchingHardware() {
        boolean previous = Constants.FOLLOWER_TUNED;
        try {
            Constants.FOLLOWER_TUNED = false;
            // A null hardware map proves rejection happens before any hardware lookup.
            Constants.create(null);
            fail("Untuned follower must not access hardware or start motion.");
        } catch (IllegalStateException expected) {
            assertTrue(expected.getMessage().contains("AutoTune"));
        } finally {
            Constants.FOLLOWER_TUNED = previous;
        }
    }

    @Test
    public void podOffsetsUseMillimetersWhilePathsUseInches() {
        assertEquals(DistanceUnit.MM, Constants.localizerConfig.offsetUnits.get());
        assertEquals(DistanceUnit.INCH, Constants.localizerConfig.globalDistanceUnit.get());
    }
}
