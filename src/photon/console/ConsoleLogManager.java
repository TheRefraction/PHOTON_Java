package photon.console;

import java.util.logging.*;

public class ConsoleLogManager
{
    public static Logger logger = Logger.getLogger("PHOTON");

    public ConsoleLogManager()
    {
    }

    public static void init()
    {
        ConsoleLogFormatter consolelogformatter = new ConsoleLogFormatter();
        logger.setUseParentHandlers(false);
        ConsoleHandler consolehandler = new ConsoleHandler();
        consolehandler.setFormatter(consolelogformatter);
        logger.addHandler(consolehandler);
        try
        {
            FileHandler filehandler = new FileHandler("console.log", true);
            filehandler.setFormatter(consolelogformatter);
            logger.addHandler(filehandler);
        }
        catch(Exception exception)
        {
            logger.log(Level.WARNING, "Failed to log to console.log", exception);
        }
    }

}
