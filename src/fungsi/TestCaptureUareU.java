package fungsi;

import jpos.config.JposEntry;
import jpos.config.simple.SimpleEntryRegistry;
import jpos.config.simple.xml.SimpleXmlRegPopulator;
import jpos.loader.JposServiceLoader;
import jpos.loader.simple.SimpleServiceManager;

import java.io.File;
import java.nio.file.Files;
import java.util.Enumeration;

public class TestCaptureUareU {
    public static void main(String[] args) {
        // Paksa pakai jpos config sample yang sudah terbukti jalan
        String jposPath = "C:\\Program Files\\DigitalPersona\\U.are.U SDK\\Windows\\Samples\\UareUSampleJavaPOS\\config\\jpos\\res\\jposUareU.xml";
        try {
            //ensureJposConfigAndInject(jposPath, UareUHelper.LOGICAL_NAME);

            System.out.println("Silakan tempelkan jari di sensor... (maks 30 detik)");
            byte[] tpl = UareUHelper.captureOnce(30);
            System.out.println("OK: captured " + tpl.length + " bytes.");
            File out = new File("finger_tpl.bin");
            Files.write(out.toPath(), tpl);
            System.out.println("Disimpan ke: " + out.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Gagal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void ensureJposConfigAndInject(String jposPath, String logicalName) throws Exception {
        File f = new File(jposPath);
        System.setProperty("jpos.config.populator.class", "jpos.config.simple.xml.SimpleXmlRegPopulator");
        System.setProperty("jpos.config.populator.file", f.getAbsolutePath());
        System.out.println("Use jpos config: " + f.getAbsolutePath() + " exists=" + f.exists());

        SimpleServiceManager mgr = (SimpleServiceManager) JposServiceLoader.getManager();
        SimpleEntryRegistry reg = (SimpleEntryRegistry) mgr.getEntryRegistry();

        boolean has = false;
        for (Enumeration<?> en = reg.getEntries(); en.hasMoreElements();) {
            JposEntry e = (JposEntry) en.nextElement();
            if (logicalName.equals(e.getLogicalName())) has = true;
        }
        if (!has) {
            System.out.println("Registry tidak punya " + logicalName + " → inject dari jpos.xml");
            SimpleXmlRegPopulator pop = new SimpleXmlRegPopulator();
            pop.load(f.getAbsolutePath());
            for (Enumeration<?> en = pop.getEntries(); en.hasMoreElements();) {
                JposEntry e = (JposEntry) en.nextElement();
                reg.addJposEntry(e);
                System.out.println(" + injected: " + e.getLogicalName() +
                        " | serviceClass=" + e.getPropertyValue("serviceClass"));
            }
        }
        System.out.println("Registry entries now:");
        for (Enumeration<?> en = reg.getEntries(); en.hasMoreElements();) {
            JposEntry e = (JposEntry) en.nextElement();
            System.out.println(" - " + e.getLogicalName());
        }
    }
}
