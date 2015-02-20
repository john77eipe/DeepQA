package deepqa.rest.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import com.avnet.deepqa.dto.AnsConfRefBean;
import com.avnet.deepqa.test.TestDeepQA;
import com.google.gson.Gson;

import deepqa.rest.bean.AnswerBean;

@Path("/service")
public class QAService {
	
	@Path("/fetchAns")
	@POST
	@Produces({ MediaType.TEXT_PLAIN })	
	@Consumes({MediaType.TEXT_PLAIN})
	public String fetchAnswers(String question){
		System.out.println("hit");
		System.out.println(question);
		cleanQue(question);
		AnsConfRefBean ansConfRefBean = TestDeepQA.askMe(question, null);
		System.out.println(ansConfRefBean);
		
		List<AnswerBean> answerBeanList = new ArrayList<AnswerBean>();
		convertDTOToRestBean(ansConfRefBean, answerBeanList);
		Collections.sort(answerBeanList);
		
		return new Gson().toJson(answerBeanList);
	}
	
	

	private void cleanQue(String question) {
		question = question.replaceAll("co-founder", "cofounder");
		question = question.replace("?", "");
		
	}



	private void convertDTOToRestBean(AnsConfRefBean ansConfRefBean,
			List<AnswerBean> answerBeanList) {
		TreeMap<String, Double> ansWithConf = ansConfRefBean.getAnsWithConf();
		Map<String, String> ansWithSent = ansConfRefBean.getAnsWithSentence();
		
		System.out.println(ansWithConf.keySet());
		System.out.println("jj:"+ansWithConf.get("Steve Jobs"));
		
        
		for(Entry<String, Double> ans: ansWithConf.entrySet()){
			AnswerBean answer = new AnswerBean();
			answer.setAnswer(ans.getKey().replaceAll("\"","").replaceAll("'", ""));
			answer.setConfidence(getInPercentage(ans.getValue()));
			answer.setSource((ansWithSent.get(ans.getKey())).replaceAll("\"","").replaceAll("'", "").replaceAll("\\P{Print}", ""));
			System.out.println(answer);
			answerBeanList.add(answer);
		}
	}
	
	private String getInPercentage(double value) {
		value = value*50;
		return Integer.toString((int)value);
		
	}

	@Path("/test")
	@POST
	@Produces({MediaType.TEXT_PLAIN})
	@Consumes({MediaType.TEXT_PLAIN})
	public String hello(String s){
		return "hello "+s;
	}
	
	@Path("/solr/index")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void indexDocument(MultivaluedMap<String,String> multivaluedMap){

		String title = multivaluedMap.get("title").get(0);
		String content = multivaluedMap.get("content").get(0);
		
		SolrServer server = new HttpSolrServer("http://localhost:8983/solr/");
		SolrInputDocument doc1 = new SolrInputDocument();
	    doc1.addField( "id", Integer.toString((int)(Math.random()*100)) );
	    doc1.addField( "title", title );
	    doc1.addField( "content", content );
	    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
	    docs.add( doc1 );
	    try {
			server.add(docs);
			UpdateResponse solrResponse = server.commit();
			System.out.println(solrResponse.getStatus());
		} catch (SolrServerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
}
