package pgdp.net;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A tool to generate simple HTML pages for PinguDating based on templates.
 */
public final class HtmlGenerator {

    static Path findTemplatePath = Path.of("templates", "search.html");
    static Path profileTemplatePath = Path.of("templates", "profile.html");

    private static final String TEMPL_MAX_AGE = "%maxAge";
    private static final String TEMPL_MIN_AGE = "%minAge";
    private static final String TEMPL_TABLE = "%table";
    private static final String TEMPL_HOBBIES = "%hobbies";
    private static final String TEMPL_AGE = "%age";
    private static final String TEMPL_NAME = "%name";
    private static final String TEMPL_SEXUAL_ORIENTATION = "%sexualOrientation";

    private static final String HTML_TABLE_START = "<table border=\"1px solid black\">"
            + "<tr><td><b>Name</b></td><td><b>Age</b></td><td><b>Sexual Orientation</b></td></tr>";
    private static final String HTML_TABLE_ROW = "<tr><td><a href=\"user/%s\">%s</a></td><td>%s</td><td>%s</td></tr>";
    private static final String HTML_TABLE_END = "</table>";
    private static final String HTML_TABLE_NO_RESULTS = "<p>No results found :(</p>";

    private final TemplateProcessor profileTemplate;
    private final TemplateProcessor findTemplate;

    public HtmlGenerator() throws IOException {
        this.profileTemplate = new TemplateProcessor(profileTemplatePath);
        this.findTemplate = new TemplateProcessor(findTemplatePath);
    }

    /**
     * Generates a profile page for the supplied DatingPingu using the
     * profileTemplate
     */
    public String generateProfilePage(DatingPingu dp) {
        return profileTemplate.replace(Map.of(TEMPL_NAME, dp.getName(), TEMPL_AGE, String.valueOf(dp.getAge()),
                TEMPL_SEXUAL_ORIENTATION, dp.getSexualOrientation(), TEMPL_HOBBIES,
                dp.getHobbies().stream().collect(Collectors.joining(", "))));
    }

    /**
     * Generates the find page for the supplied DatingPingu-matches using the
     * findTemplate.
     * <p>
     * The request passed to the method is used to fill the form fields with the
     * user's input values
     */
    public String generateFindPage(SeachRequest request, List<DatingPingu> results) {
        String table;
        if (results.isEmpty()) {
            table = HTML_TABLE_NO_RESULTS;
        } else {
            table = results.stream()
                    .map(dp -> String.format(HTML_TABLE_ROW, dp.getId(), dp.getName(), dp.getAge(),
                            dp.getSexualOrientation()))
                    .collect(Collectors.joining("", HTML_TABLE_START, HTML_TABLE_END));
        }
        return findTemplate.replace(Map.of(TEMPL_MIN_AGE, String.valueOf(request.getMinAge()), TEMPL_MAX_AGE,
                String.valueOf(request.getMaxAge()), TEMPL_SEXUAL_ORIENTATION, request.getSexualOrientation(),
                TEMPL_HOBBIES, request.getHobbies().stream().collect(Collectors.joining(" ")), TEMPL_TABLE, table));
    }

    /**
     * Generates the start page which is the find page without any preset inputs and
     * no table with matching DatingPingus
     */
    public String generateStartPage() {
        return findTemplate.replace(Map.of(TEMPL_MIN_AGE, "", TEMPL_MAX_AGE, "", TEMPL_SEXUAL_ORIENTATION, "",
                TEMPL_HOBBIES, "", TEMPL_TABLE, ""));
    }
}
