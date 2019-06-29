import java.util.ArrayList;
import java.util.Collections;

class Node<T>
{
    private T data;
    private int height;
    private ArrayList<Node<T>> skips;
    
    Node(int height)
    {
        this.height = height;
        this.skips = new ArrayList<>(Collections.nCopies(height, null));
    }
    
    Node(T data, int height)
    {
        this.data = data; 
        this.height = height;
        this.skips = new ArrayList<>(Collections.nCopies(height, null));
    }
    
    public T value()
    {
        return this.data;
    }
    
    public int height()
    {
        return this.height;
    }
    
    public Node<T> next(int level)
    {
        if (level < 0 || level > this.height() - 1)
        {
            return null;
        }
        
        return this.skips.get(level);
    }
    
    public void setNext(int level, Node<T> node)
    {        
        if (level > this.skips.size() - 1)
        {
            this.skips.add(node);
        }
        else
        {
            this.skips.set(level, node);
        }
    }
    
    public void grow()
    {
        this.height++;
        this.skips.add(null);
    }
    
    public boolean maybeGrow()
    {
        if (Math.random() > 0.5)
        {
            grow();
            return true;
        }
        
        return false;
    }
    
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
    
    SkipList()
    {
        root = new Node<T>(1);
    }
    
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
    
    public int size()
    {
        return this.size;
    }
    
    public int height()
    {
        return this.root.height();
    }
    
    public Node<T> head()
    {
        return this.root;
    }
    
    int counter = 0;
    public void insert(T data)
    {

        int height = generateRandomHeight(getMaxHeight(size()));
        insert(data, height);
//        System.out.println(counter++);
    }
    public void insert(T data, int height)
    {
        Node<T> itr = root;
        Node<T> nNode = new Node<T>(data, height);

        ArrayList<Node<T>> nodesToLink = new ArrayList<>(height);

        int temp = Math.min(height, this.height());
        for (int i = 0; i < temp; i++)
        {
            nodesToLink.add(root);
            nNode.setNext(i, root.next(i));
        }

        int level = root.height() - 1;

        while(level > -1)
        {
            if (itr.next(level) == null || data.compareTo(itr.next(level).value()) <= 0)
            {
                level--;
                continue;
            }
            
            itr = itr.next(level);
            level = itr.height() - 1;
            int adjLevels = Math.min(height, itr.height());

            for (int i = 0; i < adjLevels; i++)
            {
                nNode.setNext(i, itr.next(i));
                try
                {
                    nodesToLink.set(i, itr);
                } catch (IndexOutOfBoundsException e)
                {
                    nodesToLink.add(itr);
                }
            }
        }

        for (int i = 0; i < height; i++)
        {
            try {
                nodesToLink.get(i).setNext(i, nNode);
            } catch (IndexOutOfBoundsException e) {}
        }

        size++;
        
        if (height() < getMaxHeight(size))
        {
            growSkipList();
        }
    }
    
    public void delete(T data)
    {
        // assuming max height is of root, if we encounter a larger node then size will increase
        ArrayList<Node<T>> nodesToLink = new ArrayList<>(Collections.nCopies(this.height(), this.head()));
        Node<T> itr = this.head();

        int level = this.height() - 1;
        while(level > -1)
        {
            if (itr.next(level) == null || data.compareTo(itr.next(level).value()) <= 0)
            {
                level--;
                continue;
            }

            itr = itr.next(level);
            level = itr.height() - 1;

            for (int i = 0; i < itr.height(); i++)
            {
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

        itr = itr.next(0);

        for (int i = 0; i < itr.height(); i++)
        {
            try
            {
            nodesToLink.get(i).setNext(i, itr.next(i));
            } catch (IndexOutOfBoundsException e) {}
        }

        this.size--;

        if (this.height() > this.getMaxHeightDel(size))
        {
            this.trimSkipList(this.getMaxHeightDel(size));
        }
    }
    
    public boolean contains(T data)
    {
        Node<T> itr = this.head();
        int level = this.height() - 1;
        
        while(level > -1)
        {
            if (itr.next(level) == null || data.compareTo(itr.next(level).value()) < 0)
            {
                level--;
                continue;
            }
                        
            itr = itr.next(level);
            level = itr.height() - 1;

            if (data.compareTo(itr.value()) == 0)
            {
                return true;
            }
        }
        
        return false;
    }
    
    public Node<T> get(T data)
    {
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
    
    private int getMaxHeight(int n)
    {
        int calcMax = (int) Math.ceil(Math.log10(n)/Math.log10(2));
        return (this.height() > calcMax ? this.height() : calcMax > 0 ? calcMax : 1);
    }

    private int getMaxHeightDel(int n)
    {
        int calcMax = (int) Math.ceil(Math.log10(n)/Math.log10(2));
        return (calcMax > 0 ? calcMax : 1);
    }
    
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
    
    private void growSkipList()
    {
        int oldLevel = this.height() - 1;
        int newLevel = oldLevel + 1;

        this.head().grow();

        Node<T> itr = this.head().next(oldLevel);
        Node<T> prevMax = this.head();

        while (itr != null)
        {  
            if (itr.height() == this.height() || itr.maybeGrow() == true)
            {
                prevMax.setNext(newLevel, itr);
                prevMax = itr;
            }

            itr = itr.next(oldLevel);
        }
    }
    
    public void print()
        {
                Node<T> itr = root;

                int levels = root.height();
                System.out.println("====================================");
                System.out.println("Printing list current status");
                for (int i = levels - 1; i > -1; i--)
                {
                        itr = root;
                        while (itr != null)
                        {
                                Node<T> next = itr.next(i);
                                System.out.printf("%-15s", (next == null ? "null" : next.value()));
                                itr = itr.next(0);
                        }
                        System.out.println();
                }

                itr = root;
                while (itr != null)
                {
                        System.out.printf("%-15s", itr.value());
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
    
    
        
