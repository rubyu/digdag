package io.digdag.standards.scheduler;

import java.time.ZoneId;
import io.digdag.client.config.Config;
import io.digdag.spi.Scheduler;
import io.digdag.spi.SchedulerFactory;

public class CronSchedulerFactory
        implements SchedulerFactory
{
    @Override
    public boolean matches(Config config)
    {
        return config.has("cron");
    }

    @Override
    public Scheduler newScheduler(Config config, ZoneId timeZone)
    {
        return new CronScheduler(
                config.get("cron", String.class),
                timeZone,
                config.get("delay", Integer.class, 0));
    }

    public static class CronScheduler
            extends AbstractCronScheduler
    {
        public CronScheduler(String cronPattern, ZoneId timeZone, long delaySeconds)
        {
            super(cronPattern, timeZone, delaySeconds);
        }
    }
}
