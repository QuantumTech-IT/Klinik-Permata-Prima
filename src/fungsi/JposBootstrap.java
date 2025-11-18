package fungsi;

import jpos.config.JposEntry;
import jpos.config.simple.SimpleEntryRegistry;
import jpos.config.simple.xml.SimpleXmlRegPopulator;
import jpos.loader.JposServiceLoader;
import jpos.loader.simple.SimpleServiceManager;

import java.io.File;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;

public final class JposBootstrap {
    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    // Pakai file sample yang sudah kamu buktikan jalan
    private static final String JPOS_PATH =
        "C:\\Program Files\\DigitalPersona\\U.are.U SDK\\Windows\\Samples\\UareUSampleJavaPOS\\config\\jpos\\res\\jposUareU.xml";

    private static final String LOGICAL_NAME = "DPFingerprintReader";

    private JposBootstrap() {}

    public static void ensure() {
        if (INITIALIZED.getAndSet(true)) return;   // pastikan hanya sekali

        // arahkan JPOS ke file jpos.xml/jposUareU.xml yg valid
        System.setProperty("jpos.config.populator.class", "jpos.config.simple.xml.SimpleXmlRegPopulator");
        System.setProperty("jpos.config.populator.file", new File(JPOS_PATH).getAbsolutePath());

        try {
            SimpleServiceManager mgr = (SimpleServiceManager) JposServiceLoader.getManager();
            SimpleEntryRegistry reg = (SimpleEntryRegistry) mgr.getEntryRegistry();

            boolean has = false;
            for (Enumeration<?> en = reg.getEntries(); en.hasMoreElements();) {
                JposEntry e = (JposEntry) en.nextElement();
                if (LOGICAL_NAME.equals(e.getLogicalName())) { has = true; break; }
            }
            if (!has) {
                // inject entries dari file jpos
                SimpleXmlRegPopulator pop = new SimpleXmlRegPopulator();
                pop.load(JPOS_PATH);
                for (Enumeration<?> en = pop.getEntries(); en.hasMoreElements();) {
                    JposEntry e = (JposEntry) en.nextElement();
                    reg.addJposEntry(e);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
