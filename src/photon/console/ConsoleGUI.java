package photon.console;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


public class ConsoleGUI extends JComponent
{

    public static Logger logger = Logger.getLogger("PHOTON");
    private static JFrame jframe;

    public static void initGui()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception exception) { }
        ConsoleGUI servergui = new ConsoleGUI();
        jframe = new JFrame("PHOTON Console");
        jframe.add(servergui);
        jframe.pack();
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //jframe.setLocationRelativeTo(null);
        jframe.setVisible(true);
    }

    public ConsoleGUI()
    {
        setPreferredSize(new Dimension(400, 400));
        setLayout(new BorderLayout());
        try
        {
            add(getLogComponent(), "Center");
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    private JComponent getLogComponent()
    {
        JPanel jpanel = new JPanel(new BorderLayout());
        JTextArea jtextarea = new JTextArea();
        logger.addHandler(new GuiLogOutputHandler(jtextarea));
        JScrollPane jscrollpane = new JScrollPane(jtextarea, 22, 30);
        jtextarea.setEditable(false);
        jpanel.add(jscrollpane, "Center");
        jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Log"));
        return jpanel;
    }

    public void log(String s)
    {
        logger.info(s);
    }
    
    public static void close() {
    	jframe.dispatchEvent(new WindowEvent(jframe, WindowEvent.WINDOW_CLOSING));
    }

}
