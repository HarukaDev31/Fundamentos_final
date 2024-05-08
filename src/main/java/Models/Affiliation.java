package Models;

public class Affiliation {
    private int id;
    private String name;
    private double capture;
    private double flow;
    public Affiliation(int id, String name, double capture, double flow) {
        this.id = id;
        this.name = name;
        this.capture = capture;
        this.flow = flow;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public double getCapture() {
        return capture;
    }
    public double getFlow() {
        return flow;
    }
    public String toString() {
        System.out.println("Id: " + id + " Name: " + name + " Capture: " + capture + " Flow: " + flow);
        return null;
    }
}
