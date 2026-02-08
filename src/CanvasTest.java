import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Float.parseFloat;

public class CanvasTest {

    public static void main(String[] args) {
        Canvas canvas = new Canvas();

        System.out.println("READ SHAPES AND EXCEPTIONS TESTING");
        canvas.readShapes(System.in);

        System.out.println("BEFORE SCALING");
        canvas.printAllShapes(System.out);
        canvas.scaleShapes("123456", 1.5);
        System.out.println("AFTER SCALING");
        canvas.printAllShapes(System.out);

        System.out.println("PRINT BY USER ID TESTING");
        canvas.printByUserId(System.out);

        System.out.println("PRINT STATISTICS");
        canvas.statistics(System.out);
    }
}

class Canvas {
    List<Shape> shapesList;
    public Canvas() {
        shapesList = new ArrayList<>();
    }
    public void readShapes(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while (true) {
                try {
                    String l = br.readLine();
                    if (l == null)
                        break;
                    if (l.trim().isEmpty()) return;
                    String[] parts = l.split("\\s+");
                    if (parts[0].equals("1")) {
                        Circle shape = new Circle(parts[1], parseFloat(parts[2]));
                        shapesList.add(shape);
                    } else if (parts[0].equals("2")) {
                        Square shape = new Square(parts[1], parseFloat(parts[2]));
                        shapesList.add(shape);
                    } else {
                        Rectangle shape = new Rectangle(parts[1], parseFloat(parts[2]), parseFloat(parts[3]));
                        shapesList.add(shape);
                    }
                }
                catch(InvalidIdException e) {
                    System.out.println(e.getMessage());
            }
                catch (InvalidDimensionException e) {
                    System.out.println(e.getMessage());
                    break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
    }

    public void scaleShapes(String userId, double coef) {
        shapesList.stream().filter(s -> s.getId().equals(userId))
                .forEach(s-> s.scale(coef));
    }

    void printAllShapes(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        List<Shape> sorted = new ArrayList<>(shapesList);
        sorted.sort(Comparator.comparing(Shape::getArea));
        sorted.forEach(pw::println);
        pw.flush();
    }

    public void printByUserId(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        shapesList.stream().collect(
                Collectors.groupingBy(
                        Shape::getId,
                        HashMap::new,
                        Collectors.toCollection(() -> new TreeSet<Shape>(Comparator.comparing(Shape::getPerimeter)))
                )
        ).entrySet().stream().sorted(
                Comparator.comparing((Map.Entry<String,TreeSet<Shape>> e) -> e.getValue().size(), Comparator.reverseOrder())
                        .thenComparingDouble(entry -> entry.getValue().stream().mapToDouble(Shape::getArea).sum())
        ).forEach( e -> {
            pw.println("Shapes of user: " + e.getKey());
            e.getValue().forEach(pw::println);
                }
        );
        pw.flush();
    }

    public void statistics(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        DoubleSummaryStatistics shapeStats = shapesList.stream().collect(Collectors.summarizingDouble(Shape::getArea));
        pw.println(String.format("count: %d",shapeStats.getCount()));
        pw.println(String.format("sum: %.2f",shapeStats.getSum()));
        pw.println(String.format("min: %.2f",shapeStats.getMin()));
        pw.println(String.format("average: %.2f",shapeStats.getAverage()));
        pw.println(String.format("max: %.2f",shapeStats.getMax()));
        pw.flush();
    }

}

abstract class Shape {
    private String id;
    public Shape(String id) throws InvalidIdException {
        if(!id.matches("[A-Za-z0-9]{6}")) {
            throw new InvalidIdException(id);
        }
        this.id = id;
    }
    public String getId() { return this.id; }
    abstract public void scale(double coef);
    abstract public double getArea();
    @Override
    public String toString() {
        return this.id;
    }
    abstract public double getPerimeter();
}

class Square extends Shape {
    private double side;
    public Square(String id, double side) throws InvalidDimensionException, InvalidIdException {
        super(id);
        if(side == 0) {
            throw new InvalidDimensionException();
        }
        this.side = side;
    }
    public double getSide() {
        return this.side;
    }

    @Override
    public void scale(double coef) {
        this.side = this.side*coef;
    }

    @Override
    public double getArea() {
        return this.side*this.side;
    }

    @Override
    public String toString() {
        return String.format("Square: -> Side: %.2f Area: %.2f Perimeter: %.2f",side,getArea(),getPerimeter());
    }

    @Override
    public double getPerimeter() {
        return 4*this.side;
    }
}

class Circle extends Shape {
    private double radius;
    public Circle(String id, double radius) throws InvalidDimensionException, InvalidIdException {
        super(id);
        if(radius == 0.0) {
            throw new InvalidDimensionException();
        }
        this.radius = radius;
    }
    public double getRadius() {
        return this.radius;
    }

    @Override
    public void scale(double coef) {
        this.radius = this.radius*coef;
    }

    @Override
    public double getArea() {
        return this.radius*this.radius*Math.PI;
    }

    @Override
    public String toString() {
        return String.format("Circle -> Radius: %.2f Area: %.2f Perimeter: %.2f",radius,getArea(),getPerimeter());
    }

    @Override
    public double getPerimeter() {
        return 2*this.radius*Math.PI;
    }
}

class Rectangle extends Shape {
    private double height;
    private double width;
    public Rectangle(String id, double height, double width) throws InvalidDimensionException, InvalidIdException {
        super(id);
        this.height = height;
        this.width = width;
        if(height == 0.0 || width == 0.0) {
            throw new InvalidDimensionException();
        }
    }
    public double getHeight() { return this.height; }
    public double getWidth() { return this.width; }

    @Override
    public void scale(double coef) {
        this.height = this.height*coef;
        this.width = this.width*coef;
    }

    @Override
    public double getArea() {
        return this.width*this.height;
    }

    @Override
    public String toString() {
        return String.format("Rectangle: -> Sides: %.2f, %.2f Area: %.2f Perimeter: %.2f", BigDecimal.valueOf(height)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue(),
                BigDecimal.valueOf(width)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue(),getArea(),getPerimeter());
    }

    @Override
    public double getPerimeter() {
        return 2*this.height + 2*this.width;
    }
}

class InvalidDimensionException extends Exception {
    public InvalidDimensionException() {
        super("Dimension 0 is not allowed!");
    }
    public String getMessage() {
        return "Dimension 0 is not allowed!";
    }
}

class InvalidIdException extends Exception {
    String id;
    public InvalidIdException(String id) {
        super(String.format("ID %s is not valid",id));
        this.id = id;
    }
    public String getMessage() {
        return String.format("ID %s is not valid", id);
    }
}