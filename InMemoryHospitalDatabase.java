import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHospitalDatabase implements HospitalDatabase {

    private final Map<String, Hospital> store = new LinkedHashMap<>();

    @Override
    public void addHospital(Hospital hospital) {
        if (hospital == null)
            throw new IllegalArgumentException("Hospital cannot be null");
        store.put(hospital.getId(), hospital);
    }

    @Override
    public List<Hospital> getHospitals() {
        return new ArrayList<>(store.values());
    }

    @Override
    public int getCapacity(String id) {
        Hospital h = store.get(id);
        return h != null ? h.getCurrentCapacity() : -1;
    }

    @Override
    public void updateCapacity(String id, int newCapacity) {
        if (!store.containsKey(id))
            throw new IllegalArgumentException("Hospital not found: " + id);
        if (newCapacity < 0)
            throw new IllegalArgumentException("Capacity cannot be negative");
        store.get(id).updateCapacity(newCapacity);
    }

    @Override
    public Hospital findById(String id) {
        return store.get(id);
    }

    @Override
    public String toString() {
        return "InMemoryHospitalDatabase{hospitals=" + store.size() + "}";
    }
}
