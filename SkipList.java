import java.util.ArrayList;
import java.util.Collections;

class Node<T>
{
    private T data;
    private int height;
    private ArrayList<Node<T>> skips;
    
    // These constructors fill the ArrayList with null values to simplify code
    // Without data, it is meant for root values
    Node(int height)
    {
        this.height = height;
        this.skips = new ArrayList<>(Collections.nCopies(height, null));
    }
    
    // This one is utilized for nodes
    Node(T data, int height)
    {
        this.data = data; 
        this.height = height;
        this.skips = new ArrayList<>(Collections.nCopies(height, null));
    }
    
    // Returns passed value of Node, root will return null
    public T value()
    {
        return this.data;
    }
    
    // Will return the current height of the node
    public int height()
    {
        return this.height;
    }
    
    // Grabs the Node associated with the specified element in the Skip List (pointer to next list)
    public Node<T> next(int level)
    {
        // Check if out of bounds
        if (level < 0 || level > this.height() - 1)
        {
            return null;
        }
        
        return this.skips.get(level);
    }
    

    // Replaces specified element with specified value
    public void setNext(int level, Node<T> node)
    {        
        this.skips.set(level, node);
    }
    
    // Increases size of the Node by 1
    public void grow()
    {
        this.height++;
        this.skips.add(null);
    }
    
    // Helper function to grow Node probalistically
    public boolean maybeGrow()
    {
        if (Math.random() > 0.5)
        {
            grow();
            return true;
        }
        
        return false;
    }
    
    // Reduces height of the Node to specified height
    public void trim(int height)
    {
        for (int i = this.height - 1; i > height - 1; i--)
        {
            this.skips.remove(i);
        }

        this.height = height;
    }

    @Override
    public String toString()
    {
        return this.value().toString();
    }
}

public class SkipList<T extends Comparable<T>>
{
    private int size;
    private Node<T> root;
    
    // Creates a root Node of height 1
    SkipList()
    {
        root = new Node<T>(1);
    }
    
    // Creates a root Node of height specified
    SkipList(int height)
    {
        if (height < 1)
        {
            root = new Node<T>(1);
        }
        else
        {
            root = new Node<T>(height);
        }
    }
    
    // Size is the amount of elements inserted
    public int size()
    {
        return this.size;
    }
    
    // Height is the height of root
    public int height()
    {
        return this.root.height();
    }
    
    // Returns root node
    public Node<T> head()
    {
        return this.root;
    }
    
    // Inserts value into list in ascending order, height probalistically generated if none specified
    public void insert(T data)
    {
        int height = generateRandomHeight(getMaxHeight(size()));
        insert(data, height);
    }

    // Value inserted into list with specified height, can be larger than the size of root itself.
    // If so, the list will properly associate elements with any size, but Node will not grow until
    // root size is greater than it.
    public void insert(T data, int height)
    {
        Node<T> itr = root;
        Node<T> nNode = new Node<T>(data, height);

        // Search for closest element to data, and insert that element there
        for (int level = this.height() - 1; level >= 0; level--)
        {
            // If we are before the element, we keep searching. We must also keep searching if we encounter
            // a duplicate item (same as the one we are searching for)
            if (itr.next(level) == null || data.compareTo(itr.next(level).value()) <= 0)
            {
                // Continually set each prior's node Next to this new Node
                // It will only reach here once it is between "two columns", or "its section"
                if (level < nNode.height())
                {
                    nNode.setNext(level, itr.next(level));
                    itr.setNext(level, nNode);
                }

                continue;
            }
            
            // Move iterator up to closer element
            itr = itr.next(level);
            level = itr.height();
        }
        
        size++;
        
        // Check if we need to grow the list size
        if (height() < getMaxHeight(size))
        {
            growSkipList();
        }
    }
     
    public void delete(T data)
    {
        // assuming max height is of root, if we encounter a larger node then size will increase
        // this list holds all the Nodes whose value needs to be changed
        ArrayList<Node<T>> nodesToLink = new ArrayList<>(Collections.nCopies(this.height(), this.head()));
        Node<T> itr = this.head();

        for (int level = this.height() - 1; level >= 0; level--)
        {
            // We continue on until we find the closest element to the Node we need to delete,
            // we must also skip past duplicate elements.
            if (itr.next(level) == null || data.compareTo(itr.next(level).value()) <= 0)
            {
                continue;
            }

            // We need to reach next closest element before we fill the link arraylist
            itr = itr.next(level);
            level = itr.height();

            // We fill the list with the "index" implicitley, and what Node must be "fixed"
            for (int i = 0; i < itr.height(); i++)
            {
                // This checks for Nodes that are currently higher than the Root height
                try
                {
                    nodesToLink.set(i, itr);
                }
                catch (IndexOutOfBoundsException e)
                {
                    nodesToLink.add(itr);
                }
            }
        }

        // we fell off list and didnt find the element
        if (itr.next(0) == null || data.compareTo(itr.next(0).value()) != 0)
        {
            return;
        }

        // We are now on the Node to delete
        itr = itr.next(0);

        for (int i = 0; i < itr.height(); i++)
        {
            // Checks if the Node to delete is higher than the root size, then we must avoid
            // those Nodes. It is setup like this because there can be another Node larger than root
            // who is shorther than the Node to delete, and we would not have that height.
            try {nodesToLink.get(i).setNext(i, itr.next(i));} 
            catch (IndexOutOfBoundsException e) {}
        }

        this.size--;

        // Check if list must be trimmed, size will be reduced to optimal height
        if (this.height() > this.getMaxHeightDel(size))
        {
            this.trimSkipList(this.getMaxHeightDel(size));
        }
    }

    // Calls get function and checks if it is null or not
    public boolean contains(T data)
    {
        Node<T> check = this.get(data);

        if (check == null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    // Finds first instance of Data in Node List
    public Node<T> get(T data)
    {
        Node<T> itr = this.head();
        
        // We will search for the first instance of Data we find in a Node.
        // Loop will return true if it ever finds the Data. If loops does not return,
        // it means that the data was not in the list.
        for (int level = this.height() - 1; level >= 0; level--)
        {
            if (itr.next(level) == null || data.compareTo(itr.next(level).value()) < 0)
            {
                continue;
            }
                        
            itr = itr.next(level);
            level = itr.height();

            if (data.compareTo(itr.value()) == 0)
            {
                return itr;
            }
        }
        
        // We did not find Data
        return null;
    }
    
    public static double difficultyRating()
    {
        return 4.0;
    }
    
    public static double hoursSpent()
    {
        return 20;
    }
    
    // Gets maxHeight for a certain size of list, it returns the highest of
    // current height and caculated size for instances where custom initial height is supplied
    private int getMaxHeight(int n)
    {
        // Max Height = cieling of log(n)/log(2)
        int calcMax = (int) Math.ceil(Math.log10(n)/Math.log10(2));
        return (this.height() > calcMax ? this.height() : calcMax > 0 ? calcMax : 1);
    }

    // Same as getMaxHeight, but we do not take into account the initial height supplied
    private int getMaxHeightDel(int n)
    {
        int calcMax = (int) Math.ceil(Math.log10(n)/Math.log10(2));
        return (calcMax > 0 ? calcMax : 1);
    }
    
    // Generates probabilistically, the height for a certain Node. 
    // Height restricted by maxHeight parameter
    private static int generateRandomHeight(int maxHeight)
    {
        int height = 1;
        while (height < maxHeight)
        {
            if (Math.random() > 0.5)
            {
                height++;
                continue;
            }
            
            break;
        }
    
        return height;
    }

    // Trims all the Nodes in list whose height is greater than the argument supplied
    private void trimSkipList(int height)
    {
        Node<T> itr = this.head();
        Node<T> curr = this.head();

        while (itr != null)
        {
            itr = itr.next(height);
            curr.trim(height);

            curr = itr;
        }
    }
    
    // Increases the size of list by one, and probabilistically increasing size
    // of any Node who's size was equal to the root height
    private void growSkipList()
    {
        int oldLevel = this.height() - 1;
        int newLevel = oldLevel + 1;

        this.head().grow();

        Node<T> itr = this.head().next(oldLevel);
        Node<T> prevMax = this.head();

        while (itr != null)
        {  
            // check if current Node height is same as previous root height
            // if we grow, we want re-organize list
            if (itr.height() >= this.height() || itr.maybeGrow() == true)
            {
                prevMax.setNext(newLevel, itr);
                prevMax = itr;
            }

            itr = itr.next(oldLevel);
        }
    }
    
    // Helper function to print the current status of the SkipList
    public void print()
    {
        Node<T> itr = root;

        int levels = root.height();

        int max = 0;
        while (itr != null)
        {
            if (itr.height() > max)
            {
                max = itr.height();
            }

            itr = itr.next(0);
        }

        System.out.println("====================================");
        System.out.println("Printing list current status");
        int index = 0;
        for (int i = max - 1; i > -1; i--)
        {
                itr = root;
                while (itr != null)
                {
                        Node<T> next = itr.next(i);
                        System.out.printf("%-15s", 
                                 (next == null ? ((i) < itr.height() ? "----" : "    ") : next.value()));
                        itr = itr.next(0);
                }
                System.out.println();
        }

        itr = root;
        while (itr != null)
        {
                System.out.printf("%-15s", ((index++ == 0) ? "root" : itr.value()));
                itr = itr.next(0);
        }

        System.out.println();

        itr = root;
        while (itr != null)
        {
                System.out.printf("%-15s", "-");
                itr = itr.next(0);
        }

        System.out.println();

        itr = root;
        while (itr != null)
        {
                System.out.printf("%-15d", itr.height());
                itr = itr.next(0);
        }

        System.out.println("\n====================================");

    }
}
