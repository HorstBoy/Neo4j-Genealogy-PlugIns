/**
 * Copyright 2021-2023 
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

/**
 *
 * @author david
 */
public class mrca_path_lengths {
    @UserFunction
    @Description("Returns string with number of mrcas and paths lengths to common ancestor. This can be used to look up the relationship and expected shared centimorgans.")

    public String mrca_path_len(
        @Name("rn1") 
            Long rn1,
        @Name("rn2") 
            Long rn2
  )
   
         { 
             
        String s = get_mrca_path_len(rn1,rn2);
         return s;
            }

        public String get_mrca_path_len(Long rn1, Long rn2){
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
                String s =gen.neo4jlib.neo4j_qry.qry_to_csv("match path = (p:Person{RN:" + rnmin + "})-[r1:father|mother*0..15]->(mrca:Person)<-[r2:father|mother*0..15]-(b:Person{RN:" + rnmax + "}) where p.RN<b.RN return mrca.RN as mrca, size(r1) as path1,size(r2) as path2");
            //gen.neo4jlib.file_lib.writeFile(s, "c://temp/mrcatest.csv");
           return s; 
        }
}
