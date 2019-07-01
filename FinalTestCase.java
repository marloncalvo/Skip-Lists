public class FinalTestCase
{
	public static void main(String [] args)
	{
		SkipList<Integer> l = new SkipList<>(2);

		l.insert(1);

		l.print();

		l.insert(2);

		l.print();

		l.insert(3, 2);

		l.print();

		l.insert(4);

		l.print();

		l.insert(5);

		l.print();
	}
}