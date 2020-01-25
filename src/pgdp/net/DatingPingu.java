package pgdp.net;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class DatingPingu {
    private final long id;
    private final String name;
    private final String sexualOrientation;
    private final int age;
    private final Set<String> hobbies;

    public DatingPingu(long id, String name, String sexualOrientation, int age, Set<String> hobbies) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name");
        this.sexualOrientation = Objects.requireNonNull(sexualOrientation, "sexualOrientation");
        if (age <= 0)
            throw new IllegalArgumentException("age <= 0");
        this.age = age;
        this.hobbies = Objects.requireNonNull(hobbies, "hobbies");
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSexualOrientation() {
        return sexualOrientation;
    }

    public int getAge() {
        return age;
    }

    public Set<String> getHobbies() {
        return hobbies;
    }

    public String toCsvRow() {
        return String.format("%s,%s,%s,%s,%s", id, name, sexualOrientation, age,
                hobbies.stream().collect(Collectors.joining(",", "\"", "\"")));
    }

    public static DatingPingu parse(String csvRow) {
        String[] parts = csvRow.split(",", 5);
        return new DatingPingu(Long.parseLong(parts[0]), parts[1], parts[2], Integer.parseInt(parts[3]),
                Set.of(parts[4].substring(1, parts[4].length() - 1).split(",", -1)));
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof DatingPingu))
            return false;
        DatingPingu other = (DatingPingu) obj;
        return id == other.id;
    }

    @Override
    public String toString() {
        return name;
    }
}
