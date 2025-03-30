package uj.wmii.pwj.gvt;
import java.util.Scanner;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

enum COMMANDS {

    init {
        public void action(ExitHandler exitHandler, Gvt gvt, String... args) {
            if (gvt.directory.exists()) {
                exitHandler.exit(10, "Current directory is already initialized.");
            } else {
                gvt.init();
            }
        }
    },

    add {
        public void action(ExitHandler exitHandler, Gvt gvt, String... args) {
            if (args.length < 2) {
                exitHandler.exit(20, "Please specify file to add.");
            } else if (new File(args[1]).exists()) {
                gvt.add(args);
            } else {
                exitHandler.exit(21, "File not found. File: " + args[1]);
            }
        }
    },

    detach {
        public void action(ExitHandler exitHandler, Gvt gvt, String... args) {
            if (args.length < 2) {
                exitHandler.exit(30, "Please specify file to detach.");
            } else gvt.detach(args);
        }
    },

    checkout {
        public void action(ExitHandler exitHandler, Gvt gvt, String... args) {
            if (args.length < 2) {
                exitHandler.exit(60, "Invalid version number: ");
            } else if (!new File(".gvt/v" + args[1]).exists()) {
                exitHandler.exit(60, "Invalid version number: " + args[1]);
            } else gvt.checkout(args);
        }
    },

    commit {
        public void action(ExitHandler exitHandler, Gvt gvt, String... args) {
            if (args.length < 2) {
                exitHandler.exit(50, "Please specify file to commit.");
            } else if (!new File(args[1]).exists()) {
                exitHandler.exit(51, "File not found. File: " + args[1]);
            } else gvt.commit(args);
        }
    },

    history {
        public void action(ExitHandler exitHandler, Gvt gvt, String... args) {
            String last = null;
            int n;
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-last")) {
                    last = args[i + 1];
                    break;
                }
            }
            try {
                n = Integer.parseInt(last) - 1;
            } catch (NumberFormatException e) {
                n = Integer.MAX_VALUE;
            }
            try {
                int version = gvt.lastVersion();
                String result = "";
                for (; version >= 0 && n >= 0; n--, version--) {
                    try (Scanner sc = new Scanner(new File(".gvt/v" + version + "/comment"))) {
                        result += version + ": " + sc.nextLine() + "\n";
                    }
                }
                exitHandler.exit(0, result);
            } catch (IOException ioe) {
                exitHandler.exit(-3, "Underlying system problem. See ERR for details.");
            }
        }
    },

    version {
        public void action(ExitHandler exitHandler, Gvt gvt, String... args) {
            int version = 0;
            try {
                if (args.length < 2) {
                    version = gvt.lastVersion();
                } else {
                    version = Integer.parseInt(args[1]);
                }
                if (!new File(".gvt/v" + version).exists()) {
                    exitHandler.exit(60, "Invalid version number: " + version);
                } else {
                    exitHandler.exit(0, "Version: " + version + "\n" + Files.readString(Path.of(".gvt/v" + version + "/comment")));
                }
            } catch (IOException ioe) {
                exitHandler.exit(60, "Invalid version number: " + version);
            }
        }
    };

    public abstract void action(ExitHandler exitHandler, Gvt gvt, String... args);
}

public class Gvt {

    private final ExitHandler exitHandler;

    static File directory = new File(".gvt");

    Gvt(ExitHandler exitHandler) {
        this.exitHandler = exitHandler;
    }

    void isCommandExist(String s) {
        boolean isExist = true;
        try {
            COMMANDS.valueOf(s);
        } catch (IllegalArgumentException e) {
            isExist = false;
        }
        if (!isExist) {
            this.exitHandler.exit(1, "Unknown command {specifed-command}.");
        }
    }

    public int lastVersion() {
        int version = 0;
        while (new File(".gvt/v" + version).exists()) version++;
        return (version - 1);
    }

    boolean zeroArguments(String... args) {
        if (args.length == 0) {
            this.exitHandler.exit(1, "Please specify command.");
            return false;
        } else return true;
    }

    void copyFile(String sourcePath, String destinationPath) throws IOException {
        FileInputStream sourceStream = new FileInputStream(sourcePath);
        FileOutputStream destinationStream = new FileOutputStream(destinationPath);
        byte[] buf = new byte[1024];
        int bytesRead;
        while ((bytesRead = sourceStream.read(buf)) > 0) {
            destinationStream.write(buf, 0, bytesRead);
        }
        sourceStream.close();
        destinationStream.close();
    }

    public static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation) throws IOException {
        Files.walk(Paths.get(sourceDirectoryLocation)).forEach(source -> {
            Path destination = Paths.get(destinationDirectoryLocation, source.toString().substring(sourceDirectoryLocation.length()));
            try {
                if (!sourceDirectoryLocation.equals(source.toString())) {
                    Files.copy(source, destination, REPLACE_EXISTING);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    void init() {
        try {
            directory.mkdir();
            new File(".gvt/v0").mkdir();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(".gvt/v0/comment"));
            bufferedWriter.write("GVT initialized.");
            bufferedWriter.close();
            exitHandler.exit(0, "Current directory initialized successfully.");
        } catch (IOException e) {
            exitHandler.exit(-3, "Underlying system problem. See ERR for details.");
        }
    }

    void add(String... args) {
        try {
            int version = lastVersion();
            if (new File(".gvt/v" + version + "/" + args[1]).exists()) {
                exitHandler.exit(0, "File already added. File: " + args[1]);
                return;
            }
            new File(".gvt/v" + (version + 1)).mkdir();
            copyDirectory(".gvt/v" + version, ".gvt/v" + (version + 1));
            copyFile(args[1], ".gvt/v" + (version + 1) + "/" + args[1]);
            String comment = null;
            for (int i = 1; i < args.length; i++) {
                if (args[i].equals("-m")) {
                    comment = args[i + 1];
                }
            }
            if (comment == null) {
                comment = "File added successfully. File: " + args[1];
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(".gvt/v" + (version + 1) + "/comment"));
            bufferedWriter.write(comment);
            bufferedWriter.close();
            exitHandler.exit(0, "File added successfully. File: " + args[1]);
        } catch (IOException ioe) {
            exitHandler.exit(22, "File cannot be added. See ERR for details. File: " + args[1]);
        }
    }

    void detach(String... args) {
        try {
            int version = lastVersion();
            if (!new File(".gvt/v" + version + "/" + args[1]).exists()) {
                exitHandler.exit(0, "File is not added to gvt. File: " + args[1]);
                return;
            }
            new File(".gvt/v" + (version + 1)).mkdir();
            copyDirectory(".gvt/v" + version, ".gvt/v" + (version + 1));
            BufferedWriter bufferedWriter = getBufferedWriter(args, version);
            bufferedWriter.close();
            exitHandler.exit(0, "File detached successfully. File: " + args[1]);
        } catch (IOException ioe) {
            exitHandler.exit(31, "File cannot be detached, see ERR for details. File: " + args[1]);
        }
    }

    private static BufferedWriter getBufferedWriter(String[] args, int version) throws IOException {
        new File(".gvt/v" + (version + 1) + "/" + args[1]).delete();
        String comment = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-m")) {
                comment = args[i + 1];
            }
        }
        if (comment == null) {
            comment = "File detached successfully. File: " + args[1];
            }
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(".gvt/v" + (version + 1) + "/comment"));
        bufferedWriter.write(comment);
        return bufferedWriter;
    }

    void checkout(String... args) {
        try {
            copyDirectory(".gvt/v" + args[1], ".");
            exitHandler.exit(0, "Checkout successful for version: " + args[1]);
        } catch (IOException ioe) {
            exitHandler.exit(-3, "Underlying system problem. See ERR for details.");
        }
    }

    void commit(String... args) {
        try {
            int version = lastVersion();
            if (!new File(".gvt/v" + version + "/" + args[1]).exists()) {
                exitHandler.exit(0, "File is not added to gvt. File: " + args[1]);
                return;
            }
            new File(".gvt/v" + (version + 1)).mkdir();
            copyFile(args[1], ".gvt/v" + (version + 1) + "/" + args[1]);
            String comment = null;
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-m")) {
                    comment = args[i + 1];
                }
            }
            if (comment == null) {
                comment = "File committed successfully. File: " + args[1];
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(".gvt/v" + (version + 1) + "/comment"));
            bufferedWriter.write(comment);
            bufferedWriter.close();
            exitHandler.exit(0, "File committed successfully. File: " + args[1]);
        } catch (IOException e) {
            exitHandler.exit(52, "File cannot be committed, see ERR for details. File: " + args[1]);
        }
    }

    public static void main(String... args) {
        Gvt gvt = new Gvt(new ExitHandler());
        gvt.mainInternal(args);
    }

    void mainInternal(String... args) {
        if (!zeroArguments(args)) return;
        String s = args[0];
        if (!new File(".gvt").exists()) {
            exitHandler.exit(-2, "Current directory is not initialized. Please use init command to initialize.");
        }
        isCommandExist(s);
        COMMANDS command = COMMANDS.valueOf(s);
        command.action(this.exitHandler, this, args);
    }
}