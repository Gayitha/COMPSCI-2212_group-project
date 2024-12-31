package view;

import utils.FileUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

public class UserDictionary extends JFrame{
    private JTable table;
    private DefaultTableModel model;
    public UserDictionary() {
        super("User Dictionary");
        setPreferredSize(new Dimension(500, 500));
        setLayout(new BorderLayout());
        model = new DefaultTableModel();
        model.addColumn("Words");

        table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        Set<String> words = null;
        try {
            words = FileUtils.readWords();
            words.forEach(s -> model.addRow(new Object[]{s}));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error reading user dictionary");
        }


        JButton addButton = new JButton("Add");
        JButton deleteButton = new JButton("Delete");
        JButton updateButton = new JButton("Update");
        JButton saveButton = new JButton("Save");
        JTextField wordTextField = new JTextField(10);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(wordTextField);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String word = wordTextField.getText();
                if (!word.isEmpty()) {
                    model.addRow(new Object[]{word});
                    wordTextField.setText("");
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    model.removeRow(selectedRow);
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String word = wordTextField.getText();
                    if (!word.isEmpty()) {
                        model.setValueAt(word, selectedRow, 0);
                        wordTextField.setText("");
                    }
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Set<String> words = new HashSet<>();
                for (int i = 0; i < model.getRowCount(); i++) {
                    words.add((String) model.getValueAt(i, 0));
                }
                try {
                    FileUtils.write(words);
                    JOptionPane.showMessageDialog(null, "Saved successfully");
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(null, "Error saving user dictionary");
                }
            }
        });
        setLocationRelativeTo(null);
        pack();
        setVisible(true);

    }



}
