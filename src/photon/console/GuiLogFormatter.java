package photon.console;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.*;

class GuiLogFormatter extends Formatter
{

    final GuiLogOutputHandler outputHandler; 

    GuiLogFormatter(GuiLogOutputHandler guilogoutputhandler)
    {
        outputHandler = guilogoutputhandler;
    }

    public String format(LogRecord logrecord)
    {
        StringBuilder stringbuilder = new StringBuilder();
        Level level = logrecord.getLevel();
        if(level == Level.FINEST)
        {
            stringbuilder.append("[FINEST] ");
        } else
        if(level == Level.FINER)
        {
            stringbuilder.append("[FINER] ");
        } else
        if(level == Level.FINE)
        {
            stringbuilder.append("[FINE] ");
        } else
        if(level == Level.INFO)
        {
            stringbuilder.append("[INFO] ");
        } else
        if(level == Level.WARNING)
        {
            stringbuilder.append("[WARNING] ");
        } else
        if(level == Level.SEVERE)
        {
            stringbuilder.append("[SEVERE] ");
        } else
        if(level == Level.SEVERE)
        {
            stringbuilder.append((new StringBuilder()).append("[").append(level.getLocalizedName()).append("] ").toString());
        }
        stringbuilder.append(logrecord.getMessage());
        stringbuilder.append('\n');
        Throwable throwable = logrecord.getThrown();
        if(throwable != null)
        {
            StringWriter stringwriter = new StringWriter();
            throwable.printStackTrace(new PrintWriter(stringwriter));
            stringbuilder.append(stringwriter.toString());
        }
        return stringbuilder.toString();
    }
}
