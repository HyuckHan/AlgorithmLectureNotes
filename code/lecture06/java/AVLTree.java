import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Distinct int keys. Empty height=-1, leaf height=0, BF=left-right. */
public final class AVLTree {
    private static final class Node {int key,height;Node left,right;Node(int k){key=k;}}
    private Node root;private int size;
    private static int h(Node x){return x==null?-1:x.height;}
    private static void update(Node x){x.height=1+Math.max(h(x.left),h(x.right));}
    private static int bf(Node x){return h(x.left)-h(x.right);}
    public boolean contains(int k){Node x=root;while(x!=null){if(k==x.key)return true;x=k<x.key?x.left:x.right;}return false;}
    public boolean insert(int k){if(contains(k))return false;root=insert(root,k);size++;return true;}
    private static Node insert(Node x,int k){if(x==null)return new Node(k);if(k<x.key)x.left=insert(x.left,k);else x.right=insert(x.right,k);return rebalance(x);}
    public boolean delete(int k){if(!contains(k))return false;root=delete(root,k);size--;return true;}
    private static Node delete(Node x,int k){
        if(k<x.key)x.left=delete(x.left,k);else if(k>x.key)x.right=delete(x.right,k);
        else{if(x.left==null)return x.right;if(x.right==null)return x.left;Node y=x.right;while(y.left!=null)y=y.left;x.key=y.key;x.right=delete(x.right,y.key);}
        return rebalance(x);
    }
    private static Node rebalance(Node x){update(x);if(bf(x)>1){if(bf(x.left)<0)x.left=left(x.left);return right(x);}if(bf(x)<-1){if(bf(x.right)>0)x.right=right(x.right);return left(x);}return x;}
    private static Node right(Node y){Node x=y.left,b=x.right;x.right=y;y.left=b;update(y);update(x);return x;}
    private static Node left(Node x){Node y=x.right,b=y.left;y.left=x;x.right=b;update(x);update(y);return y;}
    public List<Integer> inorder(){List<Integer>o=new ArrayList<>();inorder(root,o);return List.copyOf(o);}
    private static void inorder(Node x,List<Integer>o){if(x!=null){inorder(x.left,o);o.add(x.key);inorder(x.right,o);}}
    public int height(){return h(root);}
    private record Check(boolean ok,int height){}
    public boolean validate(){return validate(root,Long.MIN_VALUE,Long.MAX_VALUE).ok&&inorder().size()==size;}
    private static Check validate(Node x,long lo,long hi){if(x==null)return new Check(true,-1);Check l=validate(x.left,lo,x.key),r=validate(x.right,x.key,hi);int e=1+Math.max(l.height,r.height);return new Check(l.ok&&r.ok&&x.key>lo&&x.key<hi&&x.height==e&&Math.abs(l.height-r.height)<=1,e);}
    public static void main(String[]args){
        for(int[]s:new int[][]{{30,20,10},{10,20,30},{30,10,20},{10,30,20}}){AVLTree t=new AVLTree();for(int k:s)assert t.insert(k)&&t.validate();assert t.inorder().equals(List.of(10,20,30))&&t.height()==1;}
        AVLTree t=new AVLTree();for(int i=0;i<200;i++)assert t.insert(i)&&t.validate();assert !t.insert(100);
        for(int i=0;i<200;i+=2)assert t.delete(i)&&t.validate();
        List<Integer>ks=new ArrayList<>();for(int i=200;i<400;i++)ks.add(i);Collections.shuffle(ks,new Random(6));for(int k:ks)assert t.insert(k)&&t.validate();assert !t.delete(-1);
    }
}
