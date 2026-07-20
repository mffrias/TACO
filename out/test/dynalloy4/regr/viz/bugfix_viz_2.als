/* 
 * DynAlloy translator options 
 * --------------------------- 
 * assertionId= null
 * loopUnroll= 3
 * removeQuantifiers= false
 * strictUnrolling= false
 * build_dynalloy_trace= false
 */ 

module viz_problem_2
one sig null {}
abstract sig boolean {}
one sig true extends boolean {}
one sig false extends boolean {}
abstract sig java_lang_Throwable {}
{}
abstract sig java_lang_Object {}
{}
abstract sig slicing_koa_District extends java_lang_Object {}
{}
sig java_lang_String extends java_lang_Object {}
{}

pred TruePred[] {}


pred slicing_koa_DistrictCondition0[exit_stmt_reached: univ] {
  TruePred[]
}
pred postcondition_slicing_koa_District_name_0[
  my_kiesKring_DistrictInstance_pre:(slicing_koa_District)->one(null+slicing_koa_District), 
  my_kiesKring_DistrictInstance_post:(slicing_koa_District)->one(null+slicing_koa_District)  ] {

my_kiesKring_DistrictInstance_pre = my_kiesKring_DistrictInstance_post

}
check mi_assert



pred slicing_koa_District_name_0[
  throw_1: java_lang_Throwable + null,
  return_0: java_lang_String + null,
  return_1: java_lang_String + null,
  exit_stmt_reached_1: boolean,
  exit_stmt_reached_2: boolean
]{
  (
    throw_1=null)
  and 
  TruePred[]
  and 
  (
    exit_stmt_reached_1=false)
  and 
  (
    (
      slicing_koa_DistrictCondition0[exit_stmt_reached_1]
      and 
      (
        return_1=null)
      and 
      (
        exit_stmt_reached_2=true)
    )
    or 
    (
      (
        not (
          slicing_koa_DistrictCondition0[exit_stmt_reached_1])
      )
      and 
      TruePred[]
      and 
      (
        return_0=return_1)
      and 
      (
        exit_stmt_reached_1=exit_stmt_reached_2)
    )
  )

}



assert mi_assert{
all l0_exit_stmt_reached_1 :  boolean,
    l0_exit_stmt_reached_2 :  boolean,
    my_kiesKring_DistrictInstance_0 :  ( slicing_koa_District ) -> one ( null + slicing_koa_District ),
    return_0 :  java_lang_String + null,
    return_1 :  java_lang_String + null,
    throw_1 :  java_lang_Throwable + null | {
  (
    TruePred[]
    and 
    slicing_koa_District_name_0[throw_1,
                               return_0,
                               return_1,
                               l0_exit_stmt_reached_1,
                               l0_exit_stmt_reached_2]
  )
  implies 
          postcondition_slicing_koa_District_name_0[my_kiesKring_DistrictInstance_0,
                                                   my_kiesKring_DistrictInstance_0]
  }
}
