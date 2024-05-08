import Mailer.SendMailSSL;
import Models.Config;
import Models.Employee;
import Models.PayrollDetail;
import org.json.JSONArray;
import org.json.JSONObject;
import PDFGenerator.PDFGenerator;
import javax.mail.MessagingException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

import Models.Payroll;

public class UIUtils extends SystemUpc {
    /**
     * Este es el constructor de clase, cuando se instancia un objeto de esta clase
     * se llama a esto
     *
     * @param empJsonUrl -La ruta donde estara ubicado el archivo json con la
     *                   informacion de los empleados
     * @param config
     */
    private static Config configSettings;
    private String empJsonUrl;
    public UIUtils(String empJsonUrl, Config config) {
        this.configSettings = config;
        this.empJsonUrl = empJsonUrl;
    }
    /**
     * Este es el constructor de clase, cuando se instancia un objeto de esta clase
     * se llama a esto
     *
     * @param empJsonUrl -La ruta donde estara ubicado el archivo json con la
     *                   informacion de los empleados
     */

    /**
     * Este metodo se encarga de crear un modal y añadirle los componentes que se le pasen
     * de forma vertical uno debajo de otro
     * @param components -Los componentes que se mostraran en el modal
     * @return JDialog -El modal creado
     * Uso:
     * JDialog dialog=createModal(new JLabel("Label 1"), new JTextField("Text 1"), new JLabel("Label 2"), new JTextField("Text 2"));
     * dialog.setVisible(true);
     *
     */
    protected static JDialog createModal(JComponent ...components){
        JDialog dialog = new JDialog();
        dialog.setSize(300,300);
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
        for (JComponent component : components) {
            dialog.add(component);
        }
        return dialog;
    }
    /**
     * Este metodo se encarga de crear un combobox y añadirlo a un panel
     * @param labelText -El texto que se mostrara en el label
     * @param items -Los items que se mostraran en el combobox
     * @param tab -El panel al que se añadira el combobox
     * @param jsonData -Los datos que se mostraran en la tabla
     * @param filterColumns -Los filtros que se aplicaran a la tabla
     * @param filterColumn -La columna por la que se filtrara
     * @param filterFunction -La funcion que se aplicara al filtrar
     * @param empScrollPane -El scrollpane que contendra la tabla
     * @return JPanel -El panel que contiene el combobox
     * Uso:
     * createComboBox("Label", new String[]{"Item 1", "Item 2"}, panel, jsonData, filterColumns, "Columna", filterFunction, empScrollPane);
     */
    protected static JPanel createComboBox(String labelText, String[] items, JComponent tab, JSONArray jsonData, TableFilter[] filterColumns, String filterColumn, FilterInterface filterFunction,JScrollPane empScrollPane,String tabName) {
        JPanel panel = new JPanel();
        JComboBox<String> comboBox = new JComboBox<>(items);
        JLabel label = new JLabel(labelText);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(label);
        panel.add(comboBox);

        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox<?> cb = (JComboBox<?>) e.getSource();
                String selected = (String) cb.getSelectedItem();
                TableFilter filter = new TableFilter(filterColumn, selected);
                filterFunction.applyFilter(filterColumns, filter);
                getTable(tab, jsonData, filterColumns,empScrollPane,tabName);
            }
        });

        tab.add(panel);
        return panel;
    }
    /**
     * Este metodo se encarga de crear una tabla y añadirla a un panel
     * @param tab -El panel al que se añadira la tabla
     * @param data -Los datos que se mostraran en la tabla
     * @param filter -Los filtros que se aplicaran a la tabla
     * @param scrollPane -El scrollpane que contendra la tabla
     * Uso:
     * getTable(panel, jsonData, filterColumns, empScrollPane);
     */
    protected static void getTable(JComponent tab, JSONArray data, TableFilter[] filter, JScrollPane scrollPane,String tabName) {
        JTable table = new JTable();
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setModel(model);
        System.out.println(tabName);
        try {
            if (!data.isEmpty()) { // Verificar si data tiene elementos
                // Procesar la primera fila para determinar las columnas
                JSONObject firstObject = data.getJSONObject(0);
                for (String key : firstObject.keySet()) {
                    Object value = firstObject.get(key);
                    if (value instanceof JSONArray && key.equals("payrollDetailConcepts")) {
                        JSONArray jsonArray = (JSONArray) value;
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject concept = jsonArray.getJSONObject(j);
                            String conceptName = concept.getString("conceptName");
                            model.addColumn(conceptName); // Agregar la columna con el nombre del concepto
                        }
                    } else if (value instanceof JSONObject) {
                        JSONObject nestedObject = (JSONObject) value;
                        for (String nestedKey : nestedObject.keySet()) {
                            model.addColumn(nestedKey); // Agregar columna por cada clave en el objeto JSON
                        }
                    } else {
                        model.addColumn(key); // Agregar la columna con el nombre de la clave
                    }
                }

                // Agregar filas a la tabla
                for (int i = 0; i < data.length(); i++) {
                    JSONObject rowData = data.getJSONObject(i);
                    Object[] row = new Object[model.getColumnCount()];
                    int index = 0;
                    for (String key : rowData.keySet()) {
                        Object value = rowData.get(key);
                        if (value instanceof JSONArray && key.equals("payrollDetailConcepts")) {
                            JSONArray jsonArray = (JSONArray) value;
                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject concept = jsonArray.getJSONObject(j);
                                String conceptName = concept.getString("conceptName");
                                double conceptAmount = concept.getDouble("conceptAmount");
                                row[model.findColumn(conceptName)] = conceptAmount; // Asignar el valor a la columna correspondiente
                            }
                        } else if (value instanceof JSONObject) {
                            JSONObject nestedObject = (JSONObject) value;
                            for (String nestedKey : nestedObject.keySet()) {
                                row[model.findColumn(nestedKey)] = nestedObject.get(nestedKey); // Asignar el valor a la columna correspondiente
                            }
                        } else {
                            row[model.findColumn(key)] = value; // Asignar el valor a la columna correspondiente
                        }
                    }
                    model.addRow(row);
                }
               if(tabName.equals("emp")) {
                   JPopupMenu popupMenu = new JPopupMenu();
                   JMenuItem downloadPDFItem = new JMenuItem("Generar PDF");
                   popupMenu.add(downloadPDFItem);
                    table.addMouseListener(new MouseAdapter() {
                         public void mousePressed(MouseEvent e) {
                             if (SwingUtilities.isRightMouseButton(e)) {
                                 Point point = e.getPoint();
                                 int row = table.rowAtPoint(point);
                                 if (row >= 0 && row < table.getRowCount()) {
                                     table.setRowSelectionInterval(row, row);
                                     //get row index
                                     int rowIndex = table.getSelectedRow();
                                     String employeeId = table.getValueAt(rowIndex, 1   ).toString();
                                        downloadPDFItem.addActionListener(new ActionListener() {
                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                Employee employee = getEmployeeByEmpCode(employeeId);
                                                Payroll payroll = new Payroll(employee,configSettings , "2024-07-01");
                                                PayrollDetail payrollDetail = new PayrollDetail(employee,configSettings , "2024-07-01",payroll.getPayrollConcepts());
                                                try {
                                                    payrollDetail.generatePayrollDetailsJson();
                                                    //SHOW MESSAGE
                                                    JOptionPane.showMessageDialog(null, "PDF generado con exito");

                                                } catch (IOException ex) {
                                                    throw new RuntimeException(ex);
                                                }
                                                //generate pdf with employee details
                                            }
                                        });
                                 }
                                 popupMenu.show(table, e.getX(), e.getY());

                             }
                         }
                        });


               }
               else if(tabName.equals("pay")){
                   JPopupMenu popupMenu = new JPopupMenu();
                   JMenuItem downloadPDFItem = new JMenuItem("Descargar PDF");
                   JMenuItem sendEmailItem = new JMenuItem("Enviar por Correo");
                   popupMenu.add(downloadPDFItem);
                   popupMenu.add(sendEmailItem);
                   table.addMouseListener(new MouseAdapter() {
                       public void mousePressed(MouseEvent e) {
                           if (SwingUtilities.isRightMouseButton(e)) {
                               Point point = e.getPoint();
                               int row = table.rowAtPoint(point);
                               if (row >= 0 && row < table.getRowCount()) {
                                   table.setRowSelectionInterval(row, row);
                                   //get row inde 8
                                   int rowIndex = table.getSelectedRow();
                                   String payrollId = table.getValueAt(rowIndex, 9).toString();
                                      downloadPDFItem.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                             //generate pdf with payroll details
                                            PayrollDetail payroll = new PayrollDetail();
                                             PDFGenerator pdfGenerator = new PDFGenerator("src/main/java/Templates/payroll-template.html",
                                                     "src/main/java/payroll.pdf",
                                                     payroll.getPayrollDetailJsonWithId(Integer.parseInt(payrollId)),
                                                     "smtp.gmail.com",
                                                     "485",
                                                     "harukakasugano31@gmail.com",
                                                     "moimwcdzokheodrz");
                                            try {
                                                pdfGenerator.generateAndSavePDF();

                                            } catch (IOException ex) {
                                                throw new RuntimeException(ex);
                                            }
                                        }
                                      });
                                      sendEmailItem.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                             //send email with pdf attached
                                             PayrollDetail payroll = new PayrollDetail();
                                             PDFGenerator pdfGenerator = new PDFGenerator("src/main/java/Templates/payroll-template.html",
                                                     "src/main/java/payroll.pdf",
                                                     payroll.getPayrollDetailJsonWithId(Integer.parseInt(payrollId))
                                                        , "smtp.gmail.com",
                                                        "485",
                                                        "harukakasugano31@gmail.com",
                                                        "moimwcdzokheodrz");

                                             try {
                                                  pdfGenerator.generateAndSendPDF();

                                             } catch (IOException ex) {
                                                  throw new RuntimeException(ex);
                                             } catch (MessagingException ex) {
                                                 throw new RuntimeException(ex);
                                             }

                                        }});
                                   popupMenu.show(table, e.getX(), e.getY());
                               }
                           }
                       }
                   });
               }
                //scroll pane with horizontal and vertical scroll bars
                scrollPane.setViewportView(table);

                table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Desactivar el ajuste automático de tamaño de las columnas
                table.setAutoCreateRowSorter(true); // Activar la ordenación de filas
                tab.add(scrollPane);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Employee getEmployeeByEmpCode(String id){
        try(FileReader reader = new FileReader("src/main/java/employees.json")) {
            int character;
            String employeesString = "";
            while ((character = reader.read()) != -1) {
                employeesString += (char) character;
            }
            JSONArray employees = new JSONArray(employeesString);
            for (int i = 0; i < employees.length(); i++) {
                JSONObject employee = employees.getJSONObject(i);
                if (Objects.equals(employee.getString("empCode"), id)){
                    return new Employee(
                            employee.getString("empLastName"),
                            employee.getString("empCode"),
                            employee.getString("empName"),
                            employee.getString("empSalary"),
                            employee.getString("empEmail"),
                            employee.getInt("empAffiliation"),
                            employee.getString("dateStart"),
                            employee.getString("empRole")
                    );
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Si no se encuentra el empleado, devolver null
        return null;
    }

}
