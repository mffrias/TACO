package girish.github.heapbenchmarks;

//@nullable_by_default;
public class BNode
{
    //key to min
    public int key;

    // data for vertex
    public int vertex;

    //parent node
    public /*@nullable@*/ BNode p;

    //pointer to first child
    public /*@nullable@*/ BNode child;

    //pointer to sibling
    public /*@nullable@*/ BNode sibling;

    //no of children
    public int degree;

    public BNode(){}
    
    public BNode(int vertex, int weight)
    {
        key = weight;
        this.vertex = vertex;
        p = null;
        child = null;
        sibling = null;
        degree = 0;
    }
}