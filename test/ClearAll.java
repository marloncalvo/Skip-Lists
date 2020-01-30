import java.util.HashSet;

public class ClearAll
{

    public static void fillSet(HashSet<Integer> hs)
    {
        for (int i = 0; i < 1000000; i++)
        {
            if (!hs.add((int)(Math.random() * 1000000 + 1)))
            {
                i--;
            }
        }
    }

    public static void main(String [] args)
    {
        SkipList<Integer> s = new SkipList<>();
        HashSet<Integer> hs = new HashSet<>();

        System.out.println("Filling array");
        fillSet(hs);

        int count = 1;
        for (int l : hs)
        {
            System.out.println("Inserting element number " + count++);
            s.insert(l);
        }

        count = 1;
        for (int l : hs)
        {
            System.out.println("Deleting element number " + count++);
            s.delete(l);
        }

        s.print();
    }
}
