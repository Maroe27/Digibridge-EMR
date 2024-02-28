import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JScrollPaneSearch extends JFrame {
    private JList<String> nameList;
    private DefaultListModel<String> listModel;

    public JScrollPaneSearch() {
        setTitle("ScrollPane Search");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);

        // Sample data for the JList
        String[] data = {"John", "Jane", "Doe", "Alice", "Bob", "Charlie", "David", "Eve"};

        // Initialize list model
        listModel = new DefaultListModel<>();
        for (String name : data) {
            listModel.addElement(name);
        }
        nameList = new JList<>(listModel);

        // Create a JScrollPane and add the JList to it
        JScrollPane scrollPane = new JScrollPane(nameList);

        // Create a JTextField for searching
        JTextField searchField = new JTextField();
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterList();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterList();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterList();
            }
        });

        // Add components to the JFrame
        add(searchField, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void filterList() {
        String searchText = ((JTextField) getContentPane().getComponent(0)).getText();
        List<String> filteredNames = new ArrayList<>();
        for (int i = 0; i < listModel.getSize(); i++) {
            String name = listModel.getElementAt(i);
            if (name.toLowerCase().contains(searchText.toLowerCase())) {
                filteredNames.add(name);
            }
        }
        nameList.setListData(filteredNames.toArray(new String[0]));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JScrollPaneSearch().setVisible(true);
            }
        });
    }
}
