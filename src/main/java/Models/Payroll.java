package Models;
import org.json.JSONArray;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Arrays;

public class Payroll {
    public Employee employee;
    public Config config;
    public String systemDate;
    public Payroll(Employee employee, Config config, String systemDate) {
        this.employee = employee;
        this.config = config;
        this.systemDate = systemDate;
    }

    public Payroll() {

    }

    public double getCts(){
        double cts=0;
        int[] ctsMonths=config.getCtsMonths();
        for(int month:ctsMonths){
            if(getSystemMonth()==month){
                cts=employee.getEmpSalary()/360*getDaysWorked();
                break;
            }
        }
        return cts;
    }
    private double getEssalud() {
        return config.getEssalud()*employee.getEmpSalary();
    }
    public double getGratification(int monthManual) {
        int[] gratificationMonths = config.getGratificationMonths();
        int getEmpStartYear = employee.getStartYear();
        int getEmpStartMonth = employee.getStartMonth();
        for (int month : gratificationMonths) {
            if (month == monthManual) {

                if(getEmpStartYear == getSystemYear()) {
                    if(getEmpStartMonth >= monthManual) {
                        return 0;
                    }
                    int semesterMonthWorked= Math.min(month - (getEmpStartMonth>6?getEmpStartMonth+1:getEmpStartMonth), 6);
                    return (employee.getEmpSalary() / 6) * semesterMonthWorked + getEssalud();
                }else{
                    return (employee.getEmpSalary() / 6)*6 + getEssalud();
                }
            }
            continue;
        }
        return 0;
    }
    public double getFifthCategory() {
        double remaining = Math.max(0, getSalaryForecast()+getGratificationForeCast() - (config.getUit() * 7));
        if (remaining == 0) {
            return 0;
        }
        double fifthCategory = 0;
        Segment[] segments = config.getSegments();
        for (Segment segment : segments) {
            if (segment.getSup() == 999999) {
                fifthCategory += remaining * segment.getRet();
                break;
            } else if (segment.getId()==segments.length) {
                fifthCategory += remaining * segment.getRet();
                break;
            } else {
                fifthCategory += (segment.getSup() - segment.getInf()) * config.getUit() * segment.getRet();
                remaining -= (segment.getSup() - segment.getInf()) * config.getUit();
            }
        }
        return fifthCategory;
    }
    public void getFifthCategoryMonthly(){
        //get all payrolls in this year from this employee and sum all fifth categories, then sustract with fifth category forecast divided by remaining months
    }

    public double getSalaryForecast() {
        return employee.getStartYear()==getSystemYear()?employee.getEmpSalary()*(13-employee.getStartMonth()):employee.getEmpSalary()*12;
    }
    public double getGratificationForeCast(){

        return getGratification(7)+getGratification(12);

    }
    private int getSystemYear() {
        return Integer.parseInt(systemDate.substring(0, 4));
    }
    private int getSystemMonth() {
        return Integer.parseInt(systemDate.substring(5, 7));
    }
    private int getSystemDay() {
       //get last day of month
        return LocalDate.of(getSystemYear(), getSystemMonth(), 1).lengthOfMonth();
    }
    private int getDaysWorked() {
        LocalDate start = LocalDate.of(employee.getStartYear(), employee.getStartMonth(), employee.getStartDay());
        LocalDate end = LocalDate.of(getSystemYear(), getSystemMonth(), getSystemDay());
        return (int) (end.toEpochDay() - start.toEpochDay());
    }
    public PayrollDetailConcept[] getPayrollConcepts(){
        PayrollDetailConcept[] payrollDetailConcepts = new PayrollDetailConcept[8];
        payrollDetailConcepts[0] = new PayrollDetailConcept("Sueldo", employee.getEmpSalary(), 1);
        payrollDetailConcepts[1] = new PayrollDetailConcept("CTS", getCts(), 1);
        payrollDetailConcepts[2] = new PayrollDetailConcept("Essalud", getEssalud(), 3);
        payrollDetailConcepts[3] = new PayrollDetailConcept("Gratificacion", getGratification(getSystemMonth()), 1);
        payrollDetailConcepts[4] = new PayrollDetailConcept("Renta5nta", 0, 2);
        payrollDetailConcepts[5] = new PayrollDetailConcept("Afiliacion", getAffiliationDiscount(), 2);
        payrollDetailConcepts[6] = new PayrollDetailConcept("Total Descuentos", getAffiliationDiscount(), 2);
        payrollDetailConcepts[7] = new PayrollDetailConcept("Neto", employee.getEmpSalary()+getCts()+getGratification(getSystemMonth())-getAffiliationDiscount(), 1);
        return payrollDetailConcepts;
    }
    private double getAffiliationDiscount(){
        return employee.getEmpSalary()*config.getAffiliation(employee.getEmpAffiliation()).getCapture()+config.getAffiliation(employee.getEmpAffiliation()).getFlow();
    }
    String getAffiliationName(){
     return config.getAffiliation(employee.getEmpAffiliation()).getName();
    }
    public void generatePayrollJson() throws FileNotFoundException {
        //generate json with payroll concepts
        PayrollDetail payrollDetail = new PayrollDetail(employee, config, systemDate, getPayrollConcepts());
        payrollDetail.initFile("src/main/java/payrollDetails.json");
        //check if  jsonArray is empty
        payrollDetail.generatePayrollDetailsJson();
    }
}
