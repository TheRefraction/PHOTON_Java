package photon.console;

import java.util.logging.*;
import javax.swing.JTextArea;

public class GuiLogOutputHandler extends Handler
{

    private int field_998_b[];
    private int field_1001_c;
    Formatter field_999_a;
    private JTextArea field_1000_d;

    public GuiLogOutputHandler(JTextArea jtextarea)
    {
        field_998_b = new int[1024];
        field_1001_c = 0;
        field_999_a = new GuiLogFormatter(this);
        setFormatter(field_999_a);
        field_1000_d = jtextarea;
    }

    public void close()
    {
    }

    public void flush()
    {
    }

    public void publish(LogRecord logrecord)
    {
        int i = field_1000_d.getText().length();
        field_1000_d.append(field_999_a.format(logrecord));
        field_1000_d.setCaretPosition(field_1000_d.getText().length());
        int j = field_1000_d.getText().length() - i;
        if(field_998_b[field_1001_c] != 0)
        {
            field_1000_d.replaceRange("", 0, field_998_b[field_1001_c]);
        }
        field_998_b[field_1001_c] = j;
        field_1001_c = (field_1001_c + 1) % 1024;
    }
}
