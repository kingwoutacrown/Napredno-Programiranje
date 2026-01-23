import java.util.*;

class DocumentViewer {
    Map<String, Document> documents;
    public DocumentViewer() {
        documents = new HashMap<>();
    }
    public void addDocument(String id, String text) {
        documents.put(id,new BasicDocument(text));
    }
    public void enableLineNumbers(String id) {
        documents.put(id, new LineNumberDecorator(documents.get(id)));
    }
    public void enableWordCount(String id) {
        documents.put(id, new WordCountDecorator(documents.get(id)));
    }
    public void enableRedaction(String id, List<String> forbiddenWords) {
        documents.put(id, new RedactionDecorator(documents.get(id),forbiddenWords));
    }
    public void display(String id) {
        System.out.println("=== Document " + id + " ===");
        System.out.println(documents.get(id).getContent());
    }
}
interface Document {
    String getContent();
}

class BasicDocument implements Document {
    private String content;
    public BasicDocument(String content) {
        this.content=content;
    }
    @Override
    public String getContent() {
        return content;
    }
}

abstract class DocumentDecorator implements Document {
    private Document document;
    public DocumentDecorator(Document document) {
        this.document=document;
    }
    public Document getDocument() { return document;}
}

class LineNumberDecorator extends DocumentDecorator {
    public LineNumberDecorator(Document document) {
        super(document);
    }
    @Override
    public String getContent() {
        String[] lines = getDocument().getContent().split("\n");
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<lines.length;i++) {
            sb.append(i+1);
            sb.append(": ");
            sb.append(lines[i]);
            sb.append("\n");
        }
        return sb.toString().trim();
    }
}

class WordCountDecorator extends DocumentDecorator {
    public WordCountDecorator(Document document) {
        super(document);
    }
    @Override
    public String getContent() {
        String content = getDocument().getContent();
        if(content.trim().isEmpty()) {
            return content+"\nWords" + 0;
        }
        int words = content.trim().split("\\s+").length;
        return content + "\nWords: " + words;
    }
}

class RedactionDecorator extends DocumentDecorator {
    private List<String> forbiddenWords;
    public RedactionDecorator(Document document, List<String> forbiddenWords) {
        super(document);
        this.forbiddenWords=forbiddenWords;
    }
    @Override
    public String getContent() {
        String content = getDocument().getContent();
        for(String word: forbiddenWords) {
            word = word.toLowerCase();
            content = content.replaceAll("(?i)\\b" + word + "\\b", "*");
        }
        return content;
    }
}

public class DocumentViewerTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DocumentViewer viewer = new DocumentViewer();


        int numDocuments = Integer.parseInt(sc.nextLine());

        for (int d = 0; d < numDocuments; d++) {

            String id = sc.nextLine().trim();


            int numLines = Integer.parseInt(sc.nextLine().trim());


            StringBuilder text = new StringBuilder();
            for (int i = 0; i < numLines; i++) {
                text.append(sc.nextLine());
                if (i < numLines - 1) text.append("\n"); // keep newlines
            }

            viewer.addDocument(id, text.toString());
        }

        while (true) {
            if (!sc.hasNextLine()) break;
            String line = sc.nextLine();
            if (line.equals("exit")) break;

            String[] parts = line.split(" ");
            String command = parts[0];
            String docId = parts[1];

            switch (command) {
                case "enableLineNumbers":
                    viewer.enableLineNumbers(docId);
                    break;

                case "enableWordCount":
                    viewer.enableWordCount(docId);
                    break;

                case "enableRedaction":
                    ArrayList<String> forbiddenWords = new ArrayList<>();
                    for(int i=2;i< parts.length;i++) {
                        forbiddenWords.add(parts[i]);
                    }
                    viewer.enableRedaction(docId, forbiddenWords);
                    break;

                case "display":
                    viewer.display(docId);
                    break;

                default:
                    System.out.println("Unknown command: " + command);
            }
        }

        sc.close();
    }
}
