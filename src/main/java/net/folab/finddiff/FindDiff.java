package net.folab.finddiff;

import java.io.File;
import java.io.FileNotFoundException;
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
        if (lFile.length() != rFile.length()) {
                System.out.println("*   " + path);
        }
    }

}
