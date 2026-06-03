public class ZoneStatistics {

    private int nbPatients;
    private double avgInterventionTime;
    private String period;

    public ZoneStatistics(String period) {
        this.period = period;
        this.nbPatients = 0;
        this.avgInterventionTime = 0;
    }

    public void addPatient(double interventionTime) {
        nbPatients++;
        // New average = (old sum + new time) / new total. Old sum = old average * (nbPatients - 1)
        avgInterventionTime = ((avgInterventionTime * (nbPatients - 1)) + interventionTime) / nbPatients;
    }

    public void display() {
        System.out.println("Period: " + period);
        System.out.println("Patients: " + nbPatients);
        System.out.println("Avg intervention time: " + avgInterventionTime);
    }

    public int getNbPatients(){ 
        return nbPatients;
    }
    public double getAvgInterventionTime(){ 
        return avgInterventionTime;
    }
    public String getPeriod(){
        return period;
    }
}

