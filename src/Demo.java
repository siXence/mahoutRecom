import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
//import org.apache.mahout.cf.taste.impl.recommender.slopeone.SlopeOneRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;


public class Demo {
	static List<Double> precisionUser = new ArrayList<Double>();
	static List<Double> recallUser = new ArrayList<Double>();
	static List<Double> precisionSVD = new ArrayList<Double>();
	static List<Double> recallSVD = new ArrayList<Double>();
	
    public void userbased(DataModel model,int n) throws TasteException{
        System.out.println("-----------------------------------------------------------------------------");
        final int N=n;
        RecommenderEvaluator evaluator=new RMSRecommenderEvaluator();
        RecommenderIRStatsEvaluator statsEvaluator = new GenericRecommenderIRStatsEvaluator();
        RecommenderBuilder bulider=new RecommenderBuilder(){
        @Override
            public Recommender buildRecommender(DataModel model)
                    throws TasteException {
                // TODO Auto-generated method stub
                UserSimilarity similarity=new PearsonCorrelationSimilarity(model);
                UserNeighborhood neighborhood=new NearestNUserNeighborhood(N,similarity,model);
                Recommender recommend=new GenericUserBasedRecommender(model,neighborhood,similarity);
                return recommend;
            }
        };
        double score=evaluator.evaluate(bulider, null, model, 0.8, 1.0);
        System.out.println("UserBased "+N+"  score is"+score);
        for (int i = 1; i <= 100; i++) {
        	 IRStatistics stats = statsEvaluator.evaluate(bulider,null, model, null, i,
                     GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD,1.0);
//             System.out.println(stats.getPrecision());
//             System.out.println(stats.getRecall());
        	 precisionUser.add(stats.getPrecision());
        	 recallUser.add(stats.getRecall());
        }
        
       
    }
    
    public void itembased(DataModel model) throws TasteException{
        System.out.println("-----------------------------------------------------------------------------");
        RecommenderEvaluator evaluator=new RMSRecommenderEvaluator();
        RecommenderBuilder builder=new RecommenderBuilder(){

            @Override
            public Recommender buildRecommender(DataModel model)
                    throws TasteException {
                // TODO Auto-generated method stub
                ItemSimilarity similarity=new PearsonCorrelationSimilarity(model); 
                Recommender recommend=new GenericItemBasedRecommender(model,similarity);
                return recommend;
            }
        };
        double score=evaluator.evaluate(builder, null, model, 0.8, 1.0);
        System.out.println("ItemBased score is "+score);
        
        RecommenderIRStatsEvaluator statsEvaluator = new GenericRecommenderIRStatsEvaluator();

	     // Evaluate precision and recall "at 2":
	     IRStatistics stats = statsEvaluator.evaluate(builder,
	             null, model, null, 35,
	             GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD,
	             1.0);
	     System.out.println(stats.getPrecision());
	     System.out.println(stats.getRecall());
    }
    
//    public void slope_one(DataModel model) throws TasteException{
//        System.out.println("-----------------------------------------------------------------------------");
//        RecommenderEvaluator evaluator=new RMSRecommenderEvaluator();
//        RecommenderBuilder builder=new RecommenderBuilder(){
//
//            @Override
//            public Recommender buildRecommender(DataModel arg0)
//                    throws TasteException {
//                // TODO Auto-generated method stub
//                return new SlopeOneRecommender(arg0);
//            }
//            
//        };
//        double score=evaluator.evaluate(builder, null, model, 0.7, 1);
//        System.out.println("Slope  one score is "+score);
//    }
    
    public void SVD(DataModel model) throws TasteException{
        System.out.println("-----------------------------------------------------------------------------");
        RecommenderEvaluator evaluator=new RMSRecommenderEvaluator();
        RecommenderBuilder builder=new RecommenderBuilder(){

            @Override
            public Recommender buildRecommender(DataModel model)
                    throws TasteException {
                // TODO Auto-generated method stub
                return new SVDRecommender(model,new ALSWRFactorizer(model,10,0.05,10));
            }
            
        };
        double score=evaluator.evaluate(builder, null, model, 0.8, 1.0);
        System.out.println("SVD score is "+score);
        
        RecommenderIRStatsEvaluator statsEvaluator = new GenericRecommenderIRStatsEvaluator();

        for (int i = 1; i <= 100; i++) {
       	 IRStatistics stats = statsEvaluator.evaluate(builder,null, model, null, i,
                    GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD,1.0);
//            System.out.println(stats.getPrecision());
//            System.out.println(stats.getRecall());
       	 precisionSVD.add(stats.getPrecision());
       	 recallSVD.add(stats.getRecall());
       }
        
    }
    
    
    /**
     * 写入文件
    *
     * @param writePath
     * 文件路径
    */
    public static void write(String writePath, List<Double> list) {
	    try {
		    File f = new File(writePath);
		    if (!f.exists()) {
		    	f.createNewFile();
		    }
			    OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(f),"UTF-8");
			    BufferedWriter writer = new BufferedWriter(write);
			    int userID = 1;
			    for (Double ele:list) {
			    	String tmp = String.valueOf(userID++) + "," + String.valueOf(ele) + "\r\n";
			    	writer.write(tmp);
			    }
			    writer.close();
		} catch (Exception e) {
		    e.printStackTrace();
	    }
    }
    public static void main(String[] args) throws Exception{
        String filepath="itemAll.txt";
        DataModel model=new FileDataModel(new File(filepath));
        Demo demo=new Demo();
        demo.userbased(model, 10);
        write("precisionUser.txt", precisionUser);
        write("recallUser.txt", recallUser);
//        demo.itembased(model);
//        demo.slope_one(model);
        demo.SVD(model);
        write("precisionSVD.txt", precisionSVD);
        write("recallSVD.txt", recallSVD);
        
        System.out.println("Done");
    }
}