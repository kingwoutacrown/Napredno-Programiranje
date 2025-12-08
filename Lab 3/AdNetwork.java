import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

// todo: complete the implementation of the Ad, AdRequest, and AdNetwork classes
class Ad implements Comparable<Ad>{
    private String id;
    private String category;
    private double bidValue;
    private double ctr;
    private String content;
    private double totalScore;

    public Ad(String id, String category, double bidValue, double ctr, String content) {
        this.id = id;
        this.category = category;
        this.bidValue = bidValue;
        this.ctr = ctr;
        this.content = content;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getBidValue() {
        return this.bidValue;
    }

    public void setBidValue(double bidValue) {
        this.bidValue = bidValue;
    }

    public double getCtr() {
        return this.ctr;
    }

    public void setCtr(double ctr) {
        this.ctr = ctr;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    @Override
    public int compareTo(Ad o) {
        if(this.bidValue ==  o.bidValue) {
            return this.getId().compareTo(o.getId());
        }
        else return Double.compare(o.bidValue, this.bidValue);
    }
    @Override
    public String toString() {
        return String.format("%s %s (bid=%.2f, ctr=%.2f%%) %s",id,category,bidValue,ctr*100,content);
    }
}
class AdRequest {
    private String id;
    private String category;
    private double floorBid;
    private String keywords;

    public AdRequest(String id, String category, double floorBid, String keywords) {
        this.id = id;
        this.category = category;
        this.floorBid = floorBid;
        this.keywords = keywords;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getFloorBid() {
        return floorBid;
    }

    public void setFloorBid(double floorBid) {
        this.floorBid = floorBid;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
    @Override
    public String toString() {
        return String.format("%s [%s] (floor=%f): %s",id,category,floorBid,keywords);
    }
}

class AdNetwork {
    ArrayList<Ad> ads;
    private String lastLine = null;
    public AdNetwork() {
        ads = new ArrayList<>();
    }
    void readAds(BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine())!=null) {
            if(line.isEmpty())
                continue;
            if(line.startsWith("AR"))
            {
                this.lastLine = line;
                break;
            }
            line = line.trim();
            String[] parts = line.split("\\s+",5);
            if (parts.length < 5) continue;
            String id = parts[0];
            String category = parts[1];
            double bidValue = Double.parseDouble(parts[2]);
            double ctr = Double.parseDouble(parts[3]);
            String contents =  parts[4];
            ads.add(new Ad(id,category,bidValue,ctr,contents));
        }
    }
    public List<Ad> placeAds(BufferedReader br,int k,PrintWriter pw) throws IOException {
        String line;
        if(lastLine!=null)
        {
            line = lastLine;
            lastLine = null;
        }
        else line = br.readLine();
        if(line.isEmpty())
            return null;
        line = line.trim();
        String[] parts= line.split("\\s+",4);
        String id = parts[0];
        String category = parts[1];
        double FloorBid = Double.parseDouble(parts[2]);
        String keywords = parts[3];
        AdRequest adRequest = new AdRequest(id,category,FloorBid,keywords);
        List<Ad> adList;
        adList = ads.stream().filter(ad->ad.getBidValue() >= adRequest.getFloorBid()).collect(Collectors.toList());
        adList.forEach(ad-> {
            double totalScore;
            totalScore = relevanceScore(ad,adRequest)+5.0*ad.getBidValue()+100.0*ad.getCtr();
            ad.setTotalScore(totalScore);
        });
        List<Ad> lastList = adList.stream().sorted(Comparator.comparingDouble(Ad::getTotalScore).reversed()).limit(k).sorted().collect(Collectors.toList());
        pw.printf("Top ads for request %s:%n", adRequest.getId());
        lastList.forEach(pw::println);
        pw.flush();
        return lastList;
    }
    private int relevanceScore(Ad ad, AdRequest req) {
        int score = 0;
        if (ad.getCategory().equalsIgnoreCase(req.getCategory())) score += 10;
        String[] adWords = ad.getContent().toLowerCase().split("\\s+");
        String[] keywords = req.getKeywords().toLowerCase().split("\\s+");
        for (String kw : keywords) {
            for (String aw : adWords) {
                if (kw.equals(aw)) score++;
            }
        }
        return score;
    }
}

public class Main {
    public static void main(String[] args) throws IOException {
        AdNetwork network = new AdNetwork();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out));

        int k = Integer.parseInt(br.readLine().trim());

        if (k == 0) {
            network.readAds(br);
            network.placeAds(br, 1, pw);
        } else if (k == 1) {
            network.readAds(br);
            network.placeAds(br, 3, pw);
        } else {
            network.readAds(br);
            network.placeAds(br, 8, pw);
        }

        pw.flush();
    }
}
