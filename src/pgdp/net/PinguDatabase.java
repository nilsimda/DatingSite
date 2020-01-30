package pgdp.net;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PinguDatabase {
    static Path dataFile = Path.of("db", "penguins.csv");
    static List<DatingPingu> lst = new ArrayList<>();

    public PinguDatabase() {
        try {
            Files.lines(dataFile).forEach(s -> lst.add(DatingPingu.parse(s)));
        } catch (IOException e) {
            System.out.println("Datei konnte nicht gelesen werden.");
        }
    }

    boolean add(DatingPingu datingPingu) {
        String line = datingPingu.toCsvRow();
        boolean alreadyExists = false;
        for (DatingPingu d : lst) {
            if (d.equals(datingPingu)) {
                alreadyExists = true;
                break;
            }
        }
        if (alreadyExists) {
            return false;
        }
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(String.valueOf(dataFile), true));
            writer.println(line);
            writer.close();
            DatingPingu.parse(line);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    Optional<DatingPingu> lookupById(long id) {
        return lst.stream().filter(d -> d.getId() == id).findFirst();
    }

    List<DatingPingu> findMatchesFor(SeachRequest request) {
        List<DatingPingu> resultList = new ArrayList<>();
        lst.stream().filter(datingPingu -> datingPingu.getAge() <= request.getMaxAge() && datingPingu.getAge() >= request.getMinAge()
                && datingPingu.getSexualOrientation().equals(request.getSexualOrientation()) && checkHobbies(datingPingu.getHobbies(), request.getHobbies())).forEach(resultList::add);
        return resultList;
    }

    private static boolean checkHobbies(Set<String> hobbies1, Set<String> hobbies2) {
        for (String s1 : hobbies1) {
            for (String s2 : hobbies2) {
                if (s1.equals(s2))
                    return true;
            }
        }
        return false;
    }

}
