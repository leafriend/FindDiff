package net.folab.finddiff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class FindDiff {

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            usage();
            return;
        }

        findDiff(new File(args[0]), new File(args[1]), "");

    }

    public static void usage() {
        String className = FindDiff.class.getName();
        System.out.println("Usage: ");
        System.out.println("  java " + className + " DIR1 DIR2");
        System.out.println("  java " + className + " FILE1 FILE2");
    }

    public static void findDiff(File lBase, File rBase, String path) {

        File lFile = new File(lBase, path);
        File rFile = new File(rBase, path);

        if (!lFile.exists()) {
            if (!rFile.exists()) {
                System.out.println("!   " + path);
                return;
            } else {
                System.out.println("+   " + path);
                return;
            }
        } else {
            if (!rFile.exists()) {
                System.out.println("-   " + path);
                return;
            }
        }

        if (lFile.isFile()) {
            if (rFile.isFile()) {
                findFileDiff(lFile, rFile, path);
                return;
            } else if (rFile.isDirectory()) {
                System.out.println("<   " + path);
                return;
            } else {
                System.out.println("?   " + path);
                System.out.println("    Unexpected case: left is file but right is neither file nor directory");
                return;
                //throw new RuntimeException("Unexpected case: left is file but right is neither file nor directory - " + path);
            }
        } else if (lFile.isDirectory()) {
            if (rFile.isDirectory()) {
                List<String> names = new ArrayList<String>(Arrays.asList(lFile.list()));
                names.addAll(Arrays.asList(rFile.list()));
                names = new ArrayList<String>(new HashSet<String>(names));
                Collections.sort(names);
                for (String name : names)
                    findDiff(lBase, rBase, path + File.separator + name);
            } else if (rFile.isFile()) {
                System.out.println(">   " + path);
                return;
            } else {
                throw new RuntimeException("Unexpected case: left is directory but right is neither directory nor file - " + path);
            }
        }
    }

    public static void findFileDiff(File lFile, File rFile, String path) {
        String head;
        if (lFile.isFile() && rFile.isFile() && "".equals(path)) {
            head = "~   " + lFile.getName();
        } else {
            head = "~   " + path;
        }
        System.out.print(head);
        if (lFile.length() != rFile.length()) {
            clear(head, false);
            System.out.println("*   " + path);
            return;
        }
        FileInputStream lin = null;
        FileInputStream rin = null;
        byte[] lbuf = new byte[1024 * 1024];
        byte[] rbuf = new byte[1024 * 1024];
        int llen;
        int rlen;
        String stat;
        long length = lFile.length();
        long offset = 0;
        int w = String.valueOf(length).length();
        w = w + ((w - 1) / 3);
        try {
            lin = new FileInputStream(lFile);
            rin = new FileInputStream(rFile);
            stat = ": " + String.format("%6.2f", ((double) offset * 100) / length) + "% (" + String.format("%," + w + "d", offset) + "/" + String.format("%," + w + "d", length) + ")";
            while ((llen = lin.read(lbuf)) > 0) {
                offset += llen;
                System.out.print(stat);
                if ((rlen = rin.read(rbuf)) != llen) {
                    // TODO handle rest bytes
                    throw new RuntimeException(llen + " != " + rlen);
                }
                for (int i = 0; i < llen; i++) {
                    if (lbuf[i] != rbuf[i]) {
                        clear(head, false);
                        System.out.println("*   " + path);
                        return;
                    }
                }
                clear(stat, false);
                stat = ": " + String.format("%6.2f", ((double) offset * 100) / length) + "% (" + String.format("%," + w + "d", offset) + "/" + String.format("%," + w + "d", length) + ")";
            }
            System.out.print(stat);
            clear(stat, true);
            clear(head, true);
        } catch (IOException e) {
            clear(head, false);
            System.out.println("!   " + path);
            e.printStackTrace();
        } finally {
            if (lin != null) {
                try {
                    lin.close();
                } catch (IOException e) {
                }
            }
            if (rin != null) {
                try {
                    rin.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void clear(String path, boolean whitening) {
        for (int i = 0; i < path.length(); i++) {
            System.out.print("\b");
        }
        if (whitening) {
            for (int i = 0; i < path.length(); i++) {
                System.out.print(" ");
            }
            for (int i = 0; i < path.length(); i++) {
                System.out.print("\b");
            }
        }
    }

}
