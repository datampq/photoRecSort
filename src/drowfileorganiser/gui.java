/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drowfileorganiser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

/**
 *
 * @author datampq
 */
public class gui extends JFrame {

    Thread main;
    public manager man;
    public String title = "Drow File Organiser v0.1a";
    public String copyright = "Created by Ivan-Asen Myurvedov 06-12-2019";
    public File rootPath;
    public File newPath;
    public LinkedList<fileTypeNode> knownFileTypes;
    public LinkedList<rootNode> rootNodes;
    public Color primary = Color.decode("0x3498db");
    public Color secondary = Color.decode("0x02980b9");
    public Color text = Color.decode("0xffffff");
    public Color bg = Color.decode("0x2c3e50");
    public int width = 620;

    public gui() {
        knownFileTypes = new LinkedList();
        rootNodes = new LinkedList();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 4 - this.getSize().width / 2, dim.height / 4 - this.getSize().height / 2);
        FrameDragListener frameDragListener = new FrameDragListener(this);
        this.addMouseListener(frameDragListener);
        this.addMouseMotionListener(frameDragListener);
        this.setUndecorated(true);
        this.setBackground(bg);
        add(content());

        pack();
        setVisible(true);
    }
    button inputBtn;
    button outputBtn;
    button processBtn;
    button exitButton;
    JLabel inputDir;
    JLabel outputDir;
    JLabel status;
    JLabel progress;
    JProgressBar rootProgress;
    JProgressBar nodeProgress;

    private JPanel content() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(bg);
        content.add(top());
        content.add(mid());
        content.add(bot());
        return content;

    }

    private JPanel bot() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
        content.setBackground(bg);
        content.setPreferredSize(new Dimension(width, 35));
        JLabel copy = new JLabel(title + " " + copyright);
        copy.setForeground(text);
        content.add(copy);
        return content;
    }

    private JPanel top() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
        content.setBackground(bg);
        content.setPreferredSize(new Dimension(width, 35));
        inputBtn = new button("1. DIR IN", "selectIn", width / 4, 35, this);
        content.add(inputBtn);
        outputBtn = new button("2. DIR OUT", "selectOut", width / 4, 35, this);
        content.add(outputBtn);
        processBtn = new button("3. PROCESS", "process", width / 4, 35, this);
        content.add(processBtn);
        exitButton = new button("EXIT", "exit", width / 4, 35, this);
        content.add(exitButton);
        return content;
    }

    private JPanel mid() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(bg);
        content.setPreferredSize(new Dimension(width, 120));

        inputDir = new JLabel("Select input dir first!");
        inputDir.setForeground(text);
        content.add(inputDir);

        outputDir = new JLabel("Select output dir first!");
        outputDir.setForeground(text);
        content.add(outputDir);

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(bg);
        inner.setPreferredSize(new Dimension(width, 80));
        status = new JLabel("Idle");
        status.setForeground(text);
        inner.add(status);

        progress = new JLabel("Idle");
        progress.setForeground(text);
        inner.add(progress);

        content.add(inner);

        JPanel inner2 = new JPanel();
        inner2.setLayout(new BoxLayout(inner2, BoxLayout.X_AXIS));
        inner2.setBackground(bg);
        inner2.setPreferredSize(new Dimension(width, 40));
        rootProgress = new JProgressBar(0, 100);
        inner2.add(rootProgress);
        nodeProgress = new JProgressBar(0, 100);
        inner2.add(nodeProgress);
        content.add(inner2);

        return content;
    }

    private class dialog extends JFrame {

        public dialog(String title, String msg) {
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            this.setLocation(dim.width / 4 - this.getSize().width / 2, dim.height / 4 - this.getSize().height / 2);
            this.setBackground(bg);
            add(content(title, msg));
            pack();
            setVisible(true);
        }

        private JPanel content(String title, String msg) {
            JPanel content = new JPanel();
            content.setPreferredSize(new Dimension(520, 120));
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBackground(bg);

            JLabel l = new JLabel(title);
            l.setForeground(text);
            content.add(l);

            JPanel content1 = new JPanel();
            content1.setLayout(new BoxLayout(content1, BoxLayout.X_AXIS));
            content1.setBackground(bg);
            content1.add(l);
            content.add(content1);

            JLabel l2 = new JLabel(msg);
            l2.setForeground(primary);
            l2.setBackground(bg);
            
            JPanel content2 = new JPanel();
            content2.setBackground(bg);
            content2.setLayout(new BoxLayout(content2, BoxLayout.X_AXIS));

            content2.add(l2);
            content.add(content2);

            return content;
        }
    }

    public long numFiles = 0;
    public long numDirs = 0;

    private class fileTypeNode {

        public String ext;
        public File path;

        public fileTypeNode(String e, File roothPath) {
            ext = e;
            ext = ext.replaceAll("\\.", "_");
            System.out.println("ext is" + ext);
            path = new File(roothPath.getAbsolutePath() + File.separator + ext);
            if (!path.exists()) {
                path.mkdir();
                System.out.println("creating dir: " + path.getPath());
            }
        }

        public String getNewPath() {
            return path.getPath();
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }

    public class node {

        public LinkedList<File> files;
        public final File path;

        public node(File p) {
            path = p;
            files = new LinkedList();
            scan(path);
            numDirs++;
        }

        private void scan(File path) {
            File[] fList = path.listFiles();
            nodeProgress.setMaximum(fList.length);
            int index = 0;
            if (fList != null) {
                for (File file : fList) {
                    nodeProgress.setValue(index);
                    if (file.isFile()) {
                        files.add(file);
                        System.out.println("Adding file:" + file.getPath());
                        numFiles++;
                        fileTypeNode n = new fileTypeNode(getFileExtension(file), newPath);
                        try {
                            status.setText("Moving file from:" + file.getPath() + " to:" + n.getNewPath() + "(" + index + "/" + fList.length + ")");
                            String np = n.getNewPath() + File.separator + file.getName();
                            System.out.println("------->" + np);
                            Files.move(Paths.get(file.getPath()), Paths.get(np), StandardCopyOption.REPLACE_EXISTING);
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                    index++;
                }
            }
            status.setText("done!");
            progress.setText("done!");
            nodeProgress.setValue(fList.length);
        }
    }

    public class rootNode {

        public LinkedList<File> dirs;
        public LinkedList<node> nodes;
        public final File path;

        public rootNode(File p) {
            path = p;
            dirs = new LinkedList();
            nodes = new LinkedList();
            scan(path);
            inputBtn.enabled = true;
            inputBtn.enabled = true;
            outputBtn.enabled = true;
            processBtn.enabled = true;
            exitButton.enabled = true;
            status.setText("Idle");
        }
        int indexNodeRott = 0;

        private void scan(File path) {
            File[] fList = path.listFiles();
            rootProgress.setMaximum(fList.length);
            indexNodeRott = 0;
            Thread thread = new Thread() {
                public void run() {
                    if (fList != null) {
                        for (File file : fList) {
                            rootProgress.setValue(indexNodeRott);
                            if (file.isDirectory()) {
                                dirs.add(file);
                                progress.setText("Working with node:" + file.getPath() + "(" + indexNodeRott + "/" + fList.length + ")");
                                System.out.println("Node Thread Running: " + file.getPath());
                                nodes.add(new node(file));
                            }
                            indexNodeRott++;
                        }
                    }
                    rootProgress.setValue(fList.length);
                    new dialog("DOne!", "File sorting completed!");
                    System.out.println("num files total=" + numFiles);
                    System.out.println("num dirs total=" + numDirs);
                }
            };
            thread.start();

        }
    }

    public void selectInputFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Select input dir");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            rootPath = chooser.getSelectedFile();
            inputDir.setText("Input dir:" + rootPath.getPath());
        }
    }

    public void selectOutput() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Select output dir");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            newPath = chooser.getSelectedFile();
            outputDir.setText("Input dir:" + newPath.getPath());
        }
    }

    public void process() {
        if (newPath != null && rootPath != null) {
            status.setText("Initializing...");
            inputBtn.enabled = false;
            inputBtn.enabled = false;
            outputBtn.enabled = false;
            processBtn.enabled = false;
            exitButton.enabled = false;
            rootNodes.add(new rootNode(rootPath));

        } else {
            new dialog("Error", "Please select both input and output dirs first!");
        }

    }
    
    public void writeLogFile(StringBuilder builder){
        String toString = builder.toString();
        //output file stream
        //TODO: write log to file
    }

    private class button extends JPanel implements MouseListener {

        private final String action;
        public boolean enabled = true;
        private final gui gui;

        public button(String t, String a, int w, int h, gui g) {

            action = a;
            JLabel l = new JLabel(t);
            l.setForeground(text);
            add(l);
            gui = g;
            this.setPreferredSize(new Dimension(w, h));
            addMouseListener(this);
            this.setBackground(secondary);

        }

        @Override
        public void mouseClicked(MouseEvent me) {
            if (enabled) {
                if (action.equals("selectIn")) {
                    selectInputFolder();
                } else if (action.equals("selectOut")) {
                    selectOutput();
                } else if (action.equals("process")) {
                    process();
                    enabled = false;
                } else if (action.equals("exit")) {
                    gui.dispose();
                    System.exit(0);
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent me) {

        }

        @Override
        public void mouseReleased(MouseEvent me) {

        }

        @Override
        public void mouseEntered(MouseEvent me) {
            this.setBackground(primary);
        }

        @Override
        public void mouseExited(MouseEvent me) {
            this.setBackground(secondary);
        }

    }

}
