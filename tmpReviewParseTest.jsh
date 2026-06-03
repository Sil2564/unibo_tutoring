import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import it.unibo.tutoring.model.credit.ReviewRepository;
import it.unibo.tutoring.model.credit.ReviewRepository.Review;
var path = Paths.get("data", "reviews.csv");
var lines = Files.readAllLines(path);
System.out.println("lines: " + lines.size());
for (var i = 0; i < lines.size(); i++) {
  var line = lines.get(i);
  System.out.println("LINE[" + i + "]: '" + line + "'");
  if (line == null || line.trim().isEmpty() || line.startsWith("reviewerName")) continue;
  var parts = line.split(";", -1);
  System.out.println(" parts=" + parts.length + " => " + Arrays.toString(parts));
  if (parts.length < 5) continue;
  var recipientMatricola = parts[parts.length - 1].trim();
  System.out.println(" recipient=" + recipientMatricola);
  if (recipientMatricola.equals("3636989")) {
    var reviewerName = parts[0].trim();
    var date = parts[parts.length - 3].trim();
    var stars = Integer.parseInt(parts[parts.length - 2].trim());
    var comment = parts.length == 5 ? "" : String.join(";", java.util.Arrays.copyOfRange(parts, 4, parts.length - 1)).trim();
    System.out.println(" parsed: " + reviewerName + ", " + date + ", " + stars + ", '" + comment + "'");
  }
}
var reviews = ReviewRepository.loadReviewsForRecipient("3636989");
System.out.println("loaded=" + reviews.size());
for (var r : reviews) System.out.println(r);
