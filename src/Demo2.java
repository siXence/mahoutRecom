import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.apache.mahout.cf.taste.impl.recommender.AbstractRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
//import org.apache.mahout.cf.taste.impl.recommender.slopeone.SlopeOneRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CityBlockSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.RandomUtils;


public class Demo2 {
	static List<Double> precisionUser = new ArrayList<Double>();
	static List<Double> recallUser = new ArrayList<Double>();
	static List<Double> userF1 = new ArrayList<Double>();
	static List<Double> precisionItem = new ArrayList<Double>();
	static List<Double> recallItem = new ArrayList<Double>();
	static List<Double> itemF1 = new ArrayList<Double>();
	static List<Double> precisionSVD = new ArrayList<Double>();
	static List<Double> recallSVD = new ArrayList<Double>();
	static List<Double> svdF1 = new ArrayList<Double>();
	
	private static List<List<Long>> recomPro = new ArrayList<List<Long>>();
	private static int Tu = 0;
	private static HashMap<Integer,HashMap<Long, Boolean> >  watchLists = new HashMap<Integer,HashMap<Long, Boolean> >();
	
	private static List<List<Integer>> userRecomProID = new ArrayList<List<Integer>>();
	private static List<List<Integer>> itemRecomProID = new ArrayList<List<Integer>>();
	private static List<List<Integer>> svdRecomProID = new ArrayList<List<Integer>>();
	
    public void userbased(DataModel model,int n) throws TasteException{
        System.out.println("-----------------------------------------------------------------------------");
        RandomUtils.useTestSeed();
        final int N=n;
        
        
//        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
//        UserSimilarity similarity=new CityBlockSimilarity(model);
        UserSimilarity similarity=new EuclideanDistanceSimilarity(model);

      //选择邻居用户，使用NearestNUserNeighborhood实现UserNeighborhood接口，选择邻近的4个用户
      UserNeighborhood neighborhood = new NearestNUserNeighborhood(N, similarity, model);

      Recommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);

      //给用户1推荐4个物品
      for (int i = 1; i <= 943; i++) {
//    	  System.out.println("-------User " + i + "------");
    	  List<RecommendedItem> recommendations = recommender.recommend(i, 100);
    	  List<Long> proIDs = new ArrayList<Long>();
          for (RecommendedItem recommendation : recommendations) {
//              System.out.println(recommendation.getItemID());
              proIDs.add(recommendation.getItemID());
          }
          if (i == 1) {
        	  System.out.println("recom 1 = " + proIDs);
          }
          recomPro.add(proIDs);
      }
//      System.out.println("recomPro = " + recomPro);
      //precision recall
      getPrecisionAndRecall(0);

        
        
        
        
//        RecommenderEvaluator evaluator=new RMSRecommenderEvaluator();
//        RecommenderIRStatsEvaluator statsEvaluator = new GenericRecommenderIRStatsEvaluator();
//        RecommenderBuilder builder=new RecommenderBuilder(){
//        @Override
//            public Recommender buildRecommender(DataModel model)
//                    throws TasteException {
//                // TODO Auto-generated method stub
//                UserSimilarity similarity=new PearsonCorrelationSimilarity(model);
////                UserSimilarity similarity=new CityBlockSimilarity(model);
////        	 	UserSimilarity similarity=new EuclideanDistanceSimilarity(model);
//                
//                UserNeighborhood neighborhood=new NearestNUserNeighborhood(N,similarity,model);
//                Recommender recommend=new GenericUserBasedRecommender(model,neighborhood,similarity);
//                return recommend;
//            }
//        };
//        List<RecommendedItem> recommendations =((AbstractRecommender) builder).recommend(1, 4);
//
//        for (RecommendedItem recommendation : recommendations) {
//            System.out.println(recommendation);
//        }
//        
//        double score=evaluator.evaluate(builder, null, model, 0.8, 1.0);
//        System.out.println("UserBased "+N+"  score is"+score);
//        
////        IRStatistics stats11 = statsEvaluator.evaluate(builder,null, model, null, 20,
////                GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD,1.0);
//////        IRStatistics stats11 = statsEvaluator.evaluate(builder,null, model, null, 30,
//////                0,1.0);
////        System.out.println(stats11.getPrecision());
////        System.out.println(stats11.getRecall());
//        
//        for (int i = 1; i <= 100; i++) {
//        	System.out.println("------"+i+"--------");
//        	 IRStatistics stats = statsEvaluator.evaluate(builder,null, model, null, i,
//                     GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD,1.0);
//             System.out.println("precision = " + stats.getPrecision());
//             System.out.println("recall = " + stats.getRecall());
//             System.out.println("caclulate F1 = " + 2*stats.getPrecision()*stats.getRecall()/(stats.getRecall() + stats.getPrecision()));
//             System.out.println("F1 = " + stats.getF1Measure());
//             System.out.println("reach = " + stats.getReach());
//        	 precisionUser.add(stats.getPrecision());
//        	 recallUser.add(stats.getRecall());
//        	 userF1.add(stats.getF1Measure());
//        }
        
       
    }
    
    public void itembased(DataModel model) throws TasteException{
        System.out.println("-----------------------------------------------------------------------------");

        
        RecommenderEvaluator evaluator=new RMSRecommenderEvaluator();
        ItemSimilarity similarity=new CityBlockSimilarity(model); 
//    	ItemSimilarity similarity=new EuclideanDistanceSimilarity(model); 
    	
        Recommender recommend=new GenericItemBasedRecommender(model,similarity);
      //给用户1推荐4个物品
        for (int i = 1; i <= 943; i++) {
//      	  System.out.println("-------User " + i + "------");
      	  List<RecommendedItem> recommendations = recommend.recommend(i, 100);
      	  List<Long> proIDs = new ArrayList<Long>();
            for (RecommendedItem recommendation : recommendations) {
//                System.out.println(recommendation.getItemID());
                proIDs.add(recommendation.getItemID());
            }
            if (i == 1) {
          	  System.out.println("recom 1 = " + proIDs);
            }
            recomPro.add(proIDs);
        }
//        System.out.println("recomPro = " + recomPro);
        //precision recall
        getPrecisionAndRecall(1);
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
//        RecommenderEvaluator evaluator=new RMSRecommenderEvaluator();
//        RecommenderBuilder builder=new RecommenderBuilder(){
//
//            @Override
//            public Recommender buildRecommender(DataModel model)
//                    throws TasteException {
//                // TODO Auto-generated method stub
//                return new SVDRecommender(model,new ALSWRFactorizer(model,10,0.05,10));
//            }
//            
//        };
        
        Recommender recommender = new SVDRecommender(model,new ALSWRFactorizer(model,300,0.005,20));
        //给用户1推荐4个物品
        for (int i = 1; i <= 943; i++) {
//      	  System.out.println("-------User " + i + "------");
      	  List<RecommendedItem> recommendations = recommender.recommend(i, 100);
      	  List<Long> proIDs = new ArrayList<Long>();
            for (RecommendedItem recommendation : recommendations) {
//                System.out.println(recommendation.getItemID());
                proIDs.add(recommendation.getItemID());
            }
            if (i == 1) {
          	  System.out.println("recom 1 = " + proIDs);
            }
            recomPro.add(proIDs);
        }
//        System.out.println("recomPro = " + recomPro);
        //precision recall
        getPrecisionAndRecall(2);
        
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
    
    
    public static void readTxtFile(String filePath){
    	System.out.println("read test data..................");
        try {
                String encoding="GBK";
                File file=new File(filePath);
                int lastUserID = 0;
                if(file.isFile() && file.exists()){ //判断文件是否存在
                    InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file),encoding);//考虑到编码格式
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null;
                    while((lineTxt = bufferedReader.readLine()) != null){
                    	Tu++;
//                        System.out.println(lineTxt);
                        String[] tmp = lineTxt.split("\t");
                        int userID = Integer.valueOf(tmp[0]);
                        if (userID != lastUserID) {
                        	lastUserID = userID;
                        	watchLists.put(lastUserID, new HashMap<Long, Boolean>());
                        }
                        long itemID = Integer.valueOf(tmp[1]);
                        watchLists.get(userID).put(itemID, true);
                    }
//                    System.out.println("watchlists = " + watchLists.get(1)  + ",  aaa = " + watchLists.get(462));
                    read.close();
        }else{
            System.out.println("找不到指定的文件");
        }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
     
    }
    
    
    public static void getPrecisionAndRecall(int method) {
    	System.out.println("getPrecisionAndRecall..................");
    	int cnt = 0;
    	for (int j = 1; j <= 100; j++) {
    		int userID = 0;
    		for (List<Long> rl : recomPro) {
    			userID++;
//    			if (j == 3) {
//    				if (userID == 1) {
//    					System.out.println("watchlist = " + watchLists.get(userID));
//    					System.out.println("item 3 , user 1 = " + rl.get(j-1) + ",  isContain = " + watchLists.get(userID).containsKey(rl.get(j-1)));
//    				}
//    			}
    			if (watchLists.containsKey(userID) ) {
//    				System.out.println("true");
    				if (j < rl.size()) {
    					if (watchLists.get(userID).containsKey(rl.get(j-1))) {
        					cnt++;
        				}
    				}
    			}
    		}
//    		System.out.println("cnt = " + cnt);
    		
    		if (method == 0) {
    			double p = ((double)cnt)/(j*943);
    			double r = ((double)cnt)/Tu;
    			precisionUser.add(p);
    			recallUser.add(r);
    			userF1.add(2*p*r/(p+r));
    		} else if (method == 1) {
    			double p = ((double)cnt)/(j*943);
    			double r = ((double)cnt)/Tu;
    			precisionItem.add(p);
    			recallItem.add(r);
    			itemF1.add(2*p*r/(p+r));
    		} else if (method == 2) {
    			double p = ((double)cnt)/(j*943);
    			double r = ((double)cnt)/Tu;
    			precisionSVD.add(p);
    			recallSVD.add(r);
    			svdF1.add(2*p*r/(p+r));
    		}
    	}
    }
    
    
    public static void main(String[] args) throws Exception{
//        String filepath="itemAll.txt";
//    	String filepath="u.csv";
//    	String filepath="ratings.txt";
    	String filepath="u1.base";
    	readTxtFile("u1.test");
    	
        DataModel model=new FileDataModel(new File(filepath));
        Demo2 demo=new Demo2();
        
//        
//        demo.userbased(model, 10);
//        write("precisionUserML.txt", precisionUser);
//        write("recallUserML.txt", recallUser);
//        write("F1UserML.txt", userF1);
        
//        write("precisionUserTW.txt", precisionUser);
//        write("recallUserTW.txt", recallUser);
  
//        demo.itembased(model);
//        write("precisionItemML.txt", precisionItem);
//        write("recallItemML.txt", recallItem);
//        write("F1ItemML.txt", itemF1);
//        
////        write("precisionItemTW.txt", precisionItem);
////        write("recallItemTW.txt", recallItem);
//        
////        demo.slope_one(model);
        demo.SVD(model);
        write("precisionSVDML.txt", precisionSVD);
        write("recallSVDML.txt", recallSVD);
      write("F1SVDML.txt", svdF1);
//        
////        write("precisionSVDTW.txt", precisionSVD);
////        write("recallSVDTW.txt", recallSVD);
        
        System.out.println("Tu = " + Tu);
        System.out.println("Done");
    }
}