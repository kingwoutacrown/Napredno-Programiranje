import java.util.*;
import java.util.stream.Collectors;

class Post {
    private String username;
    private String postContent;
    private List<Comment> comments;
    private Map<String,Comment> allComments;
    public Post(String username, String postContent) {
        this.username=username;
        this.postContent=postContent;
        comments = new ArrayList<>();
        allComments = new HashMap<>();
    }
    void addComment(String username, String commentId, String content, String replyToId) {
        int indent;
        if(replyToId== null) {
            indent = 2;
        }
        else {
            indent = allComments.get(replyToId).getIndent()+1;
        }
        Comment comment = new Comment(username,commentId,content,indent);
        allComments.put(commentId,comment);
        if(replyToId==null) {
            comments.add(comment);
        }
        else {
            allComments.get(replyToId).addComment(comment);
        }
    }
    void likeComment(String commentId) {
        allComments.get(commentId).addLike();
    }
    @Override
    public String toString() {
        List<Comment> sorted = comments.stream().sorted(new LikeRankingComparator()).collect(Collectors.toCollection(ArrayList::new));
        StringBuilder sb = new StringBuilder();
        sb.append("Post: ");
        sb.append(postContent);
        sb.append("\nWritten by: ");
        sb.append(username);
        if(allComments.isEmpty())
            return sb.toString();
        sb.append("\nComments: \n");
        for(Comment c : sorted) {
            sb.append(c.toString());
        }
        return sb.toString();
    }
}

class Comment {
    private String username;
    private String commentId;
    private String content;
    private int likes;
    private int indent;
    private List<Comment> replies;
    public Comment(String username,String commentId,String content, int indent) {
        this.username = username;
        this.commentId=commentId;
        this.content=content;
        replies = new ArrayList<>();
        likes = 0;
        this.indent = indent;
    }
    public void addComment(Comment comment) {
        replies.add(comment);
    }
    public void addLike() {
        likes += 1;
    }
    public int getLikes() {
        return likes;
    }
    public int getAllLikes() {
        if(replies.isEmpty()) {
            return likes;
        }
        else return likes + replies.stream().mapToInt(Comment::getAllLikes).sum();
    }
    public int getIndent() { return indent; }
    @Override
    public String toString() {
        List<Comment> sorted = replies.stream().sorted(new LikeRankingComparator())
                .collect(Collectors.toCollection(ArrayList::new));
        StringBuilder sb = new StringBuilder();
        sb.append("    ".repeat(indent));
        sb.append("Comment: ").append(content);
        sb.append("\n").append("    ".repeat(indent));
        sb.append("Written by: ").append(username);
        sb.append("\n").append("    ".repeat(indent));
        sb.append("Likes: ").append(getLikes());
        sb.append("\n");
        if(sorted.isEmpty()) {
            return sb.toString();
        }
        for(Comment r : sorted) {
            sb.append(r.toString());
        }
        return sb.toString();
    }
}

class LikeRankingComparator implements Comparator<Comment> {
    @Override
    public int compare(Comment a,Comment b) {
        return Integer.compare(b.getAllLikes(),a.getAllLikes());
    }
}
public class PostTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String postAuthor = sc.nextLine();
        String postContent = sc.nextLine();

        Post p = new Post(postAuthor, postContent);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(";");
            String testCase = parts[0];

            if (testCase.equals("addComment")) {
                String author = parts[1];
                String id = parts[2];
                String content = parts[3];
                String replyToId = null;
                if (parts.length == 5) {
                    replyToId = parts[4];
                }
                p.addComment(author, id, content, replyToId);
            } else if (testCase.equals("likes")) { //likes;1;2;3;4;1;1;1;1;1 example
                for (int i = 1; i < parts.length; i++) {
                    p.likeComment(parts[i]);
                }
            } else {
                System.out.println(p);
            }

        }
    }
}
