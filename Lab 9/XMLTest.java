import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

interface XMLComponent {
    public void addAttribute(String attribute, String value);
    public String toString(int indent);
}

class XMLLeaf implements XMLComponent{
    private String tag;
    private String content;
    private Map<String,String> attributes;
    public XMLLeaf(String tag, String content) {
        this.tag = tag;
        this.content = content;
        attributes = new TreeMap<>(Comparator.reverseOrder());
    }
    @Override
    public void addAttribute(String attribute, String value) {
        attributes.put(attribute,value);
    }
    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("    ".repeat(indent));
        sb.append("<").append(tag);
        if(!attributes.isEmpty()) {
            attributes.forEach((key, value) -> sb.append(" ").append(key).append("=\"").append(value).append("\""));
        }
        sb.append(">").append(content).append("</").append(tag).append(">");
        return sb.toString();
    }
}

class XMLComposite implements XMLComponent{
    private String tag;
    private Map<String,String> attributes;
    private List<XMLComponent> components;
    public XMLComposite(String tag) {
        this.tag=tag;
        attributes = new TreeMap<>(Comparator.reverseOrder());
        components = new ArrayList<>();
    }
    public void addComponent(XMLComponent xmlComponent) {
        components.add(xmlComponent);
    }
    @Override
    public void addAttribute(String attribute, String value) {
        attributes.put(attribute,value);
    }
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("    ".repeat(indent));
        sb.append("<").append(tag);
        if(!attributes.isEmpty()) {
            attributes.forEach((k,v) -> sb.append(" ").append(k).append("=\"").append(v).append("\""));
        }
        sb.append(">\n");
        if(!components.isEmpty())
        {
            components.forEach(c -> sb.append(c.toString(indent+1)).append("\n"));
        }
        sb.append("    ".repeat(indent)).append("</").append(tag).append(">");
        return sb.toString();
    }
}
public class XMLTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = sc.nextInt();
        XMLComponent component = new XMLLeaf("student", "Trajce Trajkovski");
        component.addAttribute("type", "redoven");
        component.addAttribute("program", "KNI");

        XMLComposite composite = new XMLComposite("name");
        composite.addComponent(new XMLLeaf("first-name", "trajce"));
        composite.addComponent(new XMLLeaf("last-name", "trajkovski"));
        composite.addAttribute("type", "redoven");
        component.addAttribute("program", "KNI");

        if (testCase==1) {
            System.out.println(component.toString(0));
        } else if(testCase==2) {
            System.out.println(composite.toString(0));
        } else if (testCase==3) {
            XMLComposite main = new XMLComposite("level1");
            main.addAttribute("level","1");
            XMLComposite lvl2 = new XMLComposite("level2");
            lvl2.addAttribute("level","2");
            XMLComposite lvl3 = new XMLComposite("level3");
            lvl3.addAttribute("level","3");
            lvl3.addComponent(component);
            lvl2.addComponent(lvl3);
            lvl2.addComponent(composite);
            lvl2.addComponent(new XMLLeaf("something", "blabla"));
            main.addComponent(lvl2);
            main.addComponent(new XMLLeaf("course", "napredno programiranje"));

            System.out.println(main.toString(0));
        }
    }
}
