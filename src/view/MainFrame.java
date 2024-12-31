package view;

import utils.CheckUtils;
import utils.ErrorInfo;
import utils.FileUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainFrame extends JFrame implements ActionListener {
    JMenuItem open, save, exit, info;
    JLabel errorTypeLabel = new JLabel("");
    JTextField errorLabel = new JTextField(10);
    JTextField manuallyInput = new JTextField(10);
    JButton correction = new JButton("Correction");
    JComboBox<String> suggestion = new JComboBox<>();
    JButton ignore = new JButton("Ignore");
    JButton delete = new JButton("Delete");
    JButton add = new JButton("Add to user dictionary");
    JButton setting = new JButton("User dictionary");
    JTextPane textArea = new JTextPane();
    private int charCount = 0;
    private int lineCount = 0;
    private int wordCount = 0;
    private int misspelledWordCount = 0;
    private int misCapitalizationCount = 0;
    private int doubleWordCount = 0;
    private int manualWordCount = 0;
    private int wordSuggestionsCount = 0;
    private int wordIgnoreCount = 0;
    private int wordDeleteCount = 0;


    private List<ErrorInfo> allErrors;
    private int prevOffset = 0;

    private int currentErrorIndex = 0;
    private Set<String> userDictionary;
    private boolean hasSave = true;

    public MainFrame() {

        super("Spell Checker");
        setSize(500, 700);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        open = new JMenuItem("Open");
        save = new JMenuItem("Save");
        exit = new JMenuItem("Exit");
        file.add(open);
        file.add(save);
        file.add(exit);

        JMenu infos = new JMenu("Info");
        info = new JMenuItem("Report");
        infos.add(info);

        menuBar.add(file);
        menuBar.add(infos);
        setJMenuBar(menuBar);

        JPanel top = initTop();
        add(top, BorderLayout.NORTH);
        JPanel center = initCenter();
        add(center, BorderLayout.CENTER);

        try {
            userDictionary = FileUtils.readWords();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        open.addActionListener(e -> {
            if (!hasSave) {
                int result = JOptionPane.showConfirmDialog(null, "Do you want to save the file?");
                if (result == JOptionPane.YES_OPTION) {
                    save();
                }
            }

            JFileChooser fileChooser = new JFileChooser("./");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.showOpenDialog(null);
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            textArea.setText("");
            currentErrorIndex = 0;
            charCount = 0;
            lineCount = 0;
            wordCount = 0;
            misspelledWordCount = 0;
            misCapitalizationCount = 0;
            doubleWordCount = 0;
            manualWordCount = 0;
            wordSuggestionsCount = 0;
            wordIgnoreCount = 0;
            wordDeleteCount = 0;
            prevOffset = 0;
            startCheck(path);
        });
        save.addActionListener(e -> {
            save();
        });
        info.addActionListener(e -> showReport());

        correction.addActionListener(this);
        ignore.addActionListener(this);
        delete.addActionListener(this);
        add.addActionListener(this);
        setting.addActionListener(this);


        correction.setEnabled(false);
        ignore.setEnabled(false);
        delete.setEnabled(false);
        add.setEnabled(false);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (!hasSave) {
                    int result = JOptionPane.showConfirmDialog(null, "Do you want to save the file?");
                    if (result == JOptionPane.YES_OPTION) {
                        save();
                    }
                }
            }

        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public JPanel initTop() {
        JPanel top = new JPanel();
        top.setLayout(new BorderLayout());
        top.setPreferredSize(new Dimension(500, 200));

        JPanel topWest = new JPanel();
        topWest.setPreferredSize(new Dimension(200, 150));
        Border border = BorderFactory.createTitledBorder("Information");
        topWest.setBorder(border);
        topWest.setLayout(new GridLayout(3, 1));
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p0 = new JPanel();
        p1.add(new JLabel("Error type:"));
        p1.add(errorTypeLabel);
        p2.add(new JLabel("Error:"));
        p2.add(errorLabel);
        p0.add(setting);
        topWest.add(p1);
        topWest.add(p2);
        topWest.add(p0);
        errorLabel.setEditable(false);

        JPanel topCenter = new JPanel();
        topCenter.setPreferredSize(new Dimension(250, 200));
        Border border1 = BorderFactory.createTitledBorder("Operation");
        topCenter.setBorder(border1);
        topCenter.setLayout(new GridLayout(4, 1));
        JPanel p3 = new JPanel();
        JPanel p4 = new JPanel();
        JPanel p5 = new JPanel();
        JPanel p6 = new JPanel();
        p3.add(new JLabel("Manual input:"));
        p3.add(manuallyInput);
        p4.add(suggestion);
        suggestion.setPreferredSize(new Dimension(150, 30));
        p4.add(correction);
        p5.add(ignore);
        p5.add(delete);
        p6.add(add);
        topCenter.add(p3);
        topCenter.add(p4);
        topCenter.add(p5);
        topCenter.add(p6);

        top.add(topWest, BorderLayout.WEST);
        top.add(topCenter, BorderLayout.CENTER);
        return top;
    }

    public JPanel initCenter() {
        JPanel center = new JPanel();
        center.setLayout(new BorderLayout());
        center.setPreferredSize(new Dimension(500, 500));
        JPanel centerP = new JPanel();
        Border border = BorderFactory.createTitledBorder("Text");
        centerP.setBorder(border);
        centerP.setPreferredSize(new Dimension(450, 450));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 400));
        centerP.add(scrollPane);
        center.add(centerP, BorderLayout.CENTER);

        return center;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == info) {
            showReport();
        }
        if (e.getSource() == correction) {
            hasSave = false;
            if (manuallyInput.getText().isEmpty() && suggestion.getSelectedItem() != null) {
                String suggestion = (String) this.suggestion.getSelectedItem();
                ErrorInfo errorInfo = allErrors.get(currentErrorIndex);
                StyledDocument styledDocument = textArea.getStyledDocument();
                SimpleAttributeSet attrSet = new SimpleAttributeSet();
                StyleConstants.setBackground(attrSet, Color.GREEN);
                try {
                    styledDocument.remove(errorInfo.getStartIndex() + prevOffset, errorInfo.getEndIndex() - errorInfo.getStartIndex());
                    styledDocument.insertString(errorInfo.getStartIndex() + prevOffset, suggestion, attrSet);
                    prevOffset += suggestion.length() - errorInfo.getErrorText().length();
                } catch (BadLocationException badLocationException) {
                    badLocationException.printStackTrace();
                }
                wordSuggestionsCount++;
            } else if (!manuallyInput.getText().isEmpty()) {
                ErrorInfo errorInfo = allErrors.get(currentErrorIndex);
                StyledDocument styledDocument = textArea.getStyledDocument();
                SimpleAttributeSet attrSet = new SimpleAttributeSet();
                StyleConstants.setBackground(attrSet, Color.GREEN);
                try {
                    styledDocument.remove(errorInfo.getStartIndex() + prevOffset, errorInfo.getEndIndex() - errorInfo.getStartIndex());
                    styledDocument.insertString(errorInfo.getStartIndex() + prevOffset, manuallyInput.getText(), attrSet);
                    prevOffset += manuallyInput.getText().length() - errorInfo.getErrorText().length();
                } catch (BadLocationException badLocationException) {
                    badLocationException.printStackTrace();
                }
                manualWordCount++;
            } else {
                JOptionPane.showMessageDialog(null, "Please input a word or choose a suggestion");
            }
            currentErrorIndex++;
            if (currentErrorIndex < allErrors.size()) {
                startCorrectionError();
            } else {
                showReport();
                correction.setEnabled(false);
                ignore.setEnabled(false);
                delete.setEnabled(false);
                add.setEnabled(false);
            }
        }

        if (e.getSource() == ignore) {
            hasSave = false;
            ErrorInfo errorInfo = allErrors.get(currentErrorIndex);
            StyledDocument styledDocument = textArea.getStyledDocument();
            SimpleAttributeSet attrSet = new SimpleAttributeSet();
            StyleConstants.setBackground(attrSet, Color.lightGray);
            try {
                styledDocument.remove(errorInfo.getStartIndex() + prevOffset, errorInfo.getEndIndex() - errorInfo.getStartIndex());
                styledDocument.insertString(errorInfo.getStartIndex() + prevOffset, errorInfo.getErrorText(), attrSet);
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
            currentErrorIndex++;
            if (currentErrorIndex < allErrors.size()) {
                startCorrectionError();
            } else {
                showReport();
                correction.setEnabled(false);
                ignore.setEnabled(false);
                delete.setEnabled(false);
                add.setEnabled(false);
            }
            wordIgnoreCount++;
        }

        if (e.getSource() == delete) {
            hasSave = false;
            ErrorInfo errorInfo = allErrors.get(currentErrorIndex);
            StyledDocument styledDocument = textArea.getStyledDocument();
            SimpleAttributeSet attrSet = new SimpleAttributeSet();
            StyleConstants.setBackground(attrSet, Color.GREEN);
            try {
                if ("Double word".equals(errorInfo.getErrorType())) {
                    String text = CheckUtils.deleteDoubleWord(errorInfo.getErrorText());
                    styledDocument.remove(errorInfo.getStartIndex() + prevOffset, errorInfo.getEndIndex() - errorInfo.getStartIndex());
                    styledDocument.insertString(errorInfo.getStartIndex() + prevOffset, text, attrSet);
                    prevOffset += text.length() - errorInfo.getErrorText().length();

                } else {
                    styledDocument.remove(errorInfo.getStartIndex() + prevOffset, errorInfo.getEndIndex() - errorInfo.getStartIndex());
                    prevOffset -= errorInfo.getEndIndex() - errorInfo.getStartIndex();
                }
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
            currentErrorIndex++;
            if (currentErrorIndex < allErrors.size()) {
                startCorrectionError();
            } else {
                showReport();
                correction.setEnabled(false);
                ignore.setEnabled(false);
                delete.setEnabled(false);
                add.setEnabled(false);
            }
            wordDeleteCount++;
        }

        if (e.getSource() == add) {
            hasSave = false;
            ErrorInfo errorInfo = allErrors.get(currentErrorIndex);
            StyledDocument styledDocument = textArea.getStyledDocument();
            SimpleAttributeSet attrSet = new SimpleAttributeSet();
            StyleConstants.setBackground(attrSet, Color.WHITE);
            try {
                styledDocument.remove(errorInfo.getStartIndex() + prevOffset, errorInfo.getEndIndex() - errorInfo.getStartIndex());
                styledDocument.insertString(errorInfo.getStartIndex() + prevOffset, errorInfo.getErrorText(), attrSet);
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
            currentErrorIndex++;
            if (currentErrorIndex < allErrors.size()) {
                startCorrectionError();
            } else {
                showReport();
                correction.setEnabled(false);
                ignore.setEnabled(false);
                delete.setEnabled(false);
                add.setEnabled(false);
            }
            userDictionary.add(errorInfo.getErrorText());
            try {
                FileUtils.write(userDictionary);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(null, exception.getMessage());
            }
        }

        if (e.getSource() == setting) {
            settingUserDictionary();
        }
        System.out.println(prevOffset);
    }

    private void showReport() {
        JOptionPane.showMessageDialog(null, "Character count: " + charCount + "\n" +
                "Line count: " + lineCount + "\n" +
                "Word count: " + wordCount + "\n" +
                "Misspelled word count: " + misspelledWordCount + "\n" +
                "Mis-capitalization count: " + misCapitalizationCount + "\n" +
                "Double word count: " + doubleWordCount + "\n" +
                "Manually input word count: " + manualWordCount + "\n" +
                "Word suggestions count: " + wordSuggestionsCount + "\n" +
                "Word ignore count: " + wordIgnoreCount + "\n" +
                "Word delete count: " + wordDeleteCount + "\n");
    }

    private void startCheck(String filepath) {
        correction.setEnabled(true);
        ignore.setEnabled(true);
        delete.setEnabled(true);
        add.setEnabled(true);

        allErrors = new ArrayList<>();
        prevOffset = 0;
        try {
            CheckUtils.initWordData();
            String text = FileUtils.read(filepath);
            text = CheckUtils.removeLabel(text);
            charCount = text.length();
            Document document = textArea.getDocument();
            textArea.setEditable(false);
            String[] sentence = text.split("([.!?\"]+\\s*)");
            Pattern pattern = Pattern.compile("([.!?\"]+\\s*)");
            Matcher matcher = pattern.matcher(text);

            lineCount = text.split("\\n").length;
            int start = 0;
            for (String s : sentence) {

                if (s.isEmpty()) {
                    if (matcher.find()) {
                        start += matcher.group().length();
                    }

                    continue;
                }
                System.out.println(s);

                String[] words = s.split(CheckUtils.REGEX);

                if (!CheckUtils.checkUpperHead(words[0])) {
                    allErrors.add(new ErrorInfo(start, start + words[0].length(), words[0], "Upper head"));
                    misCapitalizationCount++;
                }

                Pattern pattern1 = Pattern.compile(CheckUtils.REGEX);
                Matcher matcher1 = pattern1.matcher(s);
                words = s.split(CheckUtils.REGEX);
                for (int i = 0; i < words.length; i++) {
                    String word = words[i];
                    if (word.isEmpty()) {
                        continue;
                    }

                    if (i!=0&&!CheckUtils.checkMixUpper(word)) {
                        allErrors.add(new ErrorInfo(start, start + word.length(), word, "Mix upper"));
                        misCapitalizationCount++;
                    }
                    if (!CheckUtils.check(word)) {
                        allErrors.add(new ErrorInfo(start, start + word.length(), word, "Misspelled word"));
                        misspelledWordCount++;
                    }
                    wordCount++;
                    if (matcher1.find()) {
                        String group = matcher1.group();
                        if (i < words.length - 1) {
                            String nextWord = words[i + 1];
                            String str = word + group + nextWord;
                            if (!CheckUtils.checkDoubleWord(str)) {
                                allErrors.add(new ErrorInfo(start, start + str.length(), str, "Double word"));
                                doubleWordCount++;
                            }
                        }
                        start += group.length();
                    }
                    start += word.length();
                }
                if (matcher.find()) {
                    start += matcher.group().length();

                }
            }


            document.insertString(0, text, null);

            StyledDocument styledDocument = textArea.getStyledDocument();
            for (ErrorInfo allError : allErrors) {
                SimpleAttributeSet attrSet = new SimpleAttributeSet();
                StyleConstants.setBackground(attrSet, Color.RED);
                styledDocument.setCharacterAttributes(allError.getStartIndex(), allError.getEndIndex() - allError.getStartIndex(), attrSet, true);
            }
            startCorrectionError();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

    }

    private void startCorrectionError() {

        ErrorInfo errorInfo = allErrors.get(currentErrorIndex);
        String errorText = errorInfo.getErrorText();
        errorTypeLabel.setText(errorInfo.getErrorType());
        errorLabel.setText(errorText);

        StyledDocument styledDocument = textArea.getStyledDocument();
        SimpleAttributeSet attrSet = new SimpleAttributeSet();
        StyleConstants.setBackground(attrSet, Color.YELLOW);
        styledDocument.setCharacterAttributes(errorInfo.getStartIndex() + prevOffset, errorInfo.getEndIndex() - errorInfo.getStartIndex(), attrSet, true);

        if ("Misspelled word".equals(errorInfo.getErrorType())) {
            String[] suggestions = CheckUtils.getAllPossibleWords(errorText.toLowerCase()).toArray(new String[0]);
            suggestion.removeAllItems();
            suggestion.addItem(null);
            for (String suggestion : suggestions) {
                this.suggestion.addItem(suggestion);
            }
        }
        if ("Upper head".equals(errorInfo.getErrorType())) {
            String[] suggestions = CheckUtils.correctionUpperHead(errorText).toArray(new String[0]);
            suggestion.removeAllItems();
            suggestion.addItem(null);
            for (String suggestion : suggestions) {
                this.suggestion.addItem(suggestion);
            }
        }
        if ("Mix upper".equals(errorInfo.getErrorType())) {
            String[] suggestions = CheckUtils.correctionMixUpper(errorText).toArray(new String[0]);
            suggestion.removeAllItems();
            suggestion.addItem(null);
            for (String suggestion : suggestions) {
                this.suggestion.addItem(suggestion);
            }
        }
        if ("Double word".equals(errorInfo.getErrorType())) {
            suggestion.removeAllItems();
        }
    }

    private void save() {
        JFileChooser fileChooser = new JFileChooser("./");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.showSaveDialog(null);
        String path = fileChooser.getSelectedFile().getAbsolutePath();
        try {
            FileUtils.write(path, textArea.getText());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        hasSave = true;
    }

    public static void settingUserDictionary() {
        new UserDictionary();
    }


}
