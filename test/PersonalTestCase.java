public class PersonalTestCase
{
    public static void main(String [] args)
    {
        SkipList<Integer> list = new SkipList<>(2);

        list.insert(1, 3);
        list.insert(2, 1);
        list.insert(3, 2);
        list.insert(4, 1);
        list.insert(5, 2);

        list.print();

        list.delete(3);

        list.print();
    }
}
