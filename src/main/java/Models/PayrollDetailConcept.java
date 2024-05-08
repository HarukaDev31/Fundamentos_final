package Models;

public class PayrollDetailConcept
{
    private String conceptName;
    private double conceptAmount;
    //1: Ingreso, 2: Descuento 3:Empleador Descuento
    private int conceptType;
    public PayrollDetailConcept(String conceptName, double conceptAmount, int conceptType) {
        this.conceptName = conceptName;
        this.conceptAmount = conceptAmount;
        this.conceptType = conceptType;
    }
    public String getConceptName() {
        return conceptName;
    }
    public double getConceptAmount() {
        return conceptAmount;
    }
    public int getConceptType() {
        return conceptType;
    }
    public String getConceptTypeName() {
        if(conceptType==1){
            return "Ingreso";
        }else if(conceptType==2){
            return "Descuento";
        }else if(conceptType==3){
            return "Empleador Descuento";
        }
        return null;
    }
    public String toString() {
        System.out.println("Concept Name: " + conceptName + " Concept Amount: " + conceptAmount + " Concept Type: " + conceptType);
        return null;
    }
}
