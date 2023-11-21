package com.crystaldevs.crystal.utils.portscanner;

import java.util.ArrayList;
import java.util.List;

public class PortScannerManager {
    public static List<PScanRunner> scans = new ArrayList<>();

    public static void killAllScans() {
        int i = 0, scansSize = scans.size();
        while (i < scansSize) {
            PScanRunner runner = scans.get(i);
            if (!runner.running) {
            } else {
                runner.cancel();
            }
            i++;
        }
        scans.clear();
    }

    public static class ScanResult {
        private int port;

        private boolean isOpen;

        public ScanResult(int port, boolean isOpen) {
            super();
            this.port = port;
            this.isOpen = isOpen;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public boolean isOpen() {
            return isOpen;
        }

        public void setOpen(boolean isOpen) {
            this.isOpen = isOpen;
        }

    }
}
