package Models;

public class Employee {

    private String empLastName;
    private String empCode;
    private String empName;
    private String empSalary;
    private String empEmail;
    private int empAffiliation;
    private String dateStart;
    private String empRole;
    public Employee(String empLastName, String empCode, String empName, String empSalary, String empEmail, int empAffiliation, String dateStart, String empRole) {
        this.empLastName = empLastName;
        this.empCode = empCode;
        this.empName = empName;
        this.empSalary = empSalary;
        this.empEmail = empEmail;
        this.empAffiliation = empAffiliation;
        this.dateStart = dateStart;
        this.empRole = empRole;
    }
    public String getEmpRole() {
        return empRole;
    }
    public String getEmpLastName() {
        return empLastName;
    }
    public String getEmpCode() {
        return empCode;
    }
    public String getEmpName() {
        return empName;
    }
    public Double getEmpSalary() {
        return Double.parseDouble(empSalary);
    }
    public String getEmpEmail() {
        return empEmail;
    }
    public int getEmpAffiliation() {
        return empAffiliation;
    }
    public String getDateStart() {
        return dateStart;
    }
    public int getStartYear() {
        return Integer.parseInt(dateStart.substring(0, 4));
    }
    public int getStartMonth() {
        return Integer.parseInt(dateStart.substring(5, 7));
    }
    public int getStartDay() {
        return Integer.parseInt(dateStart.substring(8, 10));
    }
    public String toString() {
        System.out.println("Employee: " + empLastName + " " + empCode + " " + empName + " " + empSalary + " " + empEmail + " " + empAffiliation + " " + dateStart);
        return null;
    }


}
