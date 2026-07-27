public final class MaximumSubarray {
    public record Result(long sum,int start,int end){}
    private MaximumSubarray(){}
    private static int len(Result r){return r.end()-r.start()+1;}
    private static boolean better(Result a,Result b){return a.sum()>b.sum()||(a.sum()==b.sum()&&(len(a)<len(b)||(len(a)==len(b)&&a.start()<b.start())));}
    // Nonempty, 0-based inclusive interval. Theta(n) time, Theta(1) space.
    public static Result solve(long[] a){
        if(a==null||a.length==0)throw new IllegalArgumentException();
        Result ending=new Result(a[0],0,0),best=ending;
        for(int i=1;i<a.length;i++){Result extend=new Result(Math.addExact(ending.sum(),a[i]),ending.start(),i),restart=new Result(a[i],i,i);ending=better(restart,extend)?restart:extend;if(better(ending,best))best=ending;}
        return best;
    }
    public static void main(String[] args){
        Result r=solve(new long[]{-2,1,-3,4,-1,2,1,-5,4});assert r.sum()==6&&r.start()==3&&r.end()==6;
        r=solve(new long[]{-8,-3,-6,-2,-5,-4});assert r.sum()==-2&&r.start()==3;
        assert solve(new long[]{7}).sum()==7;
        try{solve(new long[0]);assert false;}catch(IllegalArgumentException expected){}
    }
}
