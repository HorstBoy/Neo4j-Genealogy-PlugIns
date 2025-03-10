/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.gedcom;

import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;
import gen.neo4jlib.neo4j_info;
  
public class get_family_tree_data {
    @UserFunction
    @Description("Create a list of Persons their RN and kit number for curation.")


    public String person_from_rn(
        @Name("rn") 
            Long rn,
        @Name("UnicodeBrackets") 
            Boolean UnicodeBrackets
    )

        {

        String r =getPersonFromRN(rn,UnicodeBrackets);
        
        return r;
            }
    

    public static void main(String args[]) {
        // TODO code application logic here
    }
    
    public static String getPersonFromRN(Long rn,Boolean UnicodeBrackets) {
        String s;
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        try{
        if (UnicodeBrackets==true){
            s = gen.neo4jlib.neo4j_qry.qry_str("match (p:Person{RN:" + rn + "}) with p.fullname + ' ⦋' + p.RN + '⦌ (' + right(p.BDGed,4) +'-' + right(p.DDGed,4) + ')' as name return name").replace("[", "").replace("]", "").replace("\"", "");
        }
        else {
            s = gen.neo4jlib.neo4j_qry.qry_str("match (p:Person{RN:" + rn + "}) with p.fullname + ' [' + p.RN + '] (' + right(p.BDGed,4) +'-' + right(p.DDGed,4) + ')' as name return name");
           
        }
        return s.replace("\"", "");  //   .replace("[", "").replace("]", "").replace("\"", "");
        }
        catch(Exception e){return"";}
//return "xxx";
}
}
