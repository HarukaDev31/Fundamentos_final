import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import Models.Config;
import org.apache.poi.ss.usermodel.*;
import java.io.FileNotFoundException;
import org.json.*;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Este metodo es usado para la inicializacion de la GUI.
 * Si deseas agregar nuevos componentes se tienen que agregar en esta clase
 */
public class SystemUpc {
    private JFrame mainContainer;
    private JPanel itemContainer,emptabNomAp,emptabempCode,emptabAfi;
    private JMenuBar menuBar;
    private JMenu file,config;
    private JMenuItem fmi1,cmi1;
    private JTabbedPane tabbedPane;
    private JComponent empTab, payrollTab;
    private String empJsonUrl;
    private TableFilter[] employeeFilters;
    private TableFilter[] payrollFilters;
    private UIUtils utils;
    private JScrollPane empScrollPane,payrollScrollPane;
    public static Config configSettings;
    /*
     * Este es el constructor de clase, cuando se instancia un objeto de esta clase
     * se llama a esto
     *
     * @param empJsonUrl -La ruta donde estara ubicado el archivo json con la
     *                   informacion de los empleados
     */
    public SystemUpc(String empJsonUrl,Config config) {
        this.empJsonUrl = empJsonUrl;
        this.payrollFilters = new TableFilter[]{
                new TableFilter("empName", null),
                new TableFilter("empCode", null),
                new TableFilter("empAffiliation", null)};
        this.employeeFilters = new TableFilter[]{
                new TableFilter("empName", null),
                new TableFilter("empCode", null),
                new TableFilter("empAffiliation", null)};
        this.configSettings = config;
        this.utils = new UIUtils(this.empJsonUrl, this.configSettings);
        initSystem();
    }
    /**
     * Este metodo se encarga de inicializar la GUI y sus componentes
     * Ademas de inicializar los eventos de los componentes
     * y crear el json de empleados
     */
    public void initSystem() {
        initFile(empJsonUrl);
        initBase();
        initMenu();
        initTabbedPane();
        this.mainContainer.setVisible(true);

    }
    /**
     * Este metodo se encarga de crear el contenedor principal y un panel donde iran
     * todos nuestros componentes
     */
    public void initBase() {
        // init main container
        this.mainContainer = new JFrame("System Upc");
        this.mainContainer.setSize(800, 600);
        this.mainContainer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // init item container
        this.itemContainer = new JPanel();
        this.itemContainer.setSize(500, 500);
        this.itemContainer.setLayout(new BorderLayout());
        this.mainContainer.add(this.itemContainer);
        this.empScrollPane = new JScrollPane();
        this.empScrollPane.setSize(500, 500);
        this.empScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.empScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.payrollScrollPane = new JScrollPane();
        this.payrollScrollPane.setSize(500, 400);
        this.payrollScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.payrollScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }
    /**
     * Este metodo se encarga de inicializar el menu y sus submenus
     */
    public void initMenu() {
        // Create MenuBar
        this.menuBar = new JMenuBar();
        // Create Menu called File
        /// Crear Menu,añadir submenu,agregar funcionalidad
        this.file = new JMenu("File");
        this.fmi1 = new JMenuItem("Importar Empleados");
        this.file.add(this.fmi1);
        this.fmi1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                JDialog dialog=UIUtils.createModal(new JLabel("Label 1"), new JTextField("Text 1"), new JLabel("Label 2"), new JTextField("Text 2"));
//                dialog.setVisible(true);
                    openFileSelector();
            }
        });
        // Añadir menu a la barra de menu
        this.menuBar.add(this.file);
        this.mainContainer.setJMenuBar(this.menuBar);
        this.mainContainer.setVisible(true);

    }
    /**
     * Este metodo se encarga de inicializar el tabbedPane y sus tabs
     */
    private void initTabbedPane() {
        this.tabbedPane = new JTabbedPane();
        this.tabbedPane.setTabPlacement(JTabbedPane.TOP);
        this.empTab = createTab("emp.png", "Empleados");
        this.payrollTab = createTab("emp.png", "Boletas");
        this.itemContainer.add(this.tabbedPane);
//        this.emptabNomAp = UIUtils.createComboBox("Filtrar por Nombre o Apellido:", getDataFilter(this.empJsonUrl, "empName"), this.empTab, getJsonData(this.empJsonUrl), this.employeeFilters, "empName", this::filterFunction,this.empScrollPane,"emp");
//        this.emptabempCode = UIUtils.createComboBox("Filtrar por Codigo de Empleado:", getDataFilter(this.empJsonUrl, "empCode"), this.empTab, getJsonData(this.empJsonUrl), this.employeeFilters, "empCode", this::filterFunction,this.empScrollPane,"emp");
//        this.emptabAfi = UIUtils.createComboBox("Filtrar por Afiliacion:", getDataFilter(this.empJsonUrl, "empAffiliation"), this.empTab, getJsonData(this.empJsonUrl), this.employeeFilters, "empAfiliation", this::filterFunction,this.empScrollPane,"emp");
//        JPanel panel = new JPanel();
//
//        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
//        panel.add(this.emptabNomAp);
//        panel.add(this.emptabempCode);
//        panel.add(this.emptabAfi);
//        this.empTab.add(panel);
        UIUtils.getTable(this.empTab, getJsonData(this.empJsonUrl), this.employeeFilters,empScrollPane,"emp");

        this.tabbedPane.addChangeListener(e -> {
            // Obtén el índice de la pestaña seleccionada
            int selectedIndex = tabbedPane.getSelectedIndex();
            // Si la pestaña seleccionada es la de empleados, actualiza la tabla de empleados
            if (selectedIndex == 0) {
                UIUtils.getTable(this.empTab, getJsonData(this.empJsonUrl), this.employeeFilters,empScrollPane,"emp");
            }
            // Si la pestaña seleccionada es la de boletas, actualiza la tabla de boletas
            else if (selectedIndex == 1) {
                UIUtils.getTable(this.payrollTab, getJsonData("src/main/java/payrollDetails.json"), payrollFilters,payrollScrollPane,"pay");
            }
        });
//        UIUtils.getTable(this.empTab, getJsonData(this.empJsonUrl), this.employeeFilters,empScrollPane,"emp");
//        UIUtils.getTable(this.payrollTab, getJsonData("src/main/java/payrollDetails.json"), payrollFilters,payrollScrollPane,"pay");
    }
    /**
     * Este metodo se encarga de aplicar el filtro a la tabla
     *
     * @param filterColumns -Los filtros que se aplicaran a la tabla
     * @param filter        -El filtro que se aplicara
     */
    private FilterInterface filterFunction(TableFilter[] filterColumns,TableFilter filter){

        for (int i = 0; i < filterColumns.length; i++) {

            if (filterColumns[i].getKey().equals(filter.getKey())) {
                filterColumns[i] = filter;
                break;
            }
        }
        this.employeeFilters = filterColumns;
        return null;
    }

    /**
     * Este metodo crea tabs y los añade al tab que inicializado al principio
     *
     * @param iconUrl  -La ruta de la imagen que se mostrara en el tab
     * @param tabName     -El titulo que se mostrara en el tab
     */
    private JComponent createTab(String iconUrl, String tabName) {
        ImageIcon icon = createImageIcon(iconUrl);
        ImageIcon resizedIcon = resizeImageIcon(icon, 50, 50);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JComponent component = makeTextPanel(tabName);

        panel.add(component);

        // Añade cualquier otro componente aquí, uno debajo del otro

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        this.tabbedPane.addTab(tabName, resizedIcon, scrollPane, "Does nothing");
        return panel;
    }
    /**
     * Este metodo se encarga de crear un ImageIcon a partir de una ruta
     *
     * @param path -La ruta de la imagen
     * @return ImageIcon -La imagen creada
     */
    private ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = getClass().getClassLoader().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /**
     *  Este metodo se encarga de crear un panel con un texto
     * @param text
     * @return
     */
    private JPanel makeTextPanel(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        return panel;
    }
    /**
     * Este metodo se encarga de redimensionar una imagen
     *
     * @param icon   -La imagen que se redimensionara
     * @param width  -El ancho de la imagen
     * @param height -El alto de la imagen
     * @return ImageIcon -La imagen redimensionada
     */
    private ImageIcon resizeImageIcon(ImageIcon icon, int width, int height) {
        Image image = icon.getImage();
        Image newImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(newImage);
    }

    /**
     * Este metodo se añade como respuesta a la accion de importar empleados
     * Abre un FileChooser para seleccionar el archivo json de empleados
     * Esta validado para que solo se pueda seleccionar un archivo xlsx(Excel)
     */
    private void openFileSelector() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(mainContainer);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String fileExtension = getFileNameExtension(selectedFile.getName());
            if (!fileExtension.equals("xlsx")) {
                JOptionPane.showMessageDialog(null, "Tipo de Archivo no valido");
                return;
            }

            processExcelFile(selectedFile);
        }
    }
    /**
     * Este metodo se encarga de obtener la extension de un archivo
     *
     * @param filename -El nombre del archivo
     * @return String -La extension del archivo
     */
    private String getFileNameExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1)
            return "";
        return filename.substring(lastDotIndex + 1);
    }
    /**
     * Este metodo se encarga de procesar el archivo xlsx seleccionado
     * Si el archivo es valido se procesa para llenar el json de empleados de la
     * ruta definida en el constructor
     *
     * @param excelFile -El archivo seleccionado
     * @throws IOException           -Si el archivo no es valido
     * @throws FileNotFoundException -Si el archivo no es encontrado
     *
     */
    private void processExcelFile(File excelFile) {
        try {
            FileInputStream inputStream = new FileInputStream(excelFile);
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < sheet.getLastRowNum(); i++) {
                if (i == 0)
                    continue;
                if (sheet.getRow(i).getCell(0).getCellType() == CellType.BLANK)
                    break;
                JSONObject jo = new JSONObject();
                for (int j = 0; j < sheet.getRow(i).getLastCellNum(); j++) {
                    Cell cell = sheet.getRow(i).getCell(j);
                    jo.put(sheet.getRow(0).getCell(j).getStringCellValue(), getCellValue(cell));
                }
                jsonArray.put(jo);
            }
            FileWriter empWriter = new FileWriter(this.empJsonUrl);
            empWriter.write(jsonArray.toString());
            empWriter.flush();
            empWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Este metodo se encarga de obtener el valor de una celda
     *
     * @param cell -La celda de la que se obtendra el valor
     * @return String -El valor de la celda
     */
    private String getCellValue(Cell cell) {
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        }
        if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        }
        return "";
    }
    /**
     * Este metodo se encarga de inicializar un archivo
     *
     * @param filePath -La ruta del archivo
     */
    private void initFile(String filePath) {
        File file = new File(filePath);

        // Check if the file exists
        if (!file.exists()) {
            try {
                // Create the file
                boolean fileCreated = file.createNewFile();
                if (fileCreated) {
                    System.out.println("File created successfully: " + filePath);
                } else {
                    System.out.println("Failed to create the file: " + filePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File already exists: " + filePath);
        }
    }

    /**
     * Este metodo se encarga de obtener los datos de un archivo json
         * @param  dataUrl -La ruta del archivo json
     * @return JSONArray -Los datos del archivo json
     */
    private static JSONArray getJsonData(String dataUrl) {
        try {
            // Verificar el tamaño del archivo antes de intentar leerlo
            long fileSize = Files.size(Paths.get(dataUrl));
            if (fileSize == 0) {
                System.out.println("El archivo está vacío.");
                return new JSONArray();
            }

            // Crear un JSON array a partir del archivo JSON
            try (FileReader reader = new FileReader(dataUrl)) {
                return new JSONArray(new JSONTokener(reader));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    } public String getJsonDataString(String dataUrl){
        try (FileReader reader = new FileReader(dataUrl)) {
            // Create JSON array from JSON file
                return new JSONObject(new JSONTokener(reader)).toString(2);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
   /**
     * Este metodo se encarga de obtener los datos de un archivo json y filtrarlos por una columna
     * @param  dataUrl -La ruta del archivo json
     * @param  column -La columna de la que se obtendran los datos
     * @return String[] -Los datos del archivo json
    *  Uso: getDataFilter("src/main/java/employees.json","empName");
     */
    private String[] getDataFilter(String dataUrl,String column){
        try  {
            // Create JSON array from JSON file
            JSONArray jsonArray = getJsonData(dataUrl);
            String[] data = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject rowData = jsonArray.getJSONObject(i);
                //check is same column is already added
                if (Arrays.asList(data).contains(rowData.get(column).toString())) {
                    continue;
                }
                data[i] = rowData.get(column).toString();
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




}