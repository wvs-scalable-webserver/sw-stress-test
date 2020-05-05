package de.wvs.sw.stresstest;

import de.progme.iris.IrisConfig;

/**
 * Created by Marvin Erkes on 05.02.20.
 */
public class StressTestFactory {

    public StressTestFactory() {}

    public static StressTest create(IrisConfig config) {

        return new StressTest(config);
    }
}
