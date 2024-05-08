package Models;

public class Segment {
    private int id;
    private String name;
    private double inf;
    private double sup;
    private double ret;
    public Segment(int id,String name, double inf, double sup, double ret) {
        this.id = id;
        this.name = name;
        this.inf = inf;
        this.sup = sup;
        this.ret = ret;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public double getInf() {
        return inf;
    }
    public double getSup() {
        return sup;
    }
    public double getRet() {
        return ret;
    }
    public String toString() {
        System.out.println("Name: " + name + " Inf: " + inf + " Sup: " + sup + " Ret: " + ret);
        return null;
    }
}
