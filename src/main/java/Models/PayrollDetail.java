package Models;

import PDFGenerator.PDFGenerator;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

public class PayrollDetail extends Payroll {
    private PayrollDetailConcept[] payrollDetailConcepts;

    public PayrollDetail(Employee employee, Config config, String systemDate, PayrollDetailConcept[] payrollDetailConcepts) {
        super(employee, config, systemDate);
        this.payrollDetailConcepts = payrollDetailConcepts;
    }
    public  PayrollDetail(){
        super();
    }

    public void generatePayrollDetailsJson() throws FileNotFoundException {
        JSONArray payrollDetailsJson = getPayrollDetailsJson();
        try (FileWriter file = new FileWriter("src/main/java/payrollDetails.json")) { // Abrir el archivo en modo de agregar
            JSONObject payrollDetailJson = new JSONObject();
            JSONObject payrollEmployeeJson = new JSONObject();
            payrollEmployeeJson.put("employeeId", employee.getEmpCode());
            payrollEmployeeJson.put("employeeName", employee.getEmpName() + " " + employee.getEmpLastName());
            payrollEmployeeJson.put("employeeEmail", employee.getEmpEmail());
            payrollEmployeeJson.put("employeeAffiliation",getAffiliationName());
            payrollEmployeeJson.put("employeeStartDate", employee.getDateStart());
            payrollEmployeeJson.put("employeeSalary", employee.getEmpSalary());
            payrollEmployeeJson.put("employeeRole", employee.getEmpRole());

            payrollDetailJson.put("employee", payrollEmployeeJson);
            payrollDetailJson.put("systemDate", systemDate);
            payrollDetailJson.put("payrollId", payrollDetailsJson.length() + 1);
            JSONArray payrollDetailConceptsJson = getJsonArray();
            payrollDetailJson.put("payrollDetailConcepts", payrollDetailConceptsJson);
            if (payrollDetailsJson.isEmpty()) {
                payrollDetailsJson.put(payrollDetailJson);
                file.write(payrollDetailsJson.toString());
            } else {
                //if not empty, add new payroll detail toetJsonArray();
                //            payrollDetailJson.put("payrollDetailConcepts", payrollDetailConceptsJson);
                //            if (payrollDetailsJson.length() == 0) {
                //                payrollDetailsJson.put(payrollDetailJson);
                //                file.write(payrollDetailsJson.toString());
                //            } else {
                //                //if not empty, add new payroll detail to the array of payroll details
                payrollDetailsJson.put(payrollDetailJson);
                file.write(payrollDetailsJson.toString());
            }
            file.flush();
//            PDFGenerator pdfGenerator = new PDFGenerator("src/main/java/Templates/payroll-template.html","src/main/java/payroll.pdf",payrollDetailJson);
//            pdfGenerator.generatePDF();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONArray getJsonArray() {
        JSONArray payrollDetailConceptsJson = new JSONArray();

        for (PayrollDetailConcept payrollDetailConcept : payrollDetailConcepts) {
            JSONObject payrollDetailConceptJson = new JSONObject();
            payrollDetailConceptJson.put("conceptName", payrollDetailConcept.getConceptName());
            payrollDetailConceptJson.put("conceptAmount", payrollDetailConcept.getConceptAmount());
            payrollDetailConceptJson.put("conceptType", payrollDetailConcept.getConceptType());
            payrollDetailConceptsJson.put(payrollDetailConceptJson);
        }
        return payrollDetailConceptsJson;
    }

    public JSONArray getPayrollDetailsJson() throws FileNotFoundException {
        JSONArray payrollDetailsJson = new JSONArray();
        try (FileReader reader = new FileReader("src/main/java/payrollDetails.json")) {
            int character;
            String payrollDetailsString = "";
            while ((character = reader.read()) != -1) {
                payrollDetailsString += (char) character;
            }
            payrollDetailsJson = new JSONArray(payrollDetailsString);
        } catch (IOException e) {
            //RETURN EMPTY JSON ARRAY
            return payrollDetailsJson;
        } catch (Exception e) {
            return payrollDetailsJson;
        }
        return payrollDetailsJson;
    }
    public JSONObject getPayrollDetailJsonWithId(int id){
        JSONArray payrollDetailsJson = new JSONArray();
        try (FileReader reader = new FileReader("src/main/java/payrollDetails.json")) {
            //return jsonobjetct with key payrollId equals to id
            int character;
            String payrollDetailsString = "";
            while ((character = reader.read()) != -1) {
                payrollDetailsString += (char) character;
            }
            payrollDetailsJson = new JSONArray(payrollDetailsString);
            for (int i = 0; i < payrollDetailsJson.length(); i++) {
                JSONObject payrollDetail = payrollDetailsJson.getJSONObject(i);
                if (payrollDetail.getInt("payrollId") == id) {
                    return payrollDetail;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }


    public void initFile(String filePath) {
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
}
