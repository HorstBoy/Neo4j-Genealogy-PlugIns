/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.rel;
    import gen.neo4jlib.neo4j_qry;

    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;
    import org.neo4j.procedure.Description;
     import gen.conn.connTest;
import java.util.Collections;
 
    import java.util.List;        
      

public class mrca_from_cypher_List {          
    @UserFunction
    @Description("Returns most recent common ancestor shared by multiple persons. Each pair of persons in the list may have a more recent common ancestor who is descended from the reported ancestor shared by all in the list. Returns nothing if there is none.")
        
    public String mrca_from_cypher_list(
        @Name("rn_list") 
            List rn_list,
        @Name("generations") 
            Long generations
    )

        {
        String qry = "match (c:Person) where c.RN in " + rn_list + " With c order by c.RN With collect(distinct c.RN) As cc match (c2:Person)-[:father|mother*0.." + generations + "]->(MRCA:Person)<-[:father|mother*0.." + generations + "]-(c3:Person) where c2.RN in cc And c3.RN in cc  and c2.RN<>c3.RN with MRCA,cc,c2 order by c2.RN with MRCA,cc,collect(distinct c2.RN) as cc2 order by MRCA.sex desc with distinct cc,cc2,MRCA.fullname + ' ⦋' + MRCA.RN + '⦌ (' + left(MRCA.BD,4) + '-' + left(MRCA.DD,4) + ')' as CommonAncestor where cc2=cc return CommonAncestor";
        try{
            List r =mrca_from_list_qry(qry,rn_list);
            Collections.sort(r);
            String s = gen.genlib.listStrToStr.list_to_string(r);
            return s;
        }
        catch(Exception e){return "-";}
            }
    
   
    public List<String> mrca_from_list_qry(String qry,List rn_list) 
    {
     return neo4j_qry.qry_str_list(qry);
}
              
}