/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;

import gen.neo4jlib.neo4j_qry;
import java.util.List;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class mrca_attribution {
    @UserFunction
    @Description("gets list of persons rns in psth to common ancestor; excludes mrcas.")

    public String mrca_path_attribution(
        @Name("rn1") 
            Long rn1,
        @Name("rn2") 
            Long rn2
  )
   
         { 
             
        String s = get_attribution(rn1,rn2);
         return s;
            }

    
    
    public static void main(String args[]) {
        String lo = get_attribution(1L,600L);
        int fgg = 0;
    }
    
     public static String get_attribution(Long rn1, Long rn2) 
    {   
            Long rnmin;
            Long rnmax;
            if (rn1<rn2){
                rnmin=rn1;
                rnmax=rn2;
            }
            else {
                rnmin=rn2;
                rnmax=rn1;
            }
        String cq = "match path = (p:Person{RN:" + rnmin + "})-[r1:father|mother*0..15]->(mrca:Person)<-[r2:father|mother*0..15]-(b:Person{RN:" + rnmax + "}) where p.RN<b.RN with mrca, [x in nodes(path)|x.RN] as xx,collect(distinct mrca.RN) as rns with apoc.coll.disjunction(xx,rns) as rnd return distinct rnd";
        String lo = gen.neo4jlib.neo4j_qry.qry_str(cq).replace("[[","").replace("]]","");
        //System.out.println(lo);
        return lo;
    }
}
