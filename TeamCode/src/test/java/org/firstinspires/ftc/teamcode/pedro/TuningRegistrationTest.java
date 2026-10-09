package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.tuning.autotune.TunerRegistrar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TuningRegistrationTest {
    @Before
    public void clearRegistry() {
        TunerRegistrar.deregisterAll();
    }

    @After
    public void cleanRegistry() {
        TunerRegistrar.deregisterAll();
    }

    @Test
    public void sdkRegistrationMakesAllToolsAvailableWithoutDuplicates() {
        // Exercise the real AutoTune registrar rather than its broken annotation scanner.
        Tuning.register(null);
        Tuning.register(null);

        Set<String> names = new HashSet<>();
        for (TunerRegistrar.RegisteredProcedure procedure : TunerRegistrar.getProcedures()) {
            names.add(procedure.name);
            assertNotNull(procedure.procedure);
        }
        assertEquals(4, TunerRegistrar.getProcedures().size());
        assertTrue(names.contains("BioBuzz Mecanum"));
        assertTrue(names.contains("BioBuzz Pinpoint"));
        assertTrue(names.contains("BioBuzz Foresight"));
        assertTrue(names.contains("BioBuzz Tests"));
    }
}
