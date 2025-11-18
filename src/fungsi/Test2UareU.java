package fungsi;

import jpos.Biometrics;
import jpos.JposException;
import jpos.config.JposEntry;
import jpos.config.simple.SimpleEntryRegistry;
import jpos.config.simple.xml.SimpleXmlRegPopulator;
import jpos.loader.JposServiceLoader;
import jpos.loader.simple.SimpleServiceManager;

import java.io.File;
import java.util.Enumeration;

public class Test2UareU {

    // samakan dengan logicalName di jpos.xml sample
    private static final String LOGICAL_NAME = "DPFingerprintReader";

    public static void main(String[] args) {
        try {
            System.out.println("=== Test2UareU (force Program Files jpos.xml) ===");
            System.out.println("JRE arch = " + System.getProperty("sun.arch.data.model") + "-bit");
            System.out.println("java.home = " + System.getProperty("java.home"));

            // 0) allow manual override via VM option: -Djpos.xml.override="C:\path\jpos.xml"
            String override = System.getProperty("jpos.xml.override");

            // 1) cari jpos.xml / jposUareU.xml dari Program Files / E:\Samples
            String jposPath = (override != null && new File(override).isFile())
                    ? new File(override).getAbsolutePath()
                    : locateJposXmlFromInstalledSamples();

            if (jposPath == null) {
                System.err.println("ERROR: Tidak menemukan jpos.xml di Program Files / E:\\Samples.");
                System.err.println("Tips: set VM Option -Djpos.xml.override=\"C:\\\\Program Files\\\\DigitalPersona\\\\U.are.U SDK\\\\Windows\\\\Samples\\\\UareUSampleJavaPOS\\\\config\\\\jpos\\\\res\\\\jpos.xml\"");
                return;
            }
            System.out.println("Paksa pakai jpos config = " + jposPath);

            // 2) set properti JPOS supaya loader pakai file itu
            System.setProperty("jpos.config.populator.class", "jpos.config.simple.xml.SimpleXmlRegPopulator");
            System.setProperty("jpos.config.populator.file", jposPath);

            // 3) tampilkan registry & injeksi jika kosong
            SimpleServiceManager mgr = (SimpleServiceManager) JposServiceLoader.getManager();
            SimpleEntryRegistry reg = (SimpleEntryRegistry) mgr.getEntryRegistry();

            int before = dumpRegistry("Entries in JCL registry (before):", reg.getEntries());
            if (before == 0) {
                System.out.println("Registry kosong → inject entries dari jpos.xml …");
                SimpleXmlRegPopulator pop = new SimpleXmlRegPopulator();
                pop.load(jposPath);
                Enumeration<?> en = pop.getEntries();
                while (en.hasMoreElements()) {
                    JposEntry e = (JposEntry) en.nextElement();
                    reg.addJposEntry(e);
                    System.out.println(" + injected: " + e.getLogicalName() +
                            " | serviceClass=" + e.getPropertyValue("serviceClass"));
                }
            }
            dumpRegistry("Entries in JCL registry (after):", reg.getEntries());

            // 4) uji open → claim → enable
            Biometrics bio = new Biometrics();
            try {
                System.out.println("Opening logicalName = " + LOGICAL_NAME);
                bio.open(LOGICAL_NAME);
                bio.claim(5000);
                bio.setDeviceEnabled(true);
                System.out.println("OK: device enabled");
                try { System.out.println("DeviceServiceVersion = " + bio.getDeviceServiceVersion()); }
                catch (Exception ignore) {}
            } finally {
                try { bio.setDeviceEnabled(false); } catch (Exception ignore) {}
                try { bio.release(); }              catch (Exception ignore) {}
                try { bio.close(); }                catch (Exception ignore) {}
            }

            System.out.println("=== Done ===");
        } catch (Exception ex) {
            System.err.println("GAGAL: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static String locateJposXmlFromInstalledSamples() {
        String pf64 = System.getenv("ProgramFiles");          // C:\Program Files
        String pf86 = System.getenv("ProgramFiles(x86)");     // C:\Program Files (x86)

        String[] bases = new String[] {
                pf64, pf86,
                "C:\\Program Files", "C:\\Program Files (x86)",
                "E:\\Program Files", "E:\\Samples", "E:\\"
        };
        String[] rels = new String[] {
                "DigitalPersona\\U.are.U SDK\\Windows\\Samples\\UareUSampleJavaPOS\\config\\jpos\\res\\jpos.xml",
                "DigitalPersona\\U.are.U SDK\\Windows\\Samples\\UareUSampleJavaPOS\\config\\jpos\\res\\jposUareU.xml"
        };

        for (String b : bases) {
            if (b == null) continue;
            for (String r : rels) {
                File f = new File(b + File.separator + r);
                if (f.isFile()) return f.getAbsolutePath();
            }
        }
        return null;
    }

    private static int dumpRegistry(String title, Enumeration<?> entries) {
        System.out.println(title);
        int n = 0;
        while (entries.hasMoreElements()) {
            JposEntry e = (JposEntry) entries.nextElement();
            Object svc = e.getPropertyValue("serviceClass");
            System.out.println(" - " + e.getLogicalName() + (svc != null ? " | serviceClass=" + svc : ""));
            n++;
        }
        return n;
    }
}
