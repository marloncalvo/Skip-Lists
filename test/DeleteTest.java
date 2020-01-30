public class DeleteTest
{
	public static void main(String [] args)
	{
		SkipList<Integer> s = new SkipList<>();

		s.insert(5);
		s.insert(5);

		s.delete(5);

		s.print();

		s.insert(5, 2);
		s.insert(5, 2);

		s.insert(6, 1);
		s.insert(6, 2);
		s.insert(7, 1);
		s.insert(7, 2);

		s.print();

		s.delete(6);

		s.print();

		s.delete(7);

		s.print();
	}
}