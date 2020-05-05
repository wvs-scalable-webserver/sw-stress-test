package de.wvs.sw.stresstest;

import ch.qos.logback.classic.Level;
import de.progme.iris.IrisConfig;
import de.wvs.sw.shared.application.SWSlave;
import de.wvs.sw.stresstest.command.Command;
import de.wvs.sw.stresstest.command.CommandManager;
import de.wvs.sw.stresstest.command.impl.DebugCommand;
import de.wvs.sw.stresstest.command.impl.EndCommand;
import de.wvs.sw.stresstest.command.impl.HelpCommand;
import de.wvs.sw.stresstest.command.impl.StressCommand;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Pattern;

/**
 * Created by Marvin Erkes on 05.02.20.
 */
public class StressTest {

    @Getter
    public static de.wvs.sw.stresstest.StressTest instance;

    private static final String SLAVE_PACKAGE_NAME = "de.wvs.sw.stresstest";
    private static final Pattern ARGS_PATTERN = Pattern.compile(" ");
    private static Logger logger = LoggerFactory.getLogger(StressTest.class);
    private static ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(SLAVE_PACKAGE_NAME);

    @Getter
    private IrisConfig config;

    @Getter
    private ScheduledExecutorService scheduledExecutorService;

    @Getter
    private SWSlave slave;

    @Getter
    private CommandManager commandManager;

    private Scanner scanner;

    public StressTest(IrisConfig config) {

        de.wvs.sw.stresstest.StressTest.instance = this;

        this.config = config;
    }

    public void start() {

        this.scheduledExecutorService = Executors.newScheduledThreadPool(10);

        this.slave = new SWSlave();

        this.commandManager = new CommandManager();
        commandManager.addCommand(new HelpCommand("help", "List of available commands", "h"));
        commandManager.addCommand(new EndCommand("end", "Stops the load balancer", "stop", "exit"));
        commandManager.addCommand(new DebugCommand("debug", "Turns the debug mode on/off", "d"));
        commandManager.addCommand(new StressCommand("stress", "Stress host via Layer 7"));
    }

    public void stop() {

        logger.info("Stress Test is going to be stopped");

        // Close the scanner
        scanner.close();

        this.scheduledExecutorService.shutdown();

        logger.info("Stress Test has been stopped");
    }

    public void console() {

        scanner = new Scanner(System.in);

        try {
            String line;
            while ((line = scanner.nextLine()) != null) {
                if (!line.isEmpty()) {
                    String[] split = ARGS_PATTERN.split(line);

                    if (split.length == 0) {
                        continue;
                    }

                    // Get the de.wvs.stresstest.command name
                    String commandName = split[0].toLowerCase();

                    // Try to get the de.wvs.stresstest.command with the name
                    Command command = commandManager.findCommand(commandName);

                    if (command != null) {
                        logger.info("Executing command: {}", line);

                        String[] cmdArgs = Arrays.copyOfRange(split, 1, split.length);
                        command.execute(cmdArgs);
                    } else {
                        logger.info("Command not found!");
                    }
                }
            }
        } catch (IllegalStateException ignore) {}
    }

    public void changeDebug(Level level) {

        // Set the log level to debug or info based on the config value
        rootLogger.setLevel(level);

        logger.info("Logger level is now {}", rootLogger.getLevel());
    }

    public void changeDebug() {

        // Change the log level based on the current level
        changeDebug((rootLogger.getLevel() == Level.INFO) ? Level.DEBUG : Level.INFO);
    }
}
