import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Arrays;

public class GUI extends JFrame {
    JPanel jpan;
    JLabel numberOfCoders;
    JTextField codersNumberField;
    JLabel sequenceAmountLabel;
    JTextField sequenceAmountField;
    JLabel errorsIntensity;
    JTextField errorsNumberField;
    JTextArea resultsField;
    JPanel channelWire;
    JPanel splitLine;
    JButton startSimulation;
    JComboBox<String>[] coders;
    JLabel priorityLabel;
    JComboBox priority;
    Program program;


    public GUI() {
        setLayout(null);

        channelWire = new JPanel();
        channelWire.setBounds(50, 180, 780, 2);
        channelWire.setBackground(Color.BLACK);

        splitLine = new JPanel();
        splitLine.setBounds(15, 305, 850, 2);
        splitLine.setBackground(Color.lightGray);

        jpan = new JPanel();
        jpan.setBounds(50, 80, 350, 200);
        jpan.setOpaque(true);
        jpan.setBackground(Color.lightGray);
        jpan.setBorder(BorderFactory.createMatteBorder(
                1, 1, 1, 1, Color.darkGray));

        numberOfCoders = new JLabel("Число кодеков: ");
        numberOfCoders.setBounds(50, 30, 150, 30);
        numberOfCoders.setFont(new Font("Dialog", Font.BOLD, 15));

        codersNumberField = new JTextField();
        codersNumberField.setBounds(171, 30, 50, 30);
        codersNumberField.setFont(new Font("Dialog", Font.BOLD, 17));
        codersNumberField.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                addComp();
            }
        });

        sequenceAmountLabel = new JLabel("Число потоков: ");
        sequenceAmountLabel.setBounds(250, 30, 150, 30);
        sequenceAmountLabel.setFont(new Font("Dialog", Font.BOLD, 15));

        sequenceAmountField = new JTextField();
        sequenceAmountField.setBounds(369, 30, 50, 30);
        sequenceAmountField.setFont(new Font("Dialog", Font.BOLD, 17));

        errorsIntensity = new JLabel("Интенсивность ошибок: ");
        errorsIntensity.setBounds(500, 150, 200, 30);
        errorsIntensity.setFont(new Font("Dialog", Font.BOLD, 15));

        errorsNumberField = new JTextField();
        errorsNumberField.setBounds(680, 165, 50, 30);
        errorsNumberField.setFont(new Font("Dialog", Font.BOLD, 17));

        resultsField = new JTextArea();
        resultsField.setBounds(320, 330, 510, 2000);
        resultsField.setEditable(false);
        resultsField.setBorder(BorderFactory.createMatteBorder(
                1, 1, 1, 1, Color.darkGray));

        priorityLabel = new JLabel("Основной параметр оценки:");
        priorityLabel.setBounds(50, 330, 230, 30);
        priorityLabel.setFont(new Font("Dialog", Font.BOLD, 15));

        priority = new JComboBox(new String[]{"Скорость передачи", "Полнота декодирования"});
        priority.setBounds(50, 360, 220, 30);
        priority.setFont(new Font("Dialog", Font.BOLD, 15));


        startSimulation = new JButton();
        startSimulation.setBounds(680, 230, 150, 50);
        startSimulation.setForeground(Color.gray);
        startSimulation.setBackground(Color.decode("#A8FFA3"));
        startSimulation.setBorder(BorderFactory.createMatteBorder(
                1, 1, 1, 1, Color.darkGray));
        startSimulation.setText("Начать передачу");
        startSimulation.setForeground(Color.darkGray);
        startSimulation.setFont(new Font("Dialog", Font.BOLD, 15));
        startSimulation.setFocusPainted(false);
        startSimulation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int codersNumber = Integer.parseInt(codersNumberField.getText());
                int errorsNumber = Integer.parseInt(errorsNumberField.getText());
                int sequenceAmount = Integer.parseInt(sequenceAmountField.getText());

                if (coders != null && codersNumber != 0 && errorsNumber != 0 && sequenceAmount != 0) {
                    program = new Program(sequenceAmount);
                    String[] codersSet = new String[codersNumber];

                    for (int i = 0; i < codersNumber; i++) {
                        codersSet[i] = String.valueOf(coders[i].getSelectedItem());
                    }

                    if (priority.getSelectedIndex() == 0) {
                        program.setSpeedWeight(0.35);
                        program.setCorrectionWeight(0.45);
                    } else {
                        program.setSpeedWeight(0.1);
                        program.setCorrectionWeight(0.9);
                    }

                    long time = System.currentTimeMillis();
                    program.startTransmission(codersSet, errorsNumber);
                    long resultTime = System.currentTimeMillis() - time;

                    resultsField.append("Комбинация кодеков: ");
                    resultsField.append(String.join(", ", codersSet));
                    resultsField.append("\n");
                    resultsField.append("Эффективность декодирования: " + new DecimalFormat("#0.00").format(program.getCorrectionEffectiveness()) + "\n");
                    resultsField.append("Число дополнительных бит: " + new DecimalFormat("#0").format(program.getExtraBits())   + "\n");
                    resultsField.append("Эффективность цепочки: " + new DecimalFormat("#0.00").format(program.getSequenceEffectiveness()) + "\n");
                    //resultsField.append((resultTime/1000+ "," + resultTime%1000) + "\n");
                    resultsField.append("\n");
                }
            }
        });


        add(jpan);
        add(numberOfCoders);
        add(codersNumberField);
        add(sequenceAmountLabel);
        add(sequenceAmountField);
        add(errorsIntensity);
        add(errorsNumberField);
        add(resultsField);
        add(channelWire);
        add(splitLine);
        add(startSimulation);
        add(priorityLabel);
        add(priority);
        revalidate();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void addComp() {
        String str = codersNumberField.getText();
        String[] codersNames = new String[]{"Hamming", "BCH"};
        jpan.removeAll();

        if (!str.equals("")) {
            int num = Integer.parseInt(str);
            coders = new JComboBox[num];
            for (int i = 0; i < num; i++) {
                //jLabels[i] = new JLabel("New Label");
                coders[i] = new JComboBox<>(codersNames);
                jpan.add(coders[i]);
            }
        }
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        GUI gui = new GUI();
        gui.setSize(900, 600);
        gui.setVisible(true);


    }
}
