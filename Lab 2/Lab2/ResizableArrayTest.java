package Lab2;

import java.util.Scanner;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Set;
@SuppressWarnings("unchecked")
class ResizableArray<T> {
    private T[] elements;
    private int size;
    public ResizableArray() {
        elements = (T[]) new Object[2];
    }
    @SuppressWarnings("unchecked")
    void addElement(T element) {
        if (size == elements.length) {
            T[] newElements = (T[]) new Object[elements.length * 2];
            System.arraycopy(elements, 0, newElements, 0, size);
            elements = newElements;
        }
        elements[size++] = element;
    }
    boolean removeElement(T element) {
        boolean removed = false;
        int index = this.indexOf(element);
        if(index != -1)
        {
            removed = true;
            for(int i=index;i<size-1;i++)
            {
                this.elements[i] = this.elements[i+1];
            }
            this.elements[this.size-1] = null;
            this.size--;
        }
        return removed;
    }

    boolean contains(T element) {
        for(int i = 0; i < size; i++) {
            if(this.elements[i].equals(element)) {
                return true;
            }
        }
        return false;
    }
    int indexOf(T element) {
        for(int i = 0; i < size; i++) {
            if(this.elements[i].equals(element)) {
                return i;
            }
        }
        return -1;
    }
    Object[] toArray() {
        Object[] array = new Object[this.size];
        System.arraycopy(this.elements, 0, array, 0, this.size);
        return array;
    }
    boolean isEmpty() {
        return this.size == 0;
    }
    int count() {
        return this.size;
    }
    T elementAt(int idx) {
        if(idx < 0 || idx >= this.size) {
            throw new ArrayIndexOutOfBoundsException("Index is out of bounds!");
        }
        else {
            return this.elements[idx];
        }
    }
    @SuppressWarnings("unchecked")
    static <T> void copyAll(ResizableArray<T> dest,ResizableArray<? extends T> src) {
        T[] newNiza = (T[]) new Object[dest.count()+src.count()];
        System.arraycopy(dest.elements, 0, newNiza, 0, dest.count());
        System.arraycopy(src.elements, 0, newNiza, dest.count(), src.count());
        dest.elements = newNiza;
        dest.size = dest.count()+src.count();
    }
}
class IntegerArray<T> extends ResizableArray<Integer> {
    public IntegerArray() {
        super();
    }
    double sum() {
        int sum = 0;
        for(int i=0;i<this.count();i++)
        {
            sum = sum + this.elementAt(i);
        }
        return sum;
    }
    double mean() {
        return sum()/this.count();
    }
    int countNonZero() {
        int counter = 0;
        for(int i=0;i<this.count();i++) {
            if(!this.elementAt(i).equals(0)) {
                counter++;
            }
        }
        return counter;
    }
    IntegerArray<T> distinct() {
        IntegerArray<T> nova = new IntegerArray<>();

        for (int i = 0; i < this.count(); i++) {
            int element = this.elementAt(i);
            boolean flag = false;

            for (int j = 0; j < nova.count(); j++) {
                if (nova.elementAt(j).equals(element)) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                nova.addElement(element);
            }
        }

        return nova;
    }
    IntegerArray<T> increment(int offset) {
        IntegerArray<T> nova = new IntegerArray<>();
        for(int i=0;i<this.count();i++) {
            nova.addElement(this.elementAt(i)+offset);
        }
        return nova;
    }
}



public class ResizableArrayTest {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int test = jin.nextInt();
        if ( test == 0 ) { //test ResizableArray on ints
            ResizableArray<Integer> a = new ResizableArray<Integer>();
            System.out.println(a.count());
            int first = jin.nextInt();
            a.addElement(first);
            System.out.println(a.count());
            int last = first;
            while ( jin.hasNextInt() ) {
                last = jin.nextInt();
                a.addElement(last);
            }
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(a.removeElement(first));
            System.out.println(a.contains(first));
            System.out.println(a.count());
        }
        if ( test == 1 ) { //test ResizableArray on strings
            ResizableArray<String> a = new ResizableArray<String>();
            System.out.println(a.count());
            String first = jin.next();
            a.addElement(first);
            System.out.println(a.count());
            String last = first;
            for ( int i = 0 ; i < 4 ; ++i ) {
                last = jin.next();
                a.addElement(last);
            }
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(a.removeElement(first));
            System.out.println(a.contains(first));
            System.out.println(a.count());
            ResizableArray<String> b = new ResizableArray<String>();
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
            System.out.println(b.removeElement(first));
            System.out.println(b.contains(first));
            System.out.println(b.removeElement(first));
            System.out.println(b.contains(first));

            System.out.println(a.removeElement(first));
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
        }
        if ( test == 2 ) { //test IntegerArray
            IntegerArray a = new IntegerArray();
            System.out.println(a.isEmpty());
            while ( jin.hasNextInt() ) {
                a.addElement(jin.nextInt());
            }
            jin.next();
            System.out.println(a.sum());
            System.out.println(a.mean());
            System.out.println(a.countNonZero());
            System.out.println(a.count());
            IntegerArray b = a.distinct();
            System.out.println(b.sum());
            IntegerArray c = a.increment(5);
            System.out.println(c.sum());
            if ( a.sum() > 100 )
                ResizableArray.copyAll(a, a);
            else
                ResizableArray.copyAll(a, b);
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.contains(jin.nextInt()));
            System.out.println(a.contains(jin.nextInt()));
        }
        if ( test == 3 ) { //test insanely large arrays
            LinkedList<ResizableArray<Integer>> resizable_arrays = new LinkedList<ResizableArray<Integer>>();
            for ( int w = 0 ; w < 500 ; ++w ) {
                ResizableArray<Integer> a = new ResizableArray<Integer>();
                int k =  2000;
                int t =  1000;
                for ( int i = 0 ; i < k ; ++i ) {
                    a.addElement(i);
                }

                a.removeElement(0);
                for ( int i = 0 ; i < t ; ++i ) {
                    a.removeElement(k-i-1);
                }
                resizable_arrays.add(a);
            }
            System.out.println("You implementation finished in less then 3 seconds, well done!");
        }
    }

}

