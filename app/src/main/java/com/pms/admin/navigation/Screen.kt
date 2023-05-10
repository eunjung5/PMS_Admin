package com.pms.admin.navigation

//argument defined
const val DETAIL_ARGUMENT_KEY_ID = "id"

//route defined
const val ROOT_GRAPH_ROUTE = "root"
const val AUTH_GRAPH_ROUTE = "auth"
const val HOME_GRAPH_ROUTE = "home"
const val MANAGER_GRAPH_ROUTE = "manager"
const val SITE_GRAPH_ROUTE = "site"
const val MPU_GRAPH_ROUTE = "mpu"

sealed class Screen(val route : String) {
    object Home : Screen(route = "home_screen")                     //main menu screen
    object Login : Screen(route = "login_screen")                   //login screen
    object ManagerManagement : Screen(route = "manager_management") //관리자 관리 리스트
    object ManagerAdd : Screen(route = "manager_add")               //관리자 생성
    object ManagerEdit : Screen(route = "manager_edit/{userId}")               //관리자 수정
    object ManagerPasswordEdit : Screen(route = "manager_password_edit/{userId}")               //관리자 PW 변경
    object ManagerJobSearch : Screen(route = "manager_job_search/{userId}")               //관리자 작업 조회
}