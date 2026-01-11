import java.util.*;
import java.util.List;

public class DecoratorTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DocumentViewer viewer = new DocumentViewer();

        int n = sc.nextInt();

        for (int i = 0; i < n; i++) {
            String id = sc.next();
            int lines = sc.nextInt();
            sc.nextLine();

            StringBuilder content = new StringBuilder();
            for (int j = 0; j < lines; j++) {
                content.append(sc.nextLine());
                if (j < lines - 1) content.append("\n");
            }

            viewer.addDocument(id, content.toString());
        }

        while (true) {
            String command = sc.next();

            if (command.equals("exit")) {
                break;
            }

            if (command.equals("enableLineNumbers")) {
                String id = sc.next();
                viewer.enableLineNumbers(id);
            }
            else if (command.equals("enableWordCount")) {
                String id = sc.next();
                viewer.enableWordCount(id);
            }
            else if (command.equals("enableRedaction")) {
                String id = sc.next();
                String restOfLine = sc.nextLine().trim();
                String[] words = restOfLine.split("\\s+");
                List<String> forbidden = new ArrayList<>();
                for (String w : words) {
                    if (!w.isEmpty()) forbidden.add(w);
                }
                viewer.enableRedaction(id, forbidden);
            }
            else if (command.equals("display")) {
                String id = sc.next();
                viewer.display(id);
            }
        }
    }
}
interface Document {
    String render();
}
class PlainDocument implements Document {
    private final String text;

    public PlainDocument(String text) {
        this.text = text;
    }

    @Override
    public String render() {
        return text;
    }
}
abstract class DocumentDecorator implements Document {
    protected final Document document;

    public DocumentDecorator(Document document) {
        this.document = document;
    }
}

class LineNumberDecorator extends DocumentDecorator {

    public LineNumberDecorator(Document document) {
        super(document);
    }

    @Override
    public String render() {
        String[] lines = document.render().split("\n");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            sb.append(i + 1)
                    .append(": ")
                    .append(lines[i])
                    .append("\n");
        }

        return sb.toString().trim();
    }
}


class WordCountDecorator extends DocumentDecorator {

    public WordCountDecorator(Document document) {
        super(document);
    }

    @Override
    public String render() {
        String content = document.render();
        String trimmed = content.trim();

        int words = trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;

        return content + "\nWords: " + words;
    }
}

class RedactionDecorator extends DocumentDecorator {

    private final List<String> forbiddenWords;

    public RedactionDecorator(Document document, List<String> forbiddenWords) {
        super(document);
        this.forbiddenWords = forbiddenWords;
    }

    @Override
    public String render() {
        String result = document.render();
        for (String word : forbiddenWords) {
            result=result.replaceAll("(?i)\\b" + word + "\\b", "*");
        }
        return result;
    }
}

class DocumentViewer {

    private final Map<String, Document> documents = new HashMap<>();

    public DocumentViewer() {}

    public void addDocument(String id, String content) {
        documents.put(id, new PlainDocument(content));
    }

    public void enableLineNumbers(String id) {
        documents.put(id, new LineNumberDecorator(documents.get(id)));
    }

    public void enableWordCount(String id) {
        documents.put(id, new WordCountDecorator(documents.get(id)));
    }

    public void enableRedaction(String id, List<String> forbiddenWords) {
        documents.put(id, new RedactionDecorator(documents.get(id), forbiddenWords));
    }

    public void display(String id) {
        System.out.println("=== Document " + id + " ===");
        System.out.println(documents.get(id).render());
    }
}
