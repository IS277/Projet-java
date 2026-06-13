import java.io.Serializable;
import java.util.Objects;

/**
 * Abstract base class for all people tracked by the emergency dispatch system.
 *
 * <p>Centralises the attributes shared by every person in the domain model:
 * a unique identifier, a display name and a geographic position.
 * Concrete subclasses (e.g. {@link Patient}) extend it to add role-specific data.</p>
 *
 * <p>Implementing {@link Serializable} allows any subclass instance to be
 * persisted and restored by {@link MapPersistenceService} without additional
 * configuration.</p>
 *
 * <p>Equality is based solely on the identifier so that two references to the
 * same logical person compare as equal regardless of positional changes.</p>
 *
 * @author Maissa Tirsane, Anas Chokri, Iyed Souissi, Valery Vo-Van
 * @version 1.0
 */
public abstract class Person implements Serializable{
    /**
     * Unique identifier that distinguishes this person from all others in the system.
     * Declared {@code protected} so that subclasses can read it directly in
     * overridden {@code toString} methods without going through a getter.
     */
    protected String id;
    /**
     * Human-readable display name shown in the graphical interface and console output.
     */
    protected String name;
    /**
     * Current geographic position of this person on the dispatch map.
     */
    protected Coordinate position;
    /**
     * Fixed serialisation version identifier.
     * Prevents {@link java.io.InvalidClassException} when a saved file is loaded
     * after a minor refactoring that does not change the logical structure.
     */
    private static final long serialVersionUID = 1L;
    

    /**
     * Initialises the three attributes shared by all person subclasses.
     *
     * @param id       unique identifier; must be non-null and non-blank
     * @param name     display name shown to the dispatcher
     * @param position initial geographic position on the map
     */
    public Person(String id, String name, Coordinate position) {
        this.id = id;
        this.name = name;
        this.position = position;
    }

    /**
     * Returns the current geographic position of this person.
     *
     * @return current position; never {@code null} after construction
     */
    public Coordinate getPosition() {
        return position;
    }

    /**
     * Moves this person to a new geographic position.
     *
     * <p>Called whenever the dispatcher drags a person on the map or issues
     * a move command from the CLI. {@link MapManager} triggers a full recomputation
     * after calling this method so that assignments and geometry remain consistent.</p>
     *
     * @param newPosition the destination position; must not be {@code null}
     */
    public void updatePosition(Coordinate newPosition) {
        this.position = newPosition;
    }

    /**
     * Returns the unique identifier of this person.
     *
     * @return identifier string; never {@code null}
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the display name of this person.
     *
     * @return name string; never {@code null}
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", position=" + position +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Person)) return false;

        Person other = (Person) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
