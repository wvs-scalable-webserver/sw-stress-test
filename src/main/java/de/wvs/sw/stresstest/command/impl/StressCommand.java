package de.wvs.sw.stresstest.command.impl;

import de.progme.hermes.client.HermesClient;
import de.progme.hermes.client.HermesClientFactory;
import de.progme.hermes.shared.http.Headers;
import de.progme.hermes.shared.http.Response;
import de.wvs.sw.stresstest.StressTest;
import de.wvs.sw.stresstest.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public class StressCommand extends Command {

    private static Logger logger = LoggerFactory.getLogger(StressCommand.class);

    public StressCommand(String name, String description, String... aliases) {

        super(name, description, aliases);
    }

    @Override
    public boolean execute(String[] args) {

        if (args.length < 3) {
            logger.info("Syntax: stress <host> <threads> <ms>");
            return false;
        }

        String hostname = args[0];
        int threads = Integer.parseInt(args[1]);
        int time = Integer.parseInt(args[2]);

        logger.info("Starting stress on " + hostname + " for " + time + "s..");

        for(int i = 0; i < threads; i++) {
            StressTest.getInstance().getScheduledExecutorService().scheduleAtFixedRate(() -> {
                try {
                    HermesClient client = HermesClientFactory.create();
                    Response response = client.get(new URL(hostname), Headers.empty());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 0, time, TimeUnit.MILLISECONDS);
        }

        return true;
    }
}
