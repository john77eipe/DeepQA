package deepqa.test;

import java.util.ArrayList;
import java.util.Iterator;

import org.opencyc.api.CycAccess;
import org.opencyc.api.CycObjectFactory;
import org.opencyc.cycobject.CycConstant;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycVariable;

public class TestCYC {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

class OpencycController
{
      CycAccess _cycaccess;
      /** Creates a new instance of OpencycController */
      public OpencycController() throws java.io.IOException
      {
            try
            {
 
                  _cycaccess = new CycAccess("192.168.0.31",3600);
 
                  _cycaccess.traceOn();
            }
            catch (java.io.IOException err)
            {
                  throw (err);
            }
      }
      public ArrayList getLivingLanguages() throws java.io.IOException
      {
 
            CycVariable languageVariable = null;
            CycList response = null;
            ArrayList results = null;
            CycList query = _cycaccess.current().makeCycList("(#$isa ?X #$LivingLanguage)");
            languageVariable = CycObjectFactory.makeCycVariable("?X");
 
            try
            {
                  CycConstant mt = this._cycaccess.getConstantByName("InferencePSC");
                  response = _cycaccess.current().askWithVariable(query,languageVariable, _cycaccess.inferencePSC);
                  results = new java.util.ArrayList();
                  Iterator iterator = response.iterator();
                  while (iterator.hasNext())
                  {
                        CycConstant item = (CycConstant)iterator.next();
                        results.add(item.getName());
                  }
                  return results;
            }
            catch (java.io.IOException err)
            {
                  throw (err);
            }
      }
      
}