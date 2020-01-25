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
    private final String aboutMe;

    public DatingPingu(long id, String name, String sexualOrientation, int age, Set<String> hobbies, String aboutMe) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name");
        this.sexualOrientation = Objects.requireNonNull(sexualOrientation, "sexualOrientation");
        if (age <= 0)
            throw new IllegalArgumentException("age <= 0");
        this.age = age;
        this.hobbies = Objects.requireNonNull(hobbies, "hobbies");
        this.aboutMe = Objects.requireNonNull(aboutMe, "aboutMe");
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

    public String getAboutMe() {
        return aboutMe;
    }

    public String toCsvRow() {
        return String.format("%s,%s,%s,%s,%s,\"%s\"", id, name, sexualOrientation, age,
                hobbies.stream().collect(Collectors.joining(" ")), aboutMe);
    }

    public static DatingPingu parse(String csvRow) {
        String[] parts = csvRow.split(",", 6);
        return new DatingPingu(Long.parseLong(parts[0]), parts[1], parts[2], Integer.parseInt(parts[3]),
                Set.of(parts[4].split(" ", -1)), parts[5].substring(1, parts[5].length() - 1));
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
