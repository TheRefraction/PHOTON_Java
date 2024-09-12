package photon.system;

import java.util.logging.Logger;

public class LogManager {
	
	public static Logger logger = Logger.getLogger("PHOTON");
	
	public static void info(String msg) {
		logger.info(msg);
	}
	
	public static void warning(String msg) {
		logger.warning(msg);
	}
	
	public static void fine(String msg) {
		logger.fine(msg);
	}
	
	public static void finer(String msg) {
		logger.finer(msg);
	}
	
	public static void finest(String msg) {
		logger.finest(msg);
	}
	
	public static void severe(String msg) {
		logger.severe(msg);
	}

}
