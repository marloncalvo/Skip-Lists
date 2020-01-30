public class Size
{
    public static void main(String [] args)
    {
        SkipList<Integer> s = new SkipList<>();

        s.insert(1, 10);
        s.print();
        s.insert(2, 1);
        s.print();
        s.insert(3, 2);
        s.print();

        s.insert(2, 11);
        s.print();

        s.delete(2);
        s.print();

        System.out.println(s.contains(1));
    }
}

