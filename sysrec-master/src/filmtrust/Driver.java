package test1;

import net.librec.conf.Configuration;
import net.librec.data.model.TextDataModel;
import net.librec.eval.EvalContext;
import net.librec.eval.RecommenderEvaluator;
import net.librec.eval.ranking.NormalizedDCGEvaluator;
import net.librec.eval.rating.RMSEEvaluator;
import net.librec.filter.GenericRecommendedFilter;
import net.librec.filter.RecommendedFilter;
import net.librec.job.RecommenderJob;
import net.librec.recommender.MatrixFactorizationRecommender;
import net.librec.recommender.Recommender;
import net.librec.recommender.RecommenderContext;
import net.librec.recommender.cf.ItemKNNRecommender;
import net.librec.recommender.cf.ranking.BPRRecommender;
import net.librec.recommender.item.RecommendedItem;
import net.librec.similarity.CosineSimilarity;
import net.librec.similarity.RecommenderSimilarity;
import net.librec.spark.Evaluator;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class Driver {

	public static String CONFIG_FILE = "conf/test.properties";

	public static void main(String[] args) throws Exception {
		// Build configuration in code
		Configuration conf = new Configuration();
		conf.set("dfs.data.dir", "data/");
		conf.set("data.input.path", "filmtrust/rating/");
		conf.set("dfs.result.dir", "result/");
		conf.set("dfs.log.dir", "data/log/");
		conf.set("rec.recommender.similarity.key", "item");
		conf.setBoolean("rec.recommender.isranking", true); // Ranking or rating recommender
		conf.setInt("rec.recommender.ranking.topn", 10); // No. of recommendations to be generated per user
		conf.setInt("rec.similarity.shrinkage", 10);
		conf.setBoolean("rec.eval.enable", true);
		// Build dataModel with training and testing data
		TextDataModel dataModel = new TextDataModel(conf);
		dataModel.buildDataModel();
		System.out.print("Data Model ready \n");
		// Generate two files with training and testing data respectively
		new DataProcessing(dataModel).writeTestDataAndTrainData();
		// Build recommender context
		RecommenderContext context = new RecommenderContext(conf, dataModel);

		// Build similarity

		RecommenderSimilarity similarity = new CosineSimilarity();
		similarity.buildSimilarityMatrix(dataModel);
		context.setSimilarity(similarity);

		// Build recommender
		conf.set("rec.neighbors.knn.number", "200");
		//Recommender recommender = new ItemKNNRecommender();
		Recommender recommender = new BPRRecommender();
		recommender.setContext(context);
		// Run recommender algorithm
		recommender.train(context);
		
		// Save recommendations in a list
		List<RecommendedItem> recommendedListRank = null;
		List<RecommendedItem> recommendedListRating = null;
		recommendedListRank = recommender.getRecommendedList(recommender.recommendRank());
		recommendedListRating = recommender
				.getRecommendedList(recommender.recommendRating(context.getDataModel().getTestDataSet()));
		// Filter the recommendations generated (?)
		RecommendedFilter filter = new GenericRecommendedFilter();
		recommendedListRank = filter.filter(recommendedListRank);
		recommendedListRating = filter.filter(recommendedListRating);
		// Writing the list in a file
		String recommendationsRank = "result/recommendedListRank.dat";
		new Driver().writeResults(recommendationsRank, recommendedListRank);
		String recommendationsRate = "result/recommendedListRate.dat";
		new Driver().writeResults(recommendationsRate, recommendedListRating);

		// Evaluate the recommendations
		System.out.println("Second Recommend:");
		EvalContext evalContext = new EvalContext(conf, recommender, dataModel.getTestDataSet(),
				context.getSimilarity().getSimilarityMatrix(), context.getSimilarities());
		RecommenderEvaluator ndcgEvaluator = new NormalizedDCGEvaluator();
		//Escribir resultados automáticamente
		//Si se carga un archivo conf, en este no se invoca la función de guardar sino cuando se crea un objeto job
		//La función executeJob invoca a la función save result
		RecommenderJob job = new RecommenderJob(conf);
		job.saveResult(recommendedListRank);
		job.saveResult(recommendedListRating);

		/*
		 * RecommenderEvaluator rmseEvaluator = new RMSEEvaluator();
		 * rmseEvaluator.setTopN(10); System.out.println("rmse:"+
		 * rmseEvaluator.evaluate(evalContext));
		 */
		ndcgEvaluator.setTopN(10);

		double ndcgValue = ndcgEvaluator.evaluate(evalContext);
		System.out.println("ndcg:" + ndcgValue);
	}

	public void writeResults(String path, List<RecommendedItem> recommendedList) {
		File fileRate = new File(path);
		FileWriter frRate = null;
		try {
			frRate = new FileWriter(fileRate, true);
			for (int j = 0; j < recommendedList.size(); j++) {
				frRate.write(recommendedList.get(j).getUserId() + "\t");
				frRate.write(recommendedList.get(j).getItemId() + "\t");
				frRate.write(Double.toString(recommendedList.get(j).getValue()));
				frRate.write("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				frRate.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
