package Models;

public class Config {
    private Double uit;
    private Segment[] segments;
    private Affiliation[] affiliations;
    private double essalud;
    private int[] gratificationMonths;
    private int[] ctsMonths;
    public Config(double uit, Segment[] segments, Affiliation[] affiliations, double essalud, int[] gratificationMonths, int[] ctsMonths) {
        this.uit = uit;
        this.segments = segments;
        this.affiliations = affiliations;
        this.essalud = essalud;
        this.gratificationMonths = gratificationMonths;
        this.ctsMonths = ctsMonths;
    }
    public Double getUit() {
        return uit;
    }
    public Segment[] getSegments() {
        return segments;
    }
    public Segment getSegment(int id) {
        for (Segment segment : segments) {
            if (segment.getId() == id) {
                return segment;
            }
        }
        return null;
    }
    public Affiliation[] getAffiliations() {
        return affiliations;
    }
    public Affiliation getAffiliation(int id) {
        for (Affiliation affiliation : affiliations) {
            if (affiliation.getId() == id) {
                return affiliation;
            }
        }
        return null;
    }
    public double getEssalud() {
        return essalud;
    }
    public int[] getGratificationMonths() {
        return gratificationMonths;
    }
    public int[] getCtsMonths() {
        return ctsMonths;
    }
    public String toString() {
        System.out.println("Uit: " + uit + " Segments: " + segments + " Affiliations: " + affiliations + " Essalud: " + essalud + " GratificationMonths: " + gratificationMonths + " CtsMonths: " + ctsMonths);
        return null;
    }
}
