import java.util.List;

public interface HospitalDatabase {

    List<Hospital> getHospitals();
    int getCapacity(String id);
    void updateCapacity(String id, int newCapacity);
    Hospital findById(String id);
    void addHospital(Hospital hospital);
}
