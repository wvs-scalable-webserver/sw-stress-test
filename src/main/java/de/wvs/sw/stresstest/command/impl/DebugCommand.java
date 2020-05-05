package de.wvs.sw.stresstest.command.impl;

import de.wvs.sw.stresstest.StressTest;
import de.wvs.sw.stresstest.command.Command;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public class DebugCommand extends Command {

    public DebugCommand(String name, String description, String... aliases) {

        super(name, description, aliases);
    }

    @Override
    public boolean execute(String[] args) {

        StressTest.getInstance().changeDebug();

        return true;
    }
}
