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
const val JOB_HISTORY_GRAPH_ROUTE = "job"
const val KESCO_GRAPH_ROUTE = "kesco"
const val SMS_GRAPH_ROUTE = "sms"
const val CHANGE_PASSWORD_GRAPH_ROUTE = "changePassword"

sealed class Screen(val route : String) {
    object Home : Screen(route = "home_screen")                     //main menu screen
    object Login : Screen(route = "login_screen")                   //login screen

    /**
     * 관리자 관리
     */
    object ManagerManagement : Screen(route = "manager_management") //관리자 관리 리스트
    object ManagerAdd : Screen(route = "manager_add")               //관리자 생성
    object ManagerEdit : Screen(route = "manager_edit/{userId}")               //관리자 수정
    object ManagerPasswordEdit : Screen(route = "manager_password_edit/{userId}")               //관리자 PW 변경
    object ManagerJobSearch : Screen(route = "manager_job_search/{userId}")               //관리자 작업 조회

    /**
     * 사이트 관리
     */
    object SiteManagement : Screen(route = "site_management") //site 관리 리스트
    object SiteAdd : Screen(route = "site_add") //site 생성
    object SiteEdit : Screen(route = "site_edit/{siteId}") //site 수정
    object SiteMPUAdd : Screen(route = "site_mpu_add/{siteId}/{siteName}") //site - mpu 추가
    object SiteMPUDelete : Screen(route = "site_mpu_delete/{siteId}/{siteName}") //site - mpu 삭제
    object SiteManagerAdd : Screen(route = "site_manager_add/{siteId}/{siteName}") //site - manager 추가
    object SiteManagerDelete : Screen(route = "site_manager_delete/{siteId}/{siteName}") //site - manager 삭제

    /**
     * MPU 관리
     */
    object MPUManagement : Screen(route = "mpu_management") //mpu 관리 리스트
    object MPUAdd : Screen(route = "mpu_add") //mpu 추가
    object MPUEdit : Screen(route = "mpu_edit/{mpuId}") //mpu 수정

    /**
     * 관리자 작업조회
     */
    object JobHistory : Screen(route = "job_history") //관리자 작업 조회

    object InquiryByManager: Screen(route = "inquiry_by_manager") //관리자별 작업조회
    object InquiryByControlHistory : Screen(route = "inquiry_by_control_history") //제어 이력 조회

    /**
     * Admin PW 변경
     */

    object ChangeAdminPassword : Screen(route = "change_admin_password") // Admin PW 변경

}