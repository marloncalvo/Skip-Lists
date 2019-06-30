public class Character
{
    public static void main(String [] args)
    {
        SkipList<String> s = new SkipList<>(6);
        
        s.insert("d");
        s.insert("e");
        s.insert("a");
        s.insert("b");
        s.insert("c");

        s.print();

        s.delete("d");
        s.delete("a");

        s.print();
    }
}
