/* 
 * DynAlloy translator options 
 * --------------------------- 
 * assertionId= null
 * loopUnroll= 3
 * removeQuantifiers= false
 * strictUnrolling= false
 * build_dynalloy_trace= false
 */ 

module test
one sig null {}
abstract sig java_lang_Throwable {}
{}
abstract sig java_lang_Object {}
{}
abstract sig java_util_Map extends java_lang_Object {}
{}
sig ar_edu_dynjml4alloy_builtin_TreeMapTest extends java_lang_Object {}
{}
sig java_lang_String extends java_lang_Object {}
{}
sig java_util_TreeMap extends java_util_Map {}
{}


-- functions

fun fun_map_get[
  map: univ -> univ, 
  k: univ
]: univ { 
  (some k.map) => k.map else null 
} 

fun fun_map_put[
  map: univ->univ, 
  k: univ, 
  v: univ
]: univ-> univ { 
  map ++ (k->v) 
}

--- predicates

pred TruePred[] {}

pred updateMapPost[
  f1:univ->univ->univ,
  f0:univ->univ->univ,
  map:univ, 
  entries:univ->univ
]{ 
  f1 = f0 ++ (map->entries) 
} 

pred equ[l,r:univ] { l=r }

-- actions 
-- programs
check mi_assert



pred updateMap[
  Map_entries_0: java_util_Map -> univ -> univ,
  Map_entries_1: java_util_Map -> univ -> univ,
  map_0: java_util_Map,
  entries_0: univ -> univ
]{
  TruePred[]
  and 
  updateMapPost[Map_entries_1,
               Map_entries_0,
               map_0,
               entries_0]
}


pred java_util_Map_put_0[
  thiz_0: java_util_Map,
  throw_1: java_lang_Throwable + null,
  return_1: java_lang_Object,
  k_0: java_lang_Object + null,
  v_0: java_lang_Object + null,
  Map_entries_0: ( java_util_Map ) -> ( univ -> lone ( univ ) ),
  Map_entries_1: ( java_util_Map ) -> ( univ -> lone ( univ ) )
]{
  (
    throw_1=null)
  and 
  (
    return_1=fun_map_get[thiz_0.Map_entries_0,k_0])
  and 
  updateMap[Map_entries_0,
           Map_entries_1,
           thiz_0,
           fun_map_put[thiz_0.Map_entries_0,k_0,v_0]]

}



pred mi_program[
  thiz_0: ar_edu_dynjml4alloy_builtin_TreeMapTest,
  throw_1: java_lang_Throwable + null,
  myMap_0: ( ar_edu_dynjml4alloy_builtin_TreeMapTest ) -> one ( java_util_TreeMap + null ),
  Map_entries_0: ( java_util_Map ) -> ( univ -> lone ( univ ) ),
  Map_entries_1: ( java_util_Map ) -> ( univ -> lone ( univ ) ),
  es_var__78_0: java_lang_String + null,
  es_var__79_1: java_lang_Object + null,
  es_var__77_0: java_lang_String + null
]{
  java_util_Map_put_0[thiz_0.myMap_0,
                     throw_1,
                     es_var__79_1,
                     es_var__77_0,
                     es_var__78_0,
                     Map_entries_0,
                     Map_entries_1]

}



assert mi_assert{
all Map_entries_0 :  ( java_util_Map ) -> ( univ -> lone ( univ ) ),
    Map_entries_1 :  ( java_util_Map ) -> ( univ -> lone ( univ ) ),
    l1_es_var__77_0 :  java_lang_String + null,
    l1_es_var__78_0 :  java_lang_String + null,
    l1_es_var__79_1 :  java_lang_Object + null,
    myMap_0 :  ( ar_edu_dynjml4alloy_builtin_TreeMapTest ) -> one ( java_util_TreeMap + null ),
    thiz_0 :  ar_edu_dynjml4alloy_builtin_TreeMapTest,
    throw_1 :  java_lang_Throwable + null | {
  (
    TruePred[]
    and 
    mi_program[thiz_0,
              throw_1,
              myMap_0,
              Map_entries_0,
              Map_entries_1,
              l1_es_var__78_0,
              l1_es_var__79_1,
              l1_es_var__77_0]
  )
  implies 
          equ[thiz_0,
             null]
  }
}
