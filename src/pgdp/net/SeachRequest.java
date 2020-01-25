package pgdp.net;

import java.util.Objects;
import java.util.Set;

public final class SeachRequest {
    private final String sexualOrientation;
    private final int minAge;
    private final int maxAge;
    private final Set<String> hobbies;

    public SeachRequest(String sexualOrientation, int minAge, int maxAge, Set<String> hobbies) {
        this.sexualOrientation = Objects.requireNonNull(sexualOrientation, "sexualOrientation");
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.hobbies = Objects.requireNonNull(hobbies, "hobbies");
    }

    public String getSexualOrientation() {
        return sexualOrientation;
    }

    public int getMinAge() {
        return minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public Set<String> getHobbies() {
        return hobbies;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hobbies, maxAge, minAge, sexualOrientation);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof SeachRequest))
            return false;
        SeachRequest other = (SeachRequest) obj;
        return Objects.equals(hobbies, other.hobbies) && maxAge == other.maxAge && minAge == other.minAge
                && Objects.equals(sexualOrientation, other.sexualOrientation);
    }

    @Override
    public String toString() {
        return String.format("SeachRequest [sexualOrientation=%s, ageRange=%s-%s, hobbies=%s]", sexualOrientation,
                minAge, maxAge, hobbies);
    }

}
