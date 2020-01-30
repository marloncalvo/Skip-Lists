import java.util.TreeMap;

public class SkipListDestroyer
{
    public static void main(String [] args)
    {
        destroyer1();
    }
    // we need to check for expected size, height and properly assigned heights
    // we should add between numbers
    public static boolean destroyer1()
    {
        SkipList<Integer> s = new SkipList<>(3);

        s.insert(1,1);
        s.insert(2,1);
        s.insert(3,1);
        s.insert(4,12);
        s.insert(5,13);
        s.insert(6,12);

        s.delete(4);

        s.print();

        return true;
    }
}